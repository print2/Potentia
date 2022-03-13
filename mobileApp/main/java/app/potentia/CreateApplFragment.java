package app.potentia;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class CreateApplFragment extends Fragment {

    private View inflatedView;
    private appDriver appDriver;
    private TextInputLayout timerInput;

    private ImageView back;
    private TextView name;
    private TextView timer;
    private int pos;

    private String sName;
    private Boolean permOn = false;
    private int iTimer;

    private RadioGroup radioGroup;
    private Button create;

    public CreateApplFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_create_appl, container, false);

        appDriver = ((MainActivity) getActivity()).getAppDriver();

        //back button
        back = inflatedView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        name = inflatedView.findViewById(R.id.name);
        timer = inflatedView.findViewById(R.id.timerText);
        radioGroup = inflatedView.findViewById(R.id.radioGroup);
        timerInput = inflatedView.findViewById(R.id.textInputLayout2);
        create = inflatedView.findViewById(R.id.create);

        radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        //create
        create.setOnClickListener(new View.OnClickListener() {
            applianceProfile applianceProfile;

            @Override
            public void onClick(View view) {
                sName = name.getText().toString();

                if (validateForm()){
                    if(TextUtils.isEmpty(timer.getText())){
                        applianceProfile = new applianceProfile(sName, permOn);
                    } else {
                        iTimer = Integer.parseInt(timer.getText().toString());
                        applianceProfile = new applianceProfile(sName, permOn, iTimer, iTimer);
                    }
                    appDriver.addAppliance(applianceProfile);
                    ((MainActivity) getActivity()).onBackPressed();
                }
            }
        });

        return inflatedView;
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.manual:
                    timerInput.setVisibility(View.GONE);
                    pos = 0;
                    break;
                case R.id.permOn:
                    timerInput.setVisibility(View.GONE);
                    permOn = true;
                    pos = 1;
                    break;
                case R.id.timer:
                    timerInput.setVisibility(View.VISIBLE);
                    pos = 2;
                    break;
            }
        }
    };

    public Boolean validateForm(){
        if (TextUtils.isEmpty(sName)){
            name.setError("Please enter a name");
            return false;
        } else if (pos == 2 && timer.length() == 0){
            timer.setError("Please enter a time or select another option");
            return false;
        } else {
            return true;
        }
    }
}