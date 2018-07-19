package lokavidya.iitb.com.lvcreate.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    public static final String MyPREFERENCES = "MyPrefs";

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment selectedFragment = null;
                    boolean changeFragment = false;

                    switch (item.getItemId()) {
                        case R.id.bottom_home:
                            getSupportActionBar().setTitle("Home");
                            selectedFragment = new HomeFragment();
                            changeFragment = true;
                            break;
                        case R.id.bottom_project:
                            getSupportActionBar().setTitle("Projects");
                            selectedFragment = new ProjectFragment();
                            changeFragment = true;
                            break;
                        case R.id.bottom_blank:
                            createProjectDialog();
                            break;
                        case R.id.bottom_notification:
                            getSupportActionBar().setTitle("Notifications");
                            selectedFragment = new NotificationFragment();
                            changeFragment = true;
                            break;
                        case R.id.bottom_profile:
                            getSupportActionBar().setTitle("Profile");
                            selectedFragment = new ProfileFragment();
                            changeFragment = true;
                            break;
                    }

                    if (changeFragment) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_frame, selectedFragment)
                                .commit();
                    }
                    return true;

                }
            };

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

        ImageView imgCreateBtn = findViewById(R.id.image_create_btn);
        imgCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProjectDialog();
            }
        });

    }

    public void ongoingProj(View v) {
        Intent intent = new Intent(this, OngoingProjects.class);
        startActivity(intent);

    }

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


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void createProjectDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DashboardActivity.this, R.style.CustomAlertDialog);
        View mView = getLayoutInflater().inflate(R.layout.create_dialog, null);

        //bind views from dialog layout
        final EditText projectName = mView.findViewById(R.id.projectname);
        Button next = mView.findViewById(R.id.next);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!projectName.getText().toString().isEmpty()) {

                    // Get the project name string and pass it with intent
                    String name = projectName.getText().toString();

                    Toast.makeText(getApplicationContext(), "Project Created Successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), CreateProjectActivity.class);
                    intent.putExtra("projectTitle", name);
                    intent.putExtra("isProjectExist", false);
                    startActivity(intent);

                    dialog.dismiss();

                } else {
                    Toast.makeText(getApplicationContext(), "Enter project name", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();

    }
}
