package lokavidya.iitb.com.lvcreate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.activity.AddProjectDetails;
import lokavidya.iitb.com.lvcreate.activity.OngoingProjects;

public class ProjectFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project, container, false);





    }


    public void previewProj(View v)

    {


    }


    public void publishedProj(View v)

    {


    }
}
