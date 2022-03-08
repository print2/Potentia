package app.potentia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

//TODO
//add onSelectListener
public class ConnectFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final String ARG_PLUG_NAME = "plugName";
    private String mPlugName;

    private View inflatedView;
    private ListView listView;
    private ImageView back;
    private TextView text;

    private appDriver appDriver = new appDriver();
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

        text = inflatedView.findViewById(R.id.connectText1);
        text.setText("Connect to " + mPlugName);

        thisPlug = appDriver.getPlugByName(mPlugName);
        //new getUnconnectedListAsync().execute("");

        //test names
        unConnected.add("mPlugName");
        unConnected.add("mPlugName");

        listView = inflatedView.findViewById(R.id.unconnectedList);
        CustomAdapter adapter = new CustomAdapter(inflatedView.getContext(), unConnected);
        listView.setAdapter(adapter);

        back = inflatedView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        return inflatedView;
    }

    @SuppressLint("StaticFieldLeak")
    public class getUnconnectedListAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            unConnected = appDriver.getUnconnectedPlugs();
            return "Done";
        }
        @Override
        protected void onPostExecute(String result){

        }
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}


}