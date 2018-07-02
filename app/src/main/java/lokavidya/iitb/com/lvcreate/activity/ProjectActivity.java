package lokavidya.iitb.com.lvcreate.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
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

public class ProjectActivity extends AppCompatActivity {

    // Request codes for File intents
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_PICK_IMAGE = 2;
    public static final int REQUEST_PICK_VIDEO = 3;
    public static final int REQUEST_PICK_AUDIO = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        // Get Shared Pref instance
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Retrieve project name from sharedpref
        String projectName = preferences.getString("ProjectName", "");

        // Find views
        Toolbar toolBar = findViewById(R.id.toolbar2);

        // Setup Toolbar
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle(projectName);

    }

    public void addImage(View view) {

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogView = inflater.inflate(R.layout.add_image_layout, null);
        final AlertDialog dialog = new AlertDialog.Builder(ProjectActivity.this).create();

        dialog.setView(dialogView);

        // we don't want title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.add_image_layout);

        // Bind views from dialog layout
        TextView clickPhoto = dialogView.findViewById(R.id.click_photo);
        TextView choosePhoto = dialogView.findViewById(R.id.choose_photo);

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

                ArrayList<String> filePaths = null;

                FilePickerBuilder.getInstance().setMaxCount(50)
                        .setSelectedFiles(filePaths)
                        .setActivityTheme(R.style.LibAppTheme)
                        .enableImagePicker(true)
                        .enableVideoPicker(false)
                        .pickPhoto(ProjectActivity.this, REQUEST_PICK_IMAGE);

                dialog.dismiss();
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
                .pickPhoto(ProjectActivity.this, REQUEST_PICK_VIDEO);
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

        ArrayList<String> photoPaths;
        ArrayList<String> videoPaths;
        ArrayList<String> audioPaths;

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            photoPaths = new ArrayList<>();
            photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));

            for (int i = 0; i < photoPaths.size(); i++) {
                Log.i("Photo Paths", photoPaths.get(i));
            }
        }

        if (requestCode == REQUEST_PICK_VIDEO && resultCode == RESULT_OK && data != null) {
            videoPaths = new ArrayList<>();
            videoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));

            for (int i = 0; i < videoPaths.size(); i++) {
                Log.i("Video Paths", videoPaths.get(i));
            }
        }

        if (requestCode == REQUEST_PICK_AUDIO && resultCode == RESULT_OK && data != null) {
            audioPaths = new ArrayList<>();
            audioPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));

            for (int i = 0; i < audioPaths.size(); i++) {
                Log.i("Audio Paths", audioPaths.get(i));
            }
        }

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

        return image;
    }

}
