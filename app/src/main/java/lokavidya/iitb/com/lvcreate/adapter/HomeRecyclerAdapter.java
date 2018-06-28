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
import lokavidya.iitb.com.lvcreate.model.HomeItem;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.MyViewHolder> {

    // Global fields
    private List<HomeItem> data;

    public HomeRecyclerAdapter(List<HomeItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = inflater.inflate(R.layout.home_recycler_layout, parent, false);
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_recycler_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        HomeItem currentItem = data.get(position);

        holder.title.setText(currentItem.getItemTitle());
        holder.desc.setText(currentItem.getItemDesc());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView desc;
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.home_item_title);
            desc = itemView.findViewById(R.id.home_item_desc);
            img = itemView.findViewById(R.id.home_item_img);

        }
    }

}
