package app.potentia;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GraphFragment extends Fragment {

    private View inflatedView;
    private TabLayout tabLayout;

    private appDriver appDriver;
    private plugProfile currentPlug;
    private LineGraphSeries<DataPoint> series;
    private GraphView graph;
    private String timeSpace;
    private ArrayList<Integer> timePoints;
    private ArrayList<String> dataPoints;
    private double x, y;

    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflatedView = inflater.inflate(R.layout.fragment_graph, container, false);

        appDriver = ((MainActivity) getActivity()).getAppDriver();
        currentPlug = ((MainActivity) getActivity()).getCurrentPlug();

        graph = inflatedView.findViewById(R.id.graph);
        tabLayout = inflatedView.findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                timeSpace = tab.getText().toString();
                new graphAsync().execute(timeSpace);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return inflatedView;
    }

    public class graphAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            timePoints = appDriver.getGraphTimePoints(params[0]);
            dataPoints = appDriver.getGraphDataPoints(currentPlug, params[0]);
            return params[0];
        }
        @Override
        protected void onPostExecute(String result){
            createGraph();
        }
    }

    public void createGraph(){
        graph.removeAllSeries();
        graph.getViewport().setMinX(timePoints.get(0));
        graph.getViewport().setMaxX(timePoints.get(timePoints.size()));
        graph.getViewport().setXAxisBoundsManual(true);
        series = new LineGraphSeries<>();
        for(int i = 0; i < timePoints.size(); i++){
            if(timePoints.size() == dataPoints.size()){
                x = timePoints.get(i);
//                x = i;
                y = Double.parseDouble(dataPoints.get(i));
                series.appendData(new DataPoint(x,y), true, timePoints.size());
            }
        }
        graph.addSeries(series);
    }
}