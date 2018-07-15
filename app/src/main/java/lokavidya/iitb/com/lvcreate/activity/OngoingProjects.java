package lokavidya.iitb.com.lvcreate.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lokavidya.iitb.com.lvcreate.R;

import lokavidya.iitb.com.lvcreate.adapter.ProjectListAdapter;
import lokavidya.iitb.com.lvcreate.model.HomeItem;
import lokavidya.iitb.com.lvcreate.model.ProjectList;

public class OngoingProjects extends AppCompatActivity {

    public Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    RecyclerView projectlist;
    ProjectListAdapter adapter;
    View rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_projects);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Ongoing Projects ");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        projectlist =  (RecyclerView)findViewById(R.id.projectlist_recycler);
        ArrayList<ProjectList> data = new ArrayList<>();
        data.add(new ProjectList("Title of Video 1", " null", "2:30"));
        data.add(new ProjectList("Title of Video 2", "null", "3:50"));
        data.add(new ProjectList("Title of Video 3", "null", "4:10"));
        data.add(new ProjectList("Title of Video 4", "null", "6:30"));
        data.add(new ProjectList("Title of Video 5", "null", "2:40"));
        data.add(new ProjectList("Title of Video 6", "null", "3:15"));

        adapter = new ProjectListAdapter(data);
        projectlist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        projectlist.setAdapter(adapter);





    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}






