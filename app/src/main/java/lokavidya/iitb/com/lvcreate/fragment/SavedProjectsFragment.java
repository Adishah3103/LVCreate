package lokavidya.iitb.com.lvcreate.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.adapter.ProjectListAdapter;
import lokavidya.iitb.com.lvcreate.dbUtils.ProjectDb;
import lokavidya.iitb.com.lvcreate.model.Project;
import lokavidya.iitb.com.lvcreate.util.AppExecutors;

public class SavedProjectsFragment extends Fragment {

    View view;
    RecyclerView savedProjectList;
    ProjectListAdapter adapter;
    ProjectDb mDb;
    List<Project> data = new ArrayList<>();

    public SavedProjectsFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("onCreate", "Inside");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout
        view = inflater.inflate(R.layout.fragment_saved_projects, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        savedProjectList = view.findViewById(R.id.saved_project_recycler);

        // Get database instance
        mDb = ProjectDb.getsInstance(getActivity().getApplicationContext());

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                data = mDb.projectDao().loadAllProject();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // tabId = 1 (for Saved), tabId = 2 (for Uploaded)
                        adapter = new ProjectListAdapter(getActivity(), data, 1);
                        savedProjectList.setLayoutManager(new LinearLayoutManager((getActivity().getApplicationContext())));
                        savedProjectList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });

    }
}
