package com.schneewittchen.rosandroid.ui.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.schneewittchen.rosandroid.R;
import com.schneewittchen.rosandroid.ui.fragments.intro.IntroFragment;
import com.schneewittchen.rosandroid.ui.fragments.main.MainFragment;
import com.schneewittchen.rosandroid.ui.fragments.main.OnBackPressedListener;


/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.0.1
 * @created on 16.01.20
 * @updated on 19.06.20
 * @modified by Nils Rottmann
 * @updated on 27.07.20
 * @modified by Nils Rottmann
 */
public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERM = 101;
    public static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Edge-to-edge is enforced since targetSdk 35. Pad the root container
        // so content is not drawn under the system bars or display cutout.
        View container = findViewById(R.id.main_container);
        ViewCompat.setOnApplyWindowInsetsListener(container, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout()
                    | WindowInsetsCompat.Type.ime());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                if (fragment instanceof OnBackPressedListener
                        && ((OnBackPressedListener) fragment).onBackPressed()) {
                    return;
                }

                // Not handled by the fragment: let the system handle back
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
                setEnabled(true);
            }
        });

        try {
            if (savedInstanceState == null && requiresIntro()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, IntroFragment.newInstance())
                        .commitNow();
            } else {
                Toolbar myToolbar = findViewById(R.id.toolbar);
                setSupportActionBar(myToolbar);

                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, MainFragment.newInstance())
                            .commitNow();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        this.requestPermissions();
    }

    private void requestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, LOCATION_PERM);
    }

    // Check in required if update is available or onboarding has not been done yet
    private boolean requiresIntro() throws PackageManager.NameNotFoundException {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("introPrefs", MODE_PRIVATE);

        return (pref.getInt("VersionNumber", 0) != getPackageManager().getPackageInfo(getPackageName(), 0).versionCode) ||
                !pref.getBoolean("CheckedIn", false);

    }

}
