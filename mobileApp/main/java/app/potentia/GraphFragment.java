package app.potentia;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphFragment extends Fragment {

    public GraphFragment() {
        // Required empty public constructor
    }

    private View inflatedView;
    private TabLayout tabLayout;
    private plugProfile currentPlug;

    private LineGraphSeries<DataPoint> series;
    private GraphView graph;
    private double x, y;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflatedView = inflater.inflate(R.layout.fragment_graph, container, false);


        graph = inflatedView.findViewById(R.id.graph);
        tabLayout = inflatedView.findViewById(R.id.tabs);
        createGraph();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                createGraph();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


        return inflatedView;
    }

    public void createGraph(){
        series = new LineGraphSeries<>();
        x = -5.0;
        for(int i = 0; i < 500; i++){
            x = x + 0.1;
            y = Math.sin(x);
            series.appendData(new DataPoint(x,y), true, 500);
        }
        graph.addSeries(series);
    }
}