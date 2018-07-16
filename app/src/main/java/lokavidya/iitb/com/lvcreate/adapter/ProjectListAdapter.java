package lokavidya.iitb.com.lvcreate.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import lokavidya.iitb.com.lvcreate.fileManagement.ManageZip;
import lokavidya.iitb.com.lvcreate.model.Project;
import lokavidya.iitb.com.lvcreate.util.AppExecutors;
import lokavidya.iitb.com.lvcreate.util.CloudStorage;
import lokavidya.iitb.com.lvcreate.util.Master;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.MyViewHolder> {

    private List<Project> data;
    private Context context;
    String fileUrl = null;
    ProgressDialog progressDialog;

    public ProjectListAdapter(Context context, List<Project> data) {

        this.context = context;
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

        final Project currentItem = data.get(position);

        holder.projectName.setText(currentItem.getTitle());

        // Store the position of item in buttons
        holder.projectDelete.setTag(position);
        holder.projectUpload.setTag(position);

        holder.projectUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Uploading Project..");
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
                                    "lvcms-development-testing",
                                    currentItem.getTitle() + ".zip",  // Name of the .zip on the Bucket
                                    baseUri);

                            Log.i("Upload", fileUrl);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        progressDialog.dismiss();

                    }


                });


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
