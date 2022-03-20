package app.potentia;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import app.potentia.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    private View inflatedView;
    private Button login;
    private EditText username;
    private EditText password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflatedView = inflater.inflate(R.layout.fragment_login, container, false);

        username = inflatedView.findViewById(R.id.username);
        password = inflatedView.findViewById(R.id.password);

        //login button
        login = inflatedView.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().equals("potentia") && password.getText().toString().equals("password")){
                    ((MainActivity) getActivity()).hasLoggedIn(true);
                } else {
                    username.setError("Please enter the correct username & password");
                }

            }
        });


        return inflatedView;
    }
}