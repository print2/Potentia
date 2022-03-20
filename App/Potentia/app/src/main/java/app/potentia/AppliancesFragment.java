package app.potentia;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AppliancesFragment extends Fragment {

    private View inflatedView;
    private ListView listView;
    private appDriver appDriver;

    private CreateApplFragment createApplFragment;

    private ArrayList<applianceProfile> allAppliances = new ArrayList<applianceProfile>();
    private FloatingActionButton add;
    private ImageView back;

    private Boolean d = false;

    public AppliancesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inflatedView = inflater.inflate(R.layout.fragment_appliances, container, false);

        appDriver = ((MainActivity) getActivity()).getAppDriver();
        allAppliances = appDriver.getApplianceListN();

        listView = inflatedView.findViewById(R.id.applianceList);
        CustomAdapter adapter = new CustomAdapter(inflatedView.getContext(), allAppliances, appDriver);
        listView.setAdapter(adapter);

        //back button
        back = inflatedView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        //add button
        add = inflatedView.findViewById(R.id.fab);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createApplFragment = new CreateApplFragment();
                ((MainActivity) getActivity()).forwardFragment(createApplFragment);
            }
        });

        return inflatedView;
    }

    public class CustomAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        appDriver appDriver;
        ArrayList <applianceProfile> applianceList;

        public CustomAdapter(Context applicationContext, ArrayList <applianceProfile> applianceList, appDriver appDriver) {
            this.context = applicationContext;
            this.inflater = LayoutInflater.from(applicationContext);
            this.applianceList = applianceList;
            this.appDriver = appDriver;
        }

        @Override
        public int getCount() {
            return applianceList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public class deleteApplianceAsync extends AsyncTask<applianceProfile, Void, String> {
            @Override
            protected String doInBackground(applianceProfile... params) {
                appDriver.removeAppliance(params[0]);
                return "Done";
            }
            @Override
            protected void onPostExecute(String result){
            }
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = inflater.inflate(R.layout.appliancelist_view, viewGroup, false);

            TextView name = view.findViewById(R.id.applianceName);
            TextView timer = view.findViewById(R.id.timer);
            ImageView edit = view.findViewById(R.id.edit);
            ImageView delete = view.findViewById(R.id.delete);

            applianceProfile x = applianceList.get(i);

            name.setText(x.getName());
            if(x.getPermOn()){
                timer.setText("Timer: Always On");
            } else {
                if (x.getTimeUntilDisable() >= 0){
                    timer.setText("Timer: " + x.getTimeUntilDisable() + " min");
                } else {
                    timer.setText("Timer: None");
                }
            }

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to delete " + x.getName() + "?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new deleteApplianceAsync().execute(x);
                                    notifyDataSetChanged();
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

            return view;
        }
    }
}