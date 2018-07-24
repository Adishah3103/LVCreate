package lokavidya.iitb.com.lvcreate.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.adapter.ProjectListAdapter;
import lokavidya.iitb.com.lvcreate.dbUtils.ProjectDb;
import lokavidya.iitb.com.lvcreate.model.Project;
import lokavidya.iitb.com.lvcreate.util.AppExecutors;

public class UploadedProjectsFragment extends Fragment {

    View view;
    RecyclerView uploadedProjectList;
    ProjectListAdapter adapter;
    ProjectDb mDb;
    List<Project> data;

    public UploadedProjectsFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout
        view = inflater.inflate(R.layout.fragment_uploaded_projects, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        uploadedProjectList = view.findViewById(R.id.uploaded_project_recycler);

        /// Get database instance
        mDb = ProjectDb.getsInstance(getActivity().getApplicationContext());

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                data = mDb.projectDao().loadAllProject();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ProjectListAdapter(getActivity(), data);
                        uploadedProjectList.setLayoutManager(new LinearLayoutManager((getActivity().getApplicationContext())));
                        uploadedProjectList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });

    }

}
