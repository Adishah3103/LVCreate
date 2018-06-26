package lokavidya.iitb.com.lvcreate.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import lokavidya.iitb.com.lvcreate.R;

public class ProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("ProjectName","");
      /*  Log.i("Hello", name);
        TextView tv = (TextView)findViewById(R.id.textView5);
        tv.setText(name);

        */
        Toolbar namebar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(namebar);
       getSupportActionBar().setTitle(name);
    }
}
