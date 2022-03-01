package app.potentia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PlugFragmentMain extends Fragment{

    public PlugFragmentMain() {
        // Required empty public constructor
    }

    CreatePlugFragment createPlugFragment = new CreatePlugFragment();

    private View inflatedView;
    private ListView listView;
    private appDriver appDriver = new appDriver();

    private ArrayList <plugProfile> allPlugs = new ArrayList<plugProfile>();
    private ArrayList <applianceProfile> allAppliances = new ArrayList<applianceProfile>();

    private FloatingActionButton add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_plugmain, container, false);

        allAppliances = appDriver.getApplianceList();

        //testing
        plugProfile plug1 = new plugProfile("Plug1", allAppliances.get(0));
        plugProfile plug2 = new plugProfile("Plug2", allAppliances.get(1));
        appDriver.addPlugProfile(plug1);
        appDriver.addPlugProfile(plug2);

        allPlugs = appDriver.getPlugList();

        listView = inflatedView.findViewById(R.id.plugList);
        CustomAdapter adapter = new CustomAdapter(inflatedView.getContext(), allPlugs);
        listView.setAdapter(adapter);

        add = inflatedView.findViewById(R.id.fab);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).forwardFragment(createPlugFragment);
            }
        });

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
            TextView name = view.findViewById(R.id.plugName);
            TextView appliance = view.findViewById(R.id.applianceName);
            TextView timer = view.findViewById(R.id.timer);

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