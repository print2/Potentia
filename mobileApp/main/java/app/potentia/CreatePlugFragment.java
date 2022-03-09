package app.potentia;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;

public class CreatePlugFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public CreatePlugFragment() {
        // Required empty public constructor
    }


    private View inflatedView;
    private appDriver appDriver;

    private ImageView back;
    private Button create;
    private EditText name;
    private EditText description;
    private String sName;
    private String sDescription;
    private Spinner dropdown;
    private ArrayList<applianceProfile> applianceProfiles;
    private ArrayList<String> applianceNames = new ArrayList<>();
    private int pos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_create_plug, container, false);

        appDriver = ((MainActivity) getActivity()).getAppDriver();

        back = inflatedView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        applianceProfiles = appDriver.getApplianceList();
        applianceNames.add("None");
        for(int i = 0; i < applianceProfiles.size(); i++){
            applianceNames.add(applianceProfiles.get(i).getName());
        }

        dropdown = inflatedView.findViewById(R.id.dropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflatedView.getContext(), android.R.layout.simple_spinner_item, applianceNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        create = inflatedView.findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = inflatedView.findViewById(R.id.name);
                sName = name.getText().toString();
                description = inflatedView.findViewById(R.id.description);
                sDescription = description.getText().toString();

                //if no appliance selected
                plugProfile plug;
                if(pos == 0){
                    plug = new plugProfile(sName);

                } else {
                    plug = new plugProfile(sName, "", applianceProfiles.get(pos - 1), sDescription);
                }
                appDriver.addPlugProfile(plug);
                ((MainActivity) getActivity()).onBackPressed();
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
        pos = 0;
    }
}