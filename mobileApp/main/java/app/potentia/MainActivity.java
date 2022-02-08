package app.potentia;

import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationBarView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.*;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    NavigationBarView navigationBarView;

    InfoFragment infoFragment = new InfoFragment();
    PlugFragment plugFragment = new PlugFragment();
    HomeFragment homeFragment = new HomeFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationBarView = findViewById(R.id.bottomNavigationView);
        navigationBarView.setSelectedItemId(R.id.home);
        navigationBarView.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, infoFragment).commit();
                return true;
            case R.id.plugs:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, plugFragment).commit();
                return true;
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                return true;
            case R.id.notif:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationFragment).commit();
                return true;
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, settingsFragment).commit();
                return true;
        }
        return false;
    }
}