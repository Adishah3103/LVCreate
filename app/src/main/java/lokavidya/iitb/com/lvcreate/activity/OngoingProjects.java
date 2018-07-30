package lokavidya.iitb.com.lvcreate.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.fragment.SavedProjectsFragment;
import lokavidya.iitb.com.lvcreate.fragment.UploadedProjectsFragment;

public class OngoingProjects extends AppCompatActivity {

    public Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_projects);
        Toolbar toolbar = findViewById(R.id.ongoing_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Ongoing Projects");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        viewPager = findViewById(R.id.ongoing_viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.ongoing_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        tabLayout.setTabTextColors(Color.parseColor("#666666"),Color.parseColor("#F57C00"));
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SavedProjectsFragment(), "Saved");
        adapter.addFragment(new UploadedProjectsFragment(), "Uploaded");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}






