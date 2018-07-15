package lokavidya.iitb.com.lvcreate.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
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

        final int IMG_THUMB_WIDTH = 180;
        final int IMG_THUMB_HEIGHT = 180;

        if (data != null && !data.isEmpty()) {
            ProjectItem currentItem = data.get(position);

            // Store the position of item in Delete buttons
            holder.videoDeleteBtn.setTag(position);
            holder.imageDeleteBtn.setTag(position);

            //holder.itemThumb.setImageBitmap(currentItem.getItemThumb());
            if (currentItem.getItemIsAudio()) {

                // Hide the Video thumbnail views
                holder.videoThumb.setVisibility(View.GONE);
                holder.videoDeleteBtn.setVisibility(View.GONE);
                // Show the Image-Audio thumbnails
                holder.imageThumb.setVisibility(View.VISIBLE);
                holder.audioThumb.setVisibility(View.VISIBLE);
                holder.imageDeleteBtn.setVisibility(View.VISIBLE);

                // Create thumbnail from image path
                Bitmap imageThumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(
                        currentItem.getItemFilePath()),
                        IMG_THUMB_WIDTH,
                        IMG_THUMB_HEIGHT);

                // Set the image thumbnail
                holder.imageThumb.setImageBitmap(imageThumb);
            } else {
                // Create thumbnail from video path
                Bitmap videoThumb = ThumbnailUtils.createVideoThumbnail(
                        currentItem.getItemFilePath(),
                        MediaStore.Video.Thumbnails.MINI_KIND);
                // Crop the thumbnails
                Bitmap croppedBitmap = cropImage(videoThumb);

                holder.videoThumb.setImageBitmap(croppedBitmap);
            }
        }

        holder.videoDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retrieve the stored position and delete the entry
                removeAt(Integer.parseInt(v.getTag().toString()));

            }
        });

        holder.imageDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retrieve the stored position and delete the entry
                removeAt(Integer.parseInt(v.getTag().toString()));

            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void removeAt(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }

    private Bitmap cropImage(Bitmap ogBitmap) {

        Bitmap croppedImage;

        int width = ogBitmap.getWidth();
        int height = ogBitmap.getHeight();

        // Get the 4:3 aspect ratio to compare with Bitmap ratio
        float aspectRatio = 4f / 3f;
        float bitmapRatio;

        if (width >= height) {
            // this means bitmap is horizontally oriented
            bitmapRatio = (float) width / (float) height;

            if (bitmapRatio == aspectRatio) {
                // If the Bitmap is 4:3 don't crop
                croppedImage = ogBitmap;
            } else {
                croppedImage = Bitmap.createBitmap(ogBitmap, (width / 2 - height - 2), 0, height, height);
            }

        } else {
            // this means bitmap is vertically oriented
            bitmapRatio = (float) height / (float) width;
            if (bitmapRatio == aspectRatio) {
                croppedImage = ogBitmap;
            } else {
                croppedImage = Bitmap.createBitmap(ogBitmap, 0, (height / 2 - width / 2), width, width);
            }
        }

        return croppedImage;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumb;
        ImageView imageThumb;
        ImageView audioThumb;
        Button videoDeleteBtn;
        Button imageDeleteBtn;
        TextView fileSizeText;
        TextView fileDurationText;

        public MyViewHolder(View itemView) {
            super(itemView);

            videoThumb = itemView.findViewById(R.id.item_video_thumb);
            imageThumb = itemView.findViewById(R.id.item_img_thumb);
            audioThumb = itemView.findViewById(R.id.item_audio_thumb);
            imageDeleteBtn = itemView.findViewById(R.id.btn_delete_image);
            videoDeleteBtn = itemView.findViewById(R.id.btn_delete_video);
            fileSizeText = itemView.findViewById(R.id.txt_file_size);
            fileDurationText = itemView.findViewById(R.id.txt_file_duration);
        }
    }
}
