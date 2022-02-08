package app.potentia;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment implements View.OnClickListener{

    public HomeFragment() {
        // Required empty public constructor
    }

    private View inflatedView;
    private Button button;
    private TextView currentUsage;
    private String string;

    private plugProfile plug = new plugProfile("name");

    private Handler handler = new Handler();

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
        currentUsage.setVisibility(currentUsage.VISIBLE);
        Async update = (Async) new Async().execute(plug);
    }

    public class Async extends AsyncTask<plugProfile, Void, Void>{
        
        @Override
        protected Void doInBackground(plugProfile... plug) {
            handler.post(updateCurrent);
            return null;
        }

    }

    Runnable updateCurrent = new Runnable(){
        public void run() {
            string = plug.retrieveCurrUsage();
            currentUsage.setText(string);
            handler.postDelayed(this, 1000);
        }
    };
}
