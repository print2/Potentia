package app.potentia;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class CreatePlugFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public CreatePlugFragment() {
        // Required empty public constructor
    }

    private View inflatedView;
    private appDriver appDriver;

    private AppliancesFragment appliancesFragment = new AppliancesFragment();

    private ImageView back;
    private Button manage;
    private Button create;
    private EditText name;
    private EditText description;
    private String sName;
    private String sDescription;
    private Spinner dropdown;

    private ArrayList<applianceProfile> applianceProfiles;
    private ArrayList<String> applianceNames = new ArrayList<>();
    private Boolean hasDescription = false;
    private Boolean hasAppliance = false;
    private int pos;

    private plugProfile plug;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_create_plug, container, false);

        appDriver = ((MainActivity) getActivity()).getAppDriver();

        //back button
        back = inflatedView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        //appliance list
        applianceProfiles = appDriver.getApplianceList();
        applianceNames.clear();
        for(int i = 0; i < applianceProfiles.size(); i++){
            applianceNames.add(applianceProfiles.get(i).getName());
        }

        dropdown = inflatedView.findViewById(R.id.dropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflatedView.getContext(), android.R.layout.simple_spinner_item, applianceNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        //manage appliance
        manage = inflatedView.findViewById(R.id.manage);
        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).forwardFragment(appliancesFragment);
            }
        });

        //create profile
        create = inflatedView.findViewById(R.id.create);
        name = inflatedView.findViewById(R.id.name);
        description = inflatedView.findViewById(R.id.description);

        create.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sName = name.getText().toString();

                if (validateForm()){

                    plug = new plugProfile(sName);
                    if(hasDescription && hasAppliance){
                        sDescription = description.getText().toString();
                        plug = new plugProfile(sName, applianceProfiles.get(pos), sDescription, appDriver);

                    } else if (hasDescription && !hasAppliance){
                        sDescription = description.getText().toString();
                        plug = new plugProfile(sName, applianceProfiles.get(0), sDescription, appDriver);

                    } else if (!hasDescription && hasAppliance){
                        plug = new plugProfile(sName, applianceProfiles.get(pos), " ", appDriver);

                    } else if (!hasDescription && !hasAppliance){
                        plug = new plugProfile(sName, applianceProfiles.get(0), " ", appDriver);
                    }
                    new addPlugAsync().execute(plug);
                    ((MainActivity) getActivity()).onBackPressed();
                }
            }
        });
        return inflatedView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        pos = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public Boolean validateForm(){
        if (name.length() == 0){
            name.setError("Please enter a name");
            return false;
        } else if (!appDriver.isPlugUnique(sName)){
            name.setError("Duplicate name");
            return false;
        } else {
            if(description.length() > 0){
                hasDescription = true;
            }
            if(pos == 0){
                hasAppliance = false;
            } else {
                hasAppliance = true;
            }
            return true;
        }
    }

    public class addPlugAsync extends AsyncTask<plugProfile, Void, String> {
        @Override
        protected String doInBackground(plugProfile... params) {
            appDriver.addPlugProfile(params[0]);
            return "Done";
        }
        @Override
        protected void onPostExecute(String result){
        }
    }
}