package lokavidya.iitb.com.lvcreate.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.model.ProjectItem;

public class ProjectRecyclerAdapter extends RecyclerView.Adapter<ProjectRecyclerAdapter.MyViewHolder> {

    // Global fields
    private List<ProjectItem> data;

    public ProjectRecyclerAdapter(List<ProjectItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ProjectRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_recycler_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectRecyclerAdapter.MyViewHolder holder, int position) {

        if (data != null && !data.isEmpty()) {
            ProjectItem currentItem = data.get(position);

            holder.itemThumb.setImageBitmap(currentItem.getItemThumb());
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView itemThumb;

        public MyViewHolder(View itemView) {
            super(itemView);

            //itemThumb = itemView.findViewById(R.id.project_item_thumb);
        }
    }
}
