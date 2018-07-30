package lokavidya.iitb.com.lvcreate.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
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
import lokavidya.iitb.com.lvcreate.adapter.ProjectItemRecyclerAdapter;
import lokavidya.iitb.com.lvcreate.dbUtils.ProjectDb;
import lokavidya.iitb.com.lvcreate.fileManagement.ManageFolder;
import lokavidya.iitb.com.lvcreate.model.Project;
import lokavidya.iitb.com.lvcreate.model.ProjectItem;
import lokavidya.iitb.com.lvcreate.util.AppExecutors;
import lokavidya.iitb.com.lvcreate.util.Master;

public class CreateProjectActivity extends AppCompatActivity {

    public static final String LOG_TAG = CreateProjectActivity.class.getSimpleName();

    // Request codes for File intents
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_PICK_IMAGE = 2;
    public static final int REQUEST_PICK_VIDEO = 3;
    public static final int REQUEST_PICK_AUDIO = 4;

    // Global fields
    RecyclerView projectItemList;
    ProjectItemRecyclerAdapter adapter;
    ArrayList<ProjectItem> list = new ArrayList<>();
    String cameraIntentImgPath;
    Intent intent;
    Project currentProject;
    private String title = "";
    Boolean isProjectExist;
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
        isProjectExist = intent.getBooleanExtra("isProjectExist", false);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            title = savedInstanceState.getString("projectTitle");
        } else {
            // Initialize members with default values for a new instance
            title = intent.getStringExtra("projectTitle");
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
            projectId = intent.getLongExtra("projectId", -1);
        }

        // Find views
        Toolbar toolBar = findViewById(R.id.toolbar);
        projectItemList = findViewById(R.id.project_recycler);

        // Set up Toolbar
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle(title);

        // Set up RecyclerView
        adapter = new ProjectItemRecyclerAdapter(list);
        projectItemList.setAdapter(adapter);
        projectItemList.setLayoutManager(new LinearLayoutManager(this));

        // We restrict the DB access on this Activity.
        // Using both the table queries here.
        mDb = ProjectDb.getsInstance(getApplicationContext());

        askForStoragePermission();

        if (savedInstanceState == null && !isProjectExist) {

            /**
             * Whenever you create new object of "Project' you will get project Id that
             * starts with 0, do not use it.
             * insertItem returns the ProjectId (long) which is actually stored in the database.
             * Use that for further queries
             * */

            // Execute query to load items with project ID
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {

                    currentProject = new Project(
                            title,
                            " ",
                            00,
                            00,
                            null,
                            " ",
                            false
                    );

                    projectId = mDb.projectDao().insertItem(currentProject);

                    Log.i(LOG_TAG + " DB",
                            "Project added in database, ID: " + String.valueOf(projectId));

                    // Create the folder structure with as title as name
                    ManageFolder.createFolderStructure(getApplicationContext(), title);
                }


            });

        } else if (isProjectExist) {

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {

                    if (projectId != -1) {
                        currentProject = mDb.projectDao().loadItemById(projectId);

                        list = (ArrayList<ProjectItem>) mDb.projectItemDao().loadItemsByProjectId(projectId);

                        Log.i("pathh", list.get(0).getItemFilePath());

                        // Set up RecyclerView
                        adapter = new ProjectItemRecyclerAdapter(list);
                        projectItemList.setAdapter(adapter);
                        projectItemList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                        adapter.notifyDataSetChanged();

                    }

                }
            });

        }

        // To finish this activity remotely
        setupFinishBroadcast();

    }


    public void addDetails(View v) {

        if (list.size() > 0) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // We first need to set the Order and ProjectId of all items
                    for (int i = 0; i < list.size(); i++) {

                        ProjectItem currentItem = list.get(i);

                        currentItem.setItemProjectId(projectId);

                        // Order will start from 1
                        currentItem.setOrder(i + 1);

                        // Start storing items in database
                        mDb.projectItemDao().insertItem(currentItem);

                    }
                }
            });

            Intent intentDetails = new Intent(this, AddProjectDetails.class);

            intentDetails.putExtra("pid", projectId);
            intentDetails.putExtra("projectPath",
                    getExternalFilesDir(Master.ALL_PROJECTS_FOLDER).getAbsolutePath() + "/" + title);


            startActivity(intentDetails);
        } else {
            Toast.makeText(this, "Please add at least one item", Toast.LENGTH_SHORT).show();
        }
    }

    public void addImage(View view) {

        if (askForStoragePermission()) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateProjectActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.layout_add_image, null);
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
                    true));

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
                        true));

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
                        true));

                // Update the adapter to reflect the changes
                adapter.notifyDataSetChanged();

            }
        }

    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (!isProjectExist) {

                            //delete the empty folders now as discarded
                            ManageFolder.removeFolder(getExternalFilesDir(
                                    Master.ALL_PROJECTS_FOLDER).getAbsolutePath() + "/" + title + "/");
                            mDb.projectDao().deleteItemById(projectId);
                            Log.i(LOG_TAG + " DB",
                                    "Project discarded, ID: " + String.valueOf(projectId) +
                                            ", Name : " + String.valueOf(currentProject.getTitle()));
                        }

                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(CreateProjectActivity.this);
                    }
                };
        // Show a dialog that notifies the user they have unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);

        //super.onBackPressed();
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
        savedInstanceState.putString("title", getIntent().getStringExtra("projectTitle"));

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

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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


    private void setupFinishBroadcast() {

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    finish();
                    // DO WHATEVER YOU WANT.
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));

    }
}
