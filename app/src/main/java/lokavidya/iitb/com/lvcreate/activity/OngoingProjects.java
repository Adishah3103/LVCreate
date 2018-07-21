package lokavidya.iitb.com.lvcreate.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.adapter.ProjectListAdapter;
import lokavidya.iitb.com.lvcreate.dbUtils.ProjectDb;
import lokavidya.iitb.com.lvcreate.model.Project;

public class OngoingProjects extends AppCompatActivity {

    public Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    RecyclerView projectList;
    ProjectListAdapter adapter;
    View rootLayout;

    ProjectDb mDb;


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

        projectList = findViewById(R.id.projectlist_recycler);

        // Get database instance
        mDb = ProjectDb.getsInstance(getApplicationContext());

        List<Project> data = mDb.projectDao().loadAllProject();

        adapter = new ProjectListAdapter(OngoingProjects.this, data);
        projectList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        projectList.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}






