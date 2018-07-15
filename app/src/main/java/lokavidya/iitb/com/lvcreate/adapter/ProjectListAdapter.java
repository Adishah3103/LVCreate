package lokavidya.iitb.com.lvcreate.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.model.Project;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.MyViewHolder> {

    private List<Project> data;

    public ProjectListAdapter(List<Project> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_ongoing_recycler, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectListAdapter.MyViewHolder holder, int position) {

        Project currentItem = data.get(position);

        holder.projectName.setText(currentItem.getTitle());

        // Store the position of item in buttons
        holder.projectDelete.setTag(position);
        holder.projectUpload.setTag(position);

        holder.projectUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView projectName;
        TextView prjectDuration;
        ImageView projectThumbnail;
        Button projectUpload;
        Button projectDelete;

        public MyViewHolder(View itemView) {
            super(itemView);

            projectName = itemView.findViewById(R.id.project_name);
            prjectDuration = itemView.findViewById(R.id.project_duration);
            projectThumbnail = itemView.findViewById(R.id.img_project_thumb);
            projectUpload = itemView.findViewById(R.id.btn_upload_project);
            projectDelete = itemView.findViewById(R.id.btn_delete_project);

        }
    }

}
