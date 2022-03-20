package app.potentia;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationBarView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    NavigationBarView navigationBarView;
    appDriver appDriver = new appDriver();
    LoginFragment loginFragment = new LoginFragment();
    InfoFragment infoFragment = new InfoFragment();
    PlugFragmentMain plugFragment = new PlugFragmentMain();
    HomeFragment homeFragment = new HomeFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    Boolean login = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new loadAsync().execute();

        if(!login){
            switchFragment(loginFragment);
        }
        navigationBarView = findViewById(R.id.bottomNavigationView);
        navigationBarView.setSelectedItemId(R.id.home);
        navigationBarView.setOnItemSelectedListener(this);
    }

    //bottom nav bar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                stopAsyncReadings();
                switchFragment(infoFragment);
                return true;
            case R.id.plugs:
                stopAsyncReadings();
                switchFragment(plugFragment);
                return true;
            case R.id.home:
                stopAsyncReadings();
                switchFragment(homeFragment);
                return true;
            case R.id.notif:
                stopAsyncReadings();
                switchFragment(notificationFragment);
                return true;
            case R.id.settings:
                stopAsyncReadings();
                switchFragment(settingsFragment);
                return true;
        }
        return false;
    }

    public class loadAsync extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params) {
            appDriver.loadApplianceProfiles();
            appDriver.loadPlugProfiles();
            return "Done";
        }
        @Override
        protected void onPostExecute(String result){
        }
    }

    public void switchFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,fragment).commit();
    }

    //add to stack
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

    public plugProfile getCurrentPlug(){
        plugProfile currentPlug = homeFragment.getCurrentPlug();
        return currentPlug;
    }

    public appDriver getAppDriver(){
        return appDriver;
    }

    public void hasLoggedIn(Boolean b){
        this.login = b;
        switchFragment(homeFragment);
        navigationBarView.setVisibility(View.VISIBLE);
    }

    public void stopAsyncReadings(){
        AsyncTask readings = homeFragment.getAsync();
        Timer timer = homeFragment.getTimer();
        TimerTask task = homeFragment.getTimerTask();
        if(readings != null && timer != null && task != null){
            readings.cancel(true);
            timer.cancel();
            task.cancel();
        }
    }
}