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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    //need function to return plug object from given name

    public HomeFragment() {
        // Required empty public constructor
    }

    private View inflatedView;
//    private Button button;
    private Spinner dropdown;
    private TextView currentUsage;
    private String string;

    private appDriver appDriver = new appDriver();
    private ArrayList<String> connected = new ArrayList<String>();

    public plugProfile currentPlug;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflatedView = inflater.inflate(R.layout.fragment_home, container, false);

//        button = inflatedView.findViewById(R.id.button);
//        button.setOnClickListener(this);
        connected.add("Smart Plug 1");

        //connected = appDriver.getConnectedPlugs();
        currentUsage = inflatedView.findViewById(R.id.currentUsage);
        dropdown = inflatedView.findViewById(R.id.dropdown);
        currentUsage.setText("*** kWh");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflatedView.getContext(), android.R.layout.simple_spinner_item, connected) ; //?
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        return inflatedView;
    }

//    @Override
//    public void onClick(View view) {
//        currentPlug.getIP();
//        currUsageAsyncTask(currentPlug);
//    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                break;

        }
    }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //currUsageAsyncTask(currentPlug);
        }

    public class Async extends AsyncTask<plugProfile, Void, String>{

        @Override
        protected String doInBackground(plugProfile... params) {
            string = currentPlug.retrieveCurrUsage();
            return string;
        }

        @Override
        protected void onPostExecute(String result){
            currentUsage.setText(result);
        }

    }

    private void currUsageAsyncTask(plugProfile plug) {

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
