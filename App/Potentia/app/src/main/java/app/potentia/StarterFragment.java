package app.potentia;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StarterFragment extends Fragment {

    public StarterFragment() {
        // Required empty public constructor
    }

    private View inflatedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_starter, container, false);
        return inflatedView;
    }
}