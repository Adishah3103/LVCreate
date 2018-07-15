package lokavidya.iitb.com.lvcreate.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.model.ProjectList;

/**
 * Created by Aditya on 16-07-2018.
 */

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.MyViewHolder> {
   private List<ProjectList> data;

    public ProjectListAdapter(List<ProjectList> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_card, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectListAdapter.MyViewHolder holder, int position) {

        ProjectList currentItem = data.get(position);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView project_name;
        TextView duration;
        ImageView imgURL;

        public MyViewHolder(View itemView) {
            super(itemView);

            project_name = itemView.findViewById(R.id.project_name);
            duration = itemView.findViewById(R.id.duration);
            imgURL = itemView.findViewById(R.id.image_top_left);

        }
    }

}
