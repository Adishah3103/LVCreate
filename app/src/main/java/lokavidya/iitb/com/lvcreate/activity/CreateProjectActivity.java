package lokavidya.iitb.com.lvcreate.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import lokavidya.iitb.com.lvcreate.model.ProjectItem;

public class CreateProjectActivity extends AppCompatActivity {

    // Request codes for File intents
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_PICK_IMAGE = 2;
    public static final int REQUEST_PICK_VIDEO = 3;
    public static final int REQUEST_PICK_AUDIO = 4;

    public static final int IMG_THUMB_WIDTH = 180;
    public static final int IMG_THUMB_HEIGHT = 180;

    // Global fields
    RecyclerView projectItemList;
    ProjectRecyclerAdapter adapter;
    ArrayList<ProjectItem> list = new ArrayList<>();
    String cameraIntentImgPath;
    Intent intent;

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
        //set the title as the project name on toolbar
        String title = intent.getStringExtra("title");
        title = title.substring(0, 1).toUpperCase() + title.substring(1);

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

        try {
            list = (ArrayList<ProjectItem>) getLastNonConfigurationInstance();
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ProjectDb mDb = ProjectDb.getsInstance(getApplicationContext());

    }

    public void addImage(View view) {

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

                //Code for permission
                if (ActivityCompat.checkSelfPermission(CreateProjectActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    askForStoragePermission();

                } else {

                    //You already have the permission, just go ahead.
                    ArrayList<String> filePaths = null;

                    FilePickerBuilder.getInstance().setMaxCount(50)
                            .setSelectedFiles(filePaths)
                            .setActivityTheme(R.style.LibAppTheme)
                            .enableImagePicker(true)
                            .enableVideoPicker(false)
                            .pickPhoto(CreateProjectActivity.this, REQUEST_PICK_IMAGE);

                    dialog.dismiss();
                }

            }
        });
        dialog.show();

    }

    public void addVideo(View view) {
        ArrayList<String> filePaths = null;

        FilePickerBuilder.getInstance().setMaxCount(50)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.LibAppTheme)
                .enableVideoPicker(true)
                .enableImagePicker(false)
                .pickPhoto(CreateProjectActivity.this, REQUEST_PICK_VIDEO);
    }

    public void addAudio(View view) {

        ArrayList<String> filePaths = null;

        String[] mp3 = {".mp3"};
        String[] aac = {".aac"};
        FilePickerBuilder.getInstance()
                .addFileSupport("MP3", mp3, R.drawable.ic_camera)
                .addFileSupport("AAC", aac, R.drawable.ic_camera)
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

            for (int i = 0; i < photoPaths.size(); i++) {
                Log.i("Photo Paths", photoPaths.get(i));

                list.add(new ProjectItem(
                        0,
                        photoPaths.get(i),
                        24324,
                        true,
                        "asdsad",
                        325325,
                        324324,
                        1));

                // Update the adapter to reflect the changes
                adapter.notifyDataSetChanged();
            }
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
                        24324,
                        false,
                        "asdsad",
                        3244,
                        324234,
                        1));

                // Update the adapter to reflect the changes
                adapter.notifyDataSetChanged();

            }
        }

        // PICK_AUDIO code
        if (requestCode == REQUEST_PICK_AUDIO && resultCode == RESULT_OK && data != null) {
            audioPaths = new ArrayList<>();
            audioPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));

            for (int i = 0; i < audioPaths.size(); i++) {
                Log.i("Audio Paths", audioPaths.get(i));
            }
        }
        Log.i("Result Code", String.valueOf(resultCode));

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            File file = new File(cameraIntentImgPath);
            if (file.exists()) {

                list.add(new ProjectItem(
                        0,
                        cameraIntentImgPath,
                        24324,
                        true,
                        "asdsad",
                        3244,
                        324234,
                        1));

                adapter.notifyDataSetChanged();

                cameraIntentImgPath = null;
            }
        }
    }

    // Boilerplate methods starts from here

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

    private void askForStoragePermission() {

        //https://www.androidhive.info/2016/11/android-working-marshmallow-m-runtime-permissions/
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

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return list;
    }

    public void addDetails(View v)
    {
        Intent i = new Intent(this, AddProjectDetails.class);
        startActivity(i);

    }

}
