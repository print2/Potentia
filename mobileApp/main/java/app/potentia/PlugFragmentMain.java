package app.potentia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PlugFragmentMain extends Fragment{

    public PlugFragmentMain() {
        // Required empty public constructor
    }

    private View inflatedView;
    private ListView plugList;
    private appDriver appDriver = new appDriver();

    private ArrayList <plugProfile> allPlugs = new ArrayList<plugProfile>();

    private ArrayList <String> plugNames = new ArrayList<String>();
    private ArrayList <String> plugAppliances = new ArrayList<String>();
    private ArrayList <String> plugTimers = new ArrayList<String>();
    private ArrayList <Boolean> plugPowers = new ArrayList<Boolean>();

//    private String plugNames[];
//    private String plugAppliances[];
//    private String plugTimers[];
//    private Boolean plugPowers[];

    private TextView test;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_plugmain, container, false);

        applianceProfile fridge = new applianceProfile("Fridge", true);
        plugProfile plug1 = new plugProfile("Plug1", fridge);
        allPlugs.add(plug1);

        plugList = inflatedView.findViewById(R.id.plugList);
        CustomAdapter adapter = new CustomAdapter(inflatedView.getContext(), allPlugs);
        plugList.setAdapter(adapter);

        return inflatedView;
    }

    public static class CustomAdapter extends BaseAdapter {
        Context context;
        private ArrayList <plugProfile> allPlugs = new ArrayList<plugProfile>();
        LayoutInflater inflater;

        public CustomAdapter(Context applicationContext, ArrayList <plugProfile> allPlugs) {
            this.context = applicationContext;
            this.allPlugs = allPlugs;
            this.inflater = LayoutInflater.from(applicationContext);
        }

        @Override
        public int getCount() {
            return allPlugs.size();
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
            //TODO
            //add connected button

            view = inflater.inflate(R.layout.pluglist_view, viewGroup, false);
            TextView name = (TextView) view.findViewById(R.id.plugName);
            TextView appliance = (TextView) view.findViewById(R.id.applianceName);
            TextView timer = (TextView) view.findViewById(R.id.timer);

            plugProfile x = allPlugs.get(i);

            String as = "Appliance: " + x.getAppliance().getName();
            String ts = "";

            if(x.getAppliance().getPermOn()){
                ts = "Timer: Always On";
            } else if(!x.getAppliance().getPermOn()){
                ts = "Timer: None";
            }

            name.setText(x.getName());
            appliance.setText(as);
            timer.setText(ts);

            return view;
        }
    }
}