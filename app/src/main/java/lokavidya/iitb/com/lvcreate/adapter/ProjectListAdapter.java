package lokavidya.iitb.com.lvcreate.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.activity.CreateProjectActivity;
import lokavidya.iitb.com.lvcreate.dbUtils.ProjectDb;
import lokavidya.iitb.com.lvcreate.fileManagement.ManageFolder;
import lokavidya.iitb.com.lvcreate.fileManagement.ManageZip;
import lokavidya.iitb.com.lvcreate.model.Project;
import lokavidya.iitb.com.lvcreate.util.AppExecutors;
import lokavidya.iitb.com.lvcreate.util.CloudStorage;
import lokavidya.iitb.com.lvcreate.util.Master;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.MyViewHolder> {

    private List<Project> data;
    private Context context;
    Project currentItem;
    String fileUrl = null;
    ProgressDialog progressDialog;
    ProjectDb mDb;

    final int IMG_THUMB_WIDTH = 180;
    final int IMG_THUMB_HEIGHT = 180;

    public ProjectListAdapter(Context context, List<Project> data) {

        this.context = context;
        this.data = data;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_project_ongoing, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProjectListAdapter.MyViewHolder holder, int position) {

        currentItem = data.get(position);

        holder.projectName.setText(currentItem.getTitle());

        if (!currentItem.getFirstFileThumb().equals(" ")) {
            // Create thumbnail from image path
            Bitmap imageThumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(
                    currentItem.getFirstFileThumb()),
                    IMG_THUMB_WIDTH,
                    IMG_THUMB_HEIGHT);

            holder.projectThumbnail.setImageBitmap(imageThumb);
        }

        if (!currentItem.getDesc().equals(" ")) {
            holder.prjectDesc.setText(currentItem.getDesc());
        }

        // Store the tags of project in buttons
        holder.itemView.setTag(R.id.item_number, position);
        holder.itemView.setTag(R.id.project_id, currentItem.getId());
        holder.itemView.setTag(R.id.project_title, currentItem.getTitle());

        holder.projectUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Uploading Project...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        // Add your zipping code here
                        String projectFolderPath = context.getExternalFilesDir(Master.ALL_PROJECTS_FOLDER)
                                .getAbsolutePath() + "/" + currentItem.getTitle() + "/";
                        String zipPath = context.getExternalFilesDir(Master.ALL_ZIPS_FOLDER)
                                .getAbsolutePath() + "/" + currentItem.getTitle() + ".zip";

                        Log.d("AAD", "Project Folder path :" + projectFolderPath);
                        Log.d("AAD", "Zip path :" + zipPath);

                        ManageZip mz = new ManageZip();
                        mz.zipFileAtPath(projectFolderPath, zipPath);

                        Log.d("AAD", "Zipping done");

                        /**
                         * Code to upload the zip
                         * **/
                        Log.d("AAD", "Starting uploading");

                        // This is the input path for the .zip you created
                        //Uri baseUri = Uri.parse(String.valueOf(Environment.getExternalStorageDirectory()) + "/Sounds.zip");

                        Uri baseUri = Uri.parse(zipPath);
                        try {
                            // We get Url in fileUrl
                            fileUrl = CloudStorage.uploadFile(context,  // Get context here
                                    "lvcms-development-testing", "zips/" +
                                            currentItem.getTitle() + ".zip",  // Name of the .zip on the Bucket
                                    baseUri);

                            Log.i("Upload", fileUrl);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        progressDialog.dismiss();

                        Activity activity = (Activity) context;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.RoundAlertDialog);
                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View mView = inflater.inflate(R.layout.layout_request_sent, null);

                                Button okButton = mView.findViewById(R.id.btn_ok_req_sent);
                                mBuilder.setView(mView);
                                final AlertDialog alertDialog = mBuilder.create();

                                okButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

                                alertDialog.show();
                            }
                        });

                    }

                });

            }
        });

        holder.projectDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Delete project?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Uploading Project..");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        // User clicked the "Delete" button, so delete the project.
                        removeAt((Integer) holder.itemView.getTag(R.id.item_number));

                        mDb = ProjectDb.getsInstance(context);

                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {

                                // Delete the Project entry
                                mDb.projectDao()
                                        .deleteItemById((Long) holder.itemView.getTag(R.id.project_id));
                                // Delete all the items in it
                                mDb.projectItemDao()
                                        .deleteItemsByProjectId((Long) holder.itemView.getTag(R.id.project_id));

                                ManageFolder.removeFolder(context.getExternalFilesDir(Master.ALL_PROJECTS_FOLDER)
                                        .getAbsolutePath() + "/"
                                        + holder.projectDelete.getTag(R.id.project_title));

                                progressDialog.dismiss();
                            }

                        });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Cancel" button, so dismiss the dialog
                        // and continue editing the item.
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openProject = new Intent(context, CreateProjectActivity.class);
                openProject.putExtra("isProjectExist", true);
                openProject.putExtra("projectId",
                        (Long) holder.itemView.getTag(R.id.project_id));
                openProject.putExtra("projectTitle",
                        String.valueOf(holder.itemView.getTag(R.id.project_title)));
                context.startActivity(openProject);

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

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardView rootLayout;
        TextView projectName;
        TextView prjectDesc;
        ImageView projectThumbnail;
        Button projectUpload;
        Button projectDelete;

        public MyViewHolder(View itemView) {
            super(itemView);

            rootLayout = itemView.findViewById(R.id.root_card_view);
            projectName = itemView.findViewById(R.id.project_name);
            prjectDesc = itemView.findViewById(R.id.project_desc);
            projectThumbnail = itemView.findViewById(R.id.img_project_thumb);
            projectUpload = itemView.findViewById(R.id.btn_upload_project);
            projectDelete = itemView.findViewById(R.id.btn_delete_project);

        }
    }

}
