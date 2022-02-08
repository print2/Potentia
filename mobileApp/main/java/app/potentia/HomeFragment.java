package app.potentia;

import static java.lang.Thread.sleep;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment implements View.OnClickListener{

    public HomeFragment() {
        // Required empty public constructor
    }

    private View inflatedView;
    private Button button;
    private TextView currentUsage;
    private String string;

    public plugProfile plug = new plugProfile("name");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflatedView = inflater.inflate(R.layout.fragment_home, container, false);

        button = inflatedView.findViewById(R.id.button);
        button.setOnClickListener(this);

        currentUsage = inflatedView.findViewById(R.id.currentUsage);
        currentUsage.setOnClickListener(this);

        return inflatedView;
    }

    @Override
    public void onClick(View view) {
        plug.setIP("192.168.43.28");
        currentUsage.setVisibility(currentUsage.VISIBLE);

        currUsageAsyncTask();
    }

    public class Async extends AsyncTask<plugProfile, Void, String>{

        @Override
        protected String doInBackground(plugProfile... params) {
            string = plug.retrieveCurrUsage();
            return string;
        }

        @Override
        protected void onPostExecute(String result){
            currentUsage.setText(result);
        }

    }

    private void currUsageAsyncTask() {

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
