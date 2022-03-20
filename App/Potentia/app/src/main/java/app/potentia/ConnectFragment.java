package app.potentia;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectFragment extends Fragment{

    private static final String ARG_PLUG_NAME = "plugName";
    private String mPlugName;

    private View inflatedView;
    private ListView listView;
    private ImageView back;
    private TextView text1;
    private TextView text2;
    private appDriver appDriver;
    private plugProfile thisPlug; //plug to connect
    private ArrayList<String> unConnected = new ArrayList<>();


    public ConnectFragment(String plugName) {
        this.mPlugName = plugName;
    }

    public static ConnectFragment newInstance(String plugName) {
        ConnectFragment fragment = new ConnectFragment(plugName);
        Bundle args = new Bundle();
        args.putString(ARG_PLUG_NAME, plugName);
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
        inflatedView = inflater.inflate(R.layout.fragment_connect, container, false);

        appDriver = ((MainActivity) getActivity()).getAppDriver();
        thisPlug = appDriver.getPlugByName(mPlugName);

        text1 = inflatedView.findViewById(R.id.connectText1);
        text2 = inflatedView.findViewById(R.id.connectText2);
        text1.setText("Connect to " + mPlugName);

        listView = inflatedView.findViewById(R.id.unconnectedList);
        runAsync();

        //back button
        back = inflatedView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        return inflatedView;
    }

    //get list of unconnected plugs
    public class unconnectedAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try{
                unConnected = appDriver.getUnconnectedPlugs();
            } catch (Exception e){

            }

            return "Done";
        }
        @Override
        protected void onPostExecute(String result){
            if(unConnected.size()>0){
                text2.setText("Unconnected Smart Plugs found: ");
            } else {
                text2.setText("No Smart Plugs Found");
            }

            CustomAdapter adapter = new CustomAdapter(inflatedView.getContext(), unConnected);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //dialog alert to connect
                    AlertDialog.Builder builder = new AlertDialog.Builder(inflatedView.getContext());
                    builder.setMessage("Are you sure you want to connect " + mPlugName + " to " + unConnected.get(position) + "?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    connectAsync connectAsync = new connectAsync();
                                    connectAsync.execute(position);
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

        }
    }

    //connect selected plug
    public class connectAsync extends AsyncTask<Integer, Void, String> {

        private ProgressDialog mProgress;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mProgress = new ProgressDialog(inflatedView.getContext());
            mProgress.setMessage("Connecting...");
            mProgress.show();
        }

        @Override
        protected String doInBackground(Integer... params) {
            thisPlug.connectPlug("8b8389fb", appDriver.getNetwork(), unConnected.get(params[0]));
            return "Done";
        }
        @Override
        protected void onPostExecute(String result){
            mProgress.dismiss();
            ((MainActivity) getActivity()).onBackPressed();
        }
    }

    //run unconnectedAsync
    private void runAsync() {

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            public void run() {
                new unconnectedAsync().execute();
            }
        });
    }

    //adapter to display plug list
    public static class CustomAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        ArrayList <String> unConnected;

        public CustomAdapter(Context applicationContext, ArrayList <String> unConnected) {
            this.context = applicationContext;
            this.inflater = LayoutInflater.from(applicationContext);
            this.unConnected = unConnected;
        }

        @Override
        public int getCount() {
            return unConnected.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = inflater.inflate(R.layout.unconnectedlist_view, viewGroup, false);
            TextView name = view.findViewById(R.id.plugName);
            name.setText(unConnected.get(i));

            return view;
        }
    }

}