package lokavidya.iitb.com.lvcreate.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.fragment.HomeFragment;
import lokavidya.iitb.com.lvcreate.fragment.NotificationFragment;
import lokavidya.iitb.com.lvcreate.fragment.ProfileFragment;
import lokavidya.iitb.com.lvcreate.fragment.ProjectFragment;
import lokavidya.iitb.com.lvcreate.util.BottomNavigationViewHelper;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Global fields
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigation = findViewById(R.id.navigationView);
        // To remove the bubble-like animation
        BottomNavigationViewHelper.removeShiftMode(bottomNavigation);
        // Listener is defined below
        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavListener);

        // Set the Frame to HomeFragment
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame, new HomeFragment())
                .commit();

        // Boilerplate NavigationDrawer Activity code
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                /*LayoutInflater factory = LayoutInflater.from(DashboardActivity.this);
                final View dialogView = factory.inflate(R.layout.create_dialog, null);
                final AlertDialog createDialog = new AlertDialog.Builder(DashboardActivity.this).create();
                createDialog.setView(dialogView);
                createDialog.setContentView(R.layout.create_dialog);
                createDialog.setTitle("Project Name");

                final EditText projectName = (EditText)createDialog.findViewById(R.id.projectname);

                Button next = (Button)findViewById(R.id.next);*/

                LayoutInflater factory = LayoutInflater.from(DashboardActivity.this);
                final View dialogView = factory.inflate(R.layout.create_dialog, null);
                final AlertDialog dialog = new AlertDialog.Builder(DashboardActivity.this)
                        .setView(dialogView)
                        .create();

                //we don't want title
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.create_dialog);

                //bind views from dialog layout
                final EditText projectName = dialogView.findViewById(R.id.projectname);
                Button next = dialogView.findViewById(R.id.next);

                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (projectName.getText().toString() != "") {

                            Toast.makeText(getApplicationContext(), "Opened Project activity", Toast.LENGTH_SHORT).show();
                            //createDialog.dismiss();
                            dialog.dismiss();

                        } else {

                            Toast.makeText(getApplicationContext(), "Enter project name", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                dialog.show();
                //createDialog.show();
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.bottom_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.bottom_project:
                            selectedFragment = new ProjectFragment();
                            break;
                        case R.id.bottom_notification:
                            selectedFragment = new NotificationFragment();
                            break;
                        case R.id.bottom_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }

                    fragmentManager.beginTransaction()
                            .replace(R.id.main_frame, selectedFragment)
                            .commit();
                    return true;

                }
            };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
