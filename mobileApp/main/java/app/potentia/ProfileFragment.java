package app.potentia;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    private static final String ARG_PLUG_NAME = "plugName";
    private String mPlugName;

    private View inflatedView;
    private appDriver appDriver;
    private ConnectFragment connectFragment;

    private plugProfile thisPlug;
    private TextView name;
    private TextView description;
    private TextView appliance;
    private TextView timer;
    private TextView status;
    private TextView ip;
    private ImageView back;
    private ImageView edit;
    private ImageView delete;
    private ImageView power;
    private Button connect;

    public ProfileFragment(String plugName) {
        this.mPlugName = plugName;
    }

    public static ProfileFragment newInstance(String mPlugName) {
        ProfileFragment fragment = new ProfileFragment(mPlugName);
        Bundle args = new Bundle();
        args.putString(ARG_PLUG_NAME, mPlugName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlugName = getArguments().getString(ARG_PLUG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_profile, container, false);

        appDriver = ((MainActivity) getActivity()).getAppDriver();
        thisPlug = appDriver.getPlugByName(mPlugName);

        //back button
        back = inflatedView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        name = inflatedView.findViewById(R.id.smName);
        description = inflatedView.findViewById(R.id.smDesc);
        appliance = inflatedView.findViewById(R.id.smAppl);
        timer = inflatedView.findViewById(R.id.smTimer);
        status = inflatedView.findViewById(R.id.smStatus);
        ip = inflatedView.findViewById(R.id.smIP);

        edit = inflatedView.findViewById(R.id.edit);
        delete = inflatedView.findViewById(R.id.delete);
        power = inflatedView.findViewById(R.id.power);

        connect = inflatedView.findViewById(R.id.connect);

        //profile info
        name.setText(mPlugName);
        if(thisPlug.getDescription() != null){
            description.setText(thisPlug.getDescription());
        }
        if(thisPlug.getAppliance() != null){
            appliance.setText(thisPlug.getAppliance().getName());
            if(thisPlug.getAppliance().getPermOn()){
                timer.setText("Always On");
            } else {
                if (thisPlug.getAppliance().getTimeUntilDisable() > 0){
                    timer.setText(thisPlug.getAppliance().getTimeUntilDisable() + " min");
                } else {
                    timer.setText("None");
                }
            }
        } else {
            appliance.setText("None");
            timer.setText("None");
        }

        //connections
        if(thisPlug.getConnected()){
            status.setText("Connected");
            ip.setText(thisPlug.getIP());
            connect.setVisibility(View.GONE);
        } else {
            status.setText("Not connected");
            ip.setText("N/A");
            connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    connectFragment = new ConnectFragment(mPlugName);
                    ((MainActivity) getActivity()).forwardFragment(connectFragment);
                }
            });

        }

        //delete
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(inflatedView.getContext());
                builder.setMessage("Are you sure you want to delete " + thisPlug.getName() + "?");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new deletePlugAsync().execute(thisPlug);
                                ((MainActivity) getActivity()).onBackPressed();
                            }
                        });
                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        //power
        updatePower();
        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thisPlug.getConnected()){
                    new powerAsync().execute();
                    updatePower();
                }
            }
        });


        return inflatedView;
    }

    public class powerAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            thisPlug.togglePower();
            return "Done";
        }
        @Override
        protected void onPostExecute(String result){
            updatePower();
        }
    }

    public void updatePower(){
        if(thisPlug.isOn()){
            power.setColorFilter(getResources().getColor(R.color.teal_700));
        } else {
            power.setColorFilter(getResources().getColor(R.color.grey));
        }
    }

    public class deletePlugAsync extends AsyncTask<plugProfile, Void, String> {
        @Override
        protected String doInBackground(plugProfile... params) {
            appDriver.removePlugProfile(params[0]);
            return "Done";
        }
        @Override
        protected void onPostExecute(String result){
        }
    }
}