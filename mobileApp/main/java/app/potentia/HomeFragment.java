package app.potentia;

import android.annotation.SuppressLint;
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
    private ArrayList<String> connectedList = new ArrayList<String>();
    private plugProfile currentPlug = new plugProfile("Smart Plug 1");
    private String currentName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflatedView = inflater.inflate(R.layout.fragment_home, container, false);

        currentPlug.setIP("192.168.43.28");

        currentAsyncTask(currentPlug);

        currentUsage = inflatedView.findViewById(R.id.currentUsage);
        dropdown = inflatedView.findViewById(R.id.dropdown);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflatedView.getContext(), android.R.layout.simple_spinner_item, connectedList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        return inflatedView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        currentName = connectedList.get(position);
        currentPlug = appDriver.getPlugByName(currentName);

        currentAsyncTask(currentPlug);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        currentAsyncTask(currentPlug);
    }

    public class Async extends AsyncTask<plugProfile, Void, String>{

        @Override
        protected String doInBackground(plugProfile... params) {
            connectedList = appDriver.getConnectedPlugs();
            reading = currentPlug.retrieveCurrUsage() + " W";
            return reading;
        }

        @Override
        protected void onPostExecute(String result){
            currentUsage.setText(result);
        }
    }

    private void currentAsyncTask(plugProfile plug) {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        Async update = (Async) new Async().execute(plug);
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }

}
