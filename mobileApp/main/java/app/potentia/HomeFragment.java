package app.potentia;

import android.os.Handler;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    public HomeFragment() {
        // Required empty public constructor
    }

    private View inflatedView;
    private Spinner dropdown;
    private TextView currentUsage;
    private String reading;

    private appDriver appDriver = new appDriver();
    private ArrayList<String> connectedNameList = new ArrayList<String>();
    private plugProfile currentPlug;

    private Async update;
    private Timer timer;
    private TimerTask task;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflatedView = inflater.inflate(R.layout.fragment_home, container, false);

        appDriver = ((MainActivity) getActivity()).getAppDriver();
        connectedNameList = appDriver.getConnectedProfiles();

        if(connectedNameList.size() > 0){
            currentPlug = appDriver.getPlugByName(connectedNameList.get(0));
        } else {
            StarterFragment starterFragment = new StarterFragment();
            ((MainActivity) getActivity()).switchFragment(starterFragment);
        }
        currentUsage = inflatedView.findViewById(R.id.currentUsage);
        dropdown = inflatedView.findViewById(R.id.dropdown);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflatedView.getContext(), android.R.layout.simple_spinner_item, connectedNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        return inflatedView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        ((MainActivity) getActivity()).stopAsyncReadings();
        currentAsyncTask(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //gets list of names of connected plugs
    //gets current reading of selected
    public class Async extends AsyncTask<Integer, Void, String>{
        @Override
        protected String doInBackground(Integer... params) {
            currentPlug = appDriver.getPlugByName(connectedNameList.get(params[0]));
            reading = currentPlug.retrieveCurrUsage() + " W";
            return reading;
        }
        @Override
        protected void onPostExecute(String result){
            currentUsage.setText(result);
        }
    }

    //run Async every sec
    private void currentAsyncTask(int i) {

        final Handler handler = new Handler();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        update = (Async) new Async().execute(i);
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }

    public plugProfile getCurrentPlug(){
        return currentPlug;
    }

    public AsyncTask getAsync(){
        return  update;
    }

    public Timer getTimer(){
        return timer;
    }

    public TimerTask getTimerTask(){
        return task;
    }

}
