package lokavidya.iitb.com.lvcreate.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import lokavidya.iitb.com.lvcreate.R;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final Context context = this;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                final String PREFS_NAME = "MyPrefsFile";
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

                if (settings.getBoolean("my_first_time", true)) {
                    settings.edit().putBoolean("my_first_time", false).commit();
                    Intent intent = new Intent(context, IntroActivity.class);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    Intent intent = new Intent(context, WelcomeActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}