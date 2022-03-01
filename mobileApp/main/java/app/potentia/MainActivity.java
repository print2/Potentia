package app.potentia;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationBarView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    NavigationBarView navigationBarView;

    InfoFragment infoFragment = new InfoFragment();
    PlugFragmentMain plugFragment = new PlugFragmentMain();
    HomeFragment homeFragment = new HomeFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchFragment(homeFragment);

        navigationBarView = findViewById(R.id.bottomNavigationView);
        navigationBarView.setSelectedItemId(R.id.home);
        navigationBarView.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                switchFragment(infoFragment);
                return true;
            case R.id.plugs:
                switchFragment(plugFragment);
                return true;
            case R.id.home:
                switchFragment(homeFragment);
                return true;
            case R.id.notif:
                switchFragment(notificationFragment);
                return true;
            case R.id.settings:
                switchFragment(settingsFragment);
                return true;
        }
        return false;
    }

    public void switchFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,fragment).commit();
    }

    public void forwardFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}