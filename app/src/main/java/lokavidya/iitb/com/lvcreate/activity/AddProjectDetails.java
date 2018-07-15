package lokavidya.iitb.com.lvcreate.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.dbUtils.ProjectDb;
import lokavidya.iitb.com.lvcreate.fileManagement.ManageFile;
import lokavidya.iitb.com.lvcreate.model.Project;
import lokavidya.iitb.com.lvcreate.model.ProjectItem;
import lokavidya.iitb.com.lvcreate.util.AppExecutors;
import lokavidya.iitb.com.lvcreate.util.Master;

public class AddProjectDetails extends AppCompatActivity {

    public List<String> videoLangList;
    public List<String> channelList;
    public List<String> subChannelList;

    ProjectDb mDb;
    List<ProjectItem> list;

    Project currentProject;
    long projectId;
    String projectPath;
    String projectTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project_details);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add Project Details ");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Get the project ID from intent
        Intent intent = getIntent();
        projectId = intent.getLongExtra("pid", -1);
        projectPath = intent.getStringExtra("projectPath");

        // Get database instance
        mDb = ProjectDb.getsInstance(getApplicationContext());

        // Execute query to load items with project ID
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                if (projectId != -1) {
                    // Trying to get the data back from Database

                    list = mDb.projectItemDao().loadItemsByProjectId(projectId);

                    for (int j = 0; j < list.size(); j++) {
                        Log.i("DBRetrieve", list.get(j).getItemFilePath());
                    }

                    currentProject = mDb.projectDao().loadItemById(projectId);
                    projectTitle = currentProject.getTitle();
                }
            }
        });


        final Spinner videoLang = findViewById(R.id.video_lang);
        final Spinner channel = findViewById(R.id.channel);
        final Spinner subChannel = findViewById(R.id.subchannel);


        String[] video = new String[]{
                "Choose video language",
                "English",
                "Hindi"
        };

        String[] channels = new String[]{
                "Choose Channel",
                "Add new Channel",
                "Maths",
                "Science",
                "Geography",
                "Social Science",
                "Environment",
                "History"
        };

        String[] subchannnels = new String[]{
                "Choose Sub Channel",
                "AstroPhysics",
                "Meinkowskii Space Time Graph",
                "Relational Gravity",
                "Nuclear Stars",
                "Gravitational Wormholes",
                "Space Time Hyper Parameters",
                "Theory of Everyhing",
                "Trignometry",
                "Algebra",
                "Physics",
                "Chemistry",
                "Add new Sub Channel"
        };

        videoLangList = new ArrayList<>(Arrays.asList(video));
        channelList = new ArrayList<>(Arrays.asList(channels));
        subChannelList = new ArrayList<>(Arrays.asList(subchannnels));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, videoLangList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        videoLang.setAdapter(spinnerArrayAdapter);

        videoLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, channelList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        channel.setAdapter(spinnerArrayAdapter2);

        channel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint

                if (selectedItemText.equals("Add new Channel"))

                {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddProjectDetails.this);
                    View mView = getLayoutInflater().inflate(R.layout.new_channel, null);

                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();

                    final EditText channelName = mView.findViewById(R.id.channel_title);
                    final EditText channeldescription = mView.findViewById(R.id.channel_descrip);
                    Button add = mView.findViewById(R.id.add);

                    final Spinner channellang = mView.findViewById(R.id.channel_lang);

                    String[] lang = new String[]{
                            "Choose channel language",
                            "English",
                            "Hindi"

                    };

                    final List<String> channellanglist = new ArrayList<>(Arrays.asList(lang));

                    final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                            AddProjectDetails.this, R.layout.spinner_layout, channellanglist) {
                        @Override
                        public boolean isEnabled(int position) {
                            if (position == 0) {
                                // Disable the first item from Spinner
                                // First item will be use for hint
                                return false;
                            } else {
                                return true;
                            }
                        }

                        @Override
                        public View getDropDownView(int position, View convertView,
                                                    ViewGroup parent) {
                            View view = super.getDropDownView(position, convertView, parent);
                            TextView tv = (TextView) view;
                            if (position == 0) {
                                // Set the hint text color gray
                                tv.setTextColor(Color.GRAY);
                            } else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }
                    };
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
                    channellang.setAdapter(spinnerArrayAdapter);

                    channellang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItemText = (String) parent.getItemAtPosition(position);
                            // If user change the default selection
                            // First item is disable and it is used for hint
                            if (position > 0) {
                                // Notify the selected item text
                                Toast.makeText
                                        (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                                        .show();
                            }


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (channelName.getText().toString() != null && channeldescription.getText().toString() != null) {

                                String newchannel = channelName.getText().toString();
                                channelList.add(newchannel);
                                Toast.makeText(getApplicationContext(), "Channel created succesfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {

                                Toast.makeText(getApplicationContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });


                    dialog.show();


                }

                if (position > 0) {
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, subChannelList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        subChannel.setAdapter(spinnerArrayAdapter3);

        subChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void saveProject(View view) {

        //Master.showProgressDialog(getApplicationContext(), "Converting & Copying..");

        // Execute query to load items with project ID
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < list.size(); i++) {

                    ProjectItem currentItem = list.get(i);
                    String destFilePath;

                    if (currentItem.isOriginal()) {

                        if (currentItem.getItemIsAudio()) {

                            destFilePath = projectPath + "/"
                                    + Master.IMAGES_FOLDER
                                    + "/" + projectTitle + "." + currentItem.getOrder() + ".png";

                            if (currentItem.getItemFilePath().contains(".jpg") ||
                                    currentItem.getItemFilePath().contains(".JPG") ||
                                    currentItem.getItemFilePath().contains(".JPEG") ||
                                    currentItem.getItemFilePath().contains(".jpeg")) {

                                Log.i("Path", destFilePath);

                                convertImage(currentItem.getItemFilePath(), destFilePath);

                                // Setting file path
                                currentItem.setItemFilePath(destFilePath);

                                // Audio Conversion and move
                                String destAudioPath = projectPath + "/"
                                        + Master.AUDIOS_FOLDER
                                        + "/" + projectTitle + "." + currentItem.getOrder() + ".wav";

                                convertAudio(currentItem.getItemAudioPath(), destAudioPath);

                            } else if (currentItem.getItemFilePath().contains(".png") ||
                                    currentItem.getItemFilePath().contains(".PNG")) {
                                ManageFile.copyFile(currentItem.getItemFilePath(),
                                        destFilePath);
                            }


                        } else {
                            destFilePath = projectPath + "/"
                                    + Master.VIDEOS_FOLDER
                                    + "/" + projectTitle + "." + currentItem.getOrder() + ".mp4";
                            // Copy Video to Project Folder
                            ManageFile.copyFile(currentItem.getItemFilePath(), destFilePath);
                        }

                        currentItem.setOriginal(false);
                        // Update all paths in database
                        mDb.projectItemDao().updateItem(currentItem);
                    }

                }

            }
        });

        //Master.dismissProgressDialog();

    }

    public void convertImage(String sourcePath, String destinationPath) {

        boolean success = false;

        Bitmap bmp = BitmapFactory.decodeFile(sourcePath);
        File convertedImage = new File(destinationPath);

        try {

            FileOutputStream outStream = new FileOutputStream(convertedImage);
            success = bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);

            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (success) {
            Log.i("Conversion", "Photo Converting is successful.");
        } else {
            Log.i("Conversion", "Photo Converting is unsuccessful.");
        }
    }

    public void convertAudio(String sourcePath, final String destinationPath) {

        File sourceFile = new File(sourcePath);

        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                // So fast? Love it!
                Log.i("Audio Conversion", convertedFile.getAbsolutePath());
                ManageFile.moveFile(convertedFile.getAbsolutePath(), destinationPath);
            }

            @Override
            public void onFailure(Exception error) {
                // Oops! Something went wrong
                error.printStackTrace();
            }
        };
        AndroidAudioConverter.with(this)
                .setFile(sourceFile) // Your current audio file
                .setFormat(AudioFormat.WAV) // Your desired audio format
                .setCallback(callback) // A callback to know when conversion is finished
                .convert(); // Start conversion
    }

}
