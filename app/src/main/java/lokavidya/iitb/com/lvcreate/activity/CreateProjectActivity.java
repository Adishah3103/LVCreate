package lokavidya.iitb.com.lvcreate.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.adapter.ProjectRecyclerAdapter;
import lokavidya.iitb.com.lvcreate.dbUtils.ProjectDb;
import lokavidya.iitb.com.lvcreate.model.Project;
import lokavidya.iitb.com.lvcreate.model.ProjectItem;
import lokavidya.iitb.com.lvcreate.util.AppExecutors;

public class CreateProjectActivity extends AppCompatActivity {

    public static final String LOG_TAG = CreateProjectActivity.class.getSimpleName();

    // Request codes for File intents
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_PICK_IMAGE = 2;
    public static final int REQUEST_PICK_VIDEO = 3;
    public static final int REQUEST_PICK_AUDIO = 4;

    // Global fields
    RecyclerView projectItemList;
    ProjectRecyclerAdapter adapter;
    ArrayList<ProjectItem> list = new ArrayList<>();
    String cameraIntentImgPath;
    Intent intent;
    Project currentProject;
    private String title = "";
    long projectId;
    ProjectDb mDb;

    //permission status https://www.androidhive.info/2016/11/android-working-marshmallow-m-runtime-permissions/
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        intent = getIntent();

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            title = savedInstanceState.getString("title");
        } else {
            // Initialize members with default values for a new instance
            title = intent.getStringExtra("title");
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
        }

        //set the title as the project name on toolbar

        // Find views
        Toolbar toolBar = findViewById(R.id.toolbar);
        projectItemList = findViewById(R.id.project_recycler);

        // Set up Toolbar
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle(title);

        // Set up RecyclerView
        adapter = new ProjectRecyclerAdapter(list);
        projectItemList.setAdapter(adapter);
        projectItemList.setLayoutManager(new LinearLayoutManager(this));

        // We restrict the DB access on this Activity.
        // Using both the table queries here.
        mDb = ProjectDb.getsInstance(getApplicationContext());

        if (savedInstanceState == null) {

            /**
             * Whenever you create new object of "Project' you will get project Id that
             * starts with 0, do not use it.
             * insertItem returns the ProjectId (long) which is actually stored in the database.
             * Use that for further queries
             * */

            currentProject = new Project(
                    title,
                    null,
                    00,
                    00,
                    "English"
            );

            projectId = mDb.projectDao().insertItem(currentProject);

            Log.i(LOG_TAG + " DB",
                    "Project added in database, ID: " + String.valueOf(projectId));

        }
    }


    public void addDetails(View v) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // We first need to set the Order and ProjectId of all items
                for (int i = 0; i < list.size(); i++) {

                    ProjectItem currentItem = list.get(i);

                    Log.i("projectId12", String.valueOf(projectId));

                    currentItem.setItemProjectId(projectId);

                    Log.i("projectId13", String.valueOf(projectId));

                    // Order will start from 1
                    currentItem.setOrder(i + 1);

                    // Start storing items in database
                    mDb.projectItemDao().insertItem(currentItem);

                }
            }
        });

        Intent intentDetails = new Intent(this, AddProjectDetails.class);
        intentDetails.putExtra("pid", projectId);
        startActivity(intentDetails);

    }

    public void addImage(View view) {
        
        if (askForStoragePermission()) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateProjectActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.add_image_layout, null);
            mBuilder.setView(mView);

            TextView clickPhoto = mView.findViewById(R.id.click_photo);
            TextView choosePhoto = mView.findViewById(R.id.choose_photo);

            final AlertDialog dialog = mBuilder.create();

            clickPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        }

                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                    "lokavidya.iitb.com.lvcreate.fileprovider",
                                    photoFile);
                            Log.d("Aapdu debug", "" + photoFile + " : " + photoURI);

                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                        }
                    }

                    dialog.dismiss();
                }
            });

            choosePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (askForStoragePermission()) {

                        //You already have the permission, just go ahead.
                        ArrayList<String> filePaths = null;

                        FilePickerBuilder.getInstance().setMaxCount(1)
                                .setSelectedFiles(filePaths)
                                .setActivityTheme(R.style.LibAppTheme)
                                .setActivityTitle("Select an Image")
                                .enableImagePicker(true)
                                .enableVideoPicker(false)
                                .pickPhoto(CreateProjectActivity.this, REQUEST_PICK_IMAGE);

                        dialog.dismiss();
                    }

                }
            });
            dialog.show();
        }
    }

    public void addVideo(View view) {
        if (askForStoragePermission()) {
            ArrayList<String> filePaths = null;

            FilePickerBuilder.getInstance().setMaxCount(1)
                    .setSelectedFiles(filePaths)
                    .setActivityTheme(R.style.LibAppTheme)
                    .setActivityTitle("Select a Video")
                    .enableVideoPicker(true)
                    .enableImagePicker(false)
                    .pickPhoto(CreateProjectActivity.this, REQUEST_PICK_VIDEO);
        }
    }

    public void addAudio() {

        ArrayList<String> filePaths = null;

        String[] mp3 = {".mp3"};
        String[] aac = {".aac"};
        String[] wav = {".wav"};
        FilePickerBuilder.getInstance().setMaxCount(1)
                .addFileSupport("MP3", mp3, R.drawable.ic_audio)
                .addFileSupport("AAC", aac, R.drawable.ic_audio)
                .addFileSupport("WAV", wav, R.drawable.ic_audio)
                .setActivityTitle("Select Audio for Image")
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.LibAppTheme)
                .enableImagePicker(false)
                .enableVideoPicker(false)
                .enableDocSupport(false)
                .pickFile(this, REQUEST_PICK_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Arrays to store file paths retrieved
        ArrayList<String> photoPaths;
        ArrayList<String> videoPaths;
        ArrayList<String> audioPaths;

        // PICK_IMAGE code
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            photoPaths = new ArrayList<>();
            photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));


            Log.i("Photo Paths", photoPaths.get(photoPaths.size() - 1));

            // Create an item and keep the Audio File Path and File size null or dummy
            list.add(new ProjectItem(
                    0,
                    photoPaths.get(photoPaths.size() - 1),
                    00,
                    true,
                    "N/A",
                    00,
                    00,
                    1,
                    false));

            addAudio();

        }

        // IMAGE_CAPTURE Code
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            File file = new File(cameraIntentImgPath);
            if (file.exists()) {

                list.add(new ProjectItem(
                        0,
                        cameraIntentImgPath,
                        00,
                        true,
                        "N/A",
                        00,
                        00,
                        1,
                        false));

                addAudio();

                cameraIntentImgPath = null;
            }
        }

        // PICK_AUDIO code
        if (requestCode == REQUEST_PICK_AUDIO && resultCode == RESULT_OK && data != null) {
            audioPaths = new ArrayList<>();
            audioPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));

            Log.i("Audio Paths", audioPaths.get(audioPaths.size() - 1));

            // Get the last added Image from list
            ProjectItem lastImage = list.get(list.size() - 1);
            lastImage.setItemAudioPath(audioPaths.get(audioPaths.size() - 1));

            // Update the adapter to reflect the changes
            adapter.notifyDataSetChanged();
        }

        // PICK_VIDEO code
        if (requestCode == REQUEST_PICK_VIDEO && resultCode == RESULT_OK && data != null) {
            videoPaths = new ArrayList<>();
            videoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));

            for (int i = 0; i < videoPaths.size(); i++) {
                Log.i("Video Paths", videoPaths.get(i));

                list.add(new ProjectItem(
                        0,
                        videoPaths.get(i),
                        00,
                        false,
                        "N/A",
                        00,
                        00,
                        1,
                        false));

                // Update the adapter to reflect the changes
                adapter.notifyDataSetChanged();

            }
        }

    }

    // Boilerplate methods starts from here
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        cameraIntentImgPath = image.getAbsolutePath();

        return image;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        Log.d("AAD", "restored the state");
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {

            // Restore value of members from saved state
            title = savedInstanceState.getString("title");
        } else {

            // Initialize members with default values for a new instance
            title = intent.getStringExtra("title");
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        Log.d("AAD", "saved the state");
        savedInstanceState.putString("title", getIntent().getStringExtra("title"));

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

    }

    private boolean askForStoragePermission() {

        //https://www.androidhive.info/2016/11/android-working-marshmallow-m-runtime-permissions/
        if (ActivityCompat.checkSelfPermission(CreateProjectActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateProjectActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateProjectActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(CreateProjectActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateProjectActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(CreateProjectActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            editor.commit();
        } else {
            return true;
        }
        return false;
    }
}
