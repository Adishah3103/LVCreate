package lokavidya.iitb.com.lvcreate.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

    ProgressDialog progressDialog;

    ProjectDb mDb;
    List<ProjectItem> list;
    Project currentProject;

    long projectId;
    String projectPath;
    String projectTitle;
    String projectDesc;

    EditText editProjectTitle;
    EditText editProjectDesc;
    Spinner spinVideoLang;
    Spinner spinChannel;
    Spinner spinSubChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project_details);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);


        // Get the project ID from intent
        Intent intent = getIntent();
        projectId = intent.getLongExtra("pid", -1);
        projectPath = intent.getStringExtra("projectPath");

        // Get database instance
        mDb = ProjectDb.getsInstance(getApplicationContext());
        if (projectId != -1) {

            currentProject = mDb.projectDao().loadItemById(projectId);
            projectTitle = currentProject.getTitle();
            projectDesc = currentProject.getDesc();

            // Execute query to load items with project ID
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Get the data back from Database
                    list = mDb.projectItemDao().loadItemsByProjectId(projectId);
                }
            });
        }

        editProjectTitle = findViewById(R.id.txt_project_title);
        editProjectDesc = findViewById(R.id.txt_project_desc);
        spinVideoLang = findViewById(R.id.spin_vid_lang);
        spinChannel = findViewById(R.id.spin_channel);
        spinSubChannel = findViewById(R.id.spin_sub_channel);

        //calling makeUI to generate Ui from boilerplate
        makeUI();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void saveProject(View view) {

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = new ProgressDialog(AddProjectDetails.this);
        progressDialog.setMessage("Converting & Copying..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Save project description in database
        projectDesc = editProjectDesc.getText().toString();
        if (!projectDesc.equals(" ")) {
            currentProject.setDesc(projectDesc);
            mDb.projectDao().updateItem(currentProject);
        }

        // Null check to save empty project
        if (list.isEmpty()) {

            Intent intent = new Intent(AddProjectDetails.this, OngoingProjects.class);
            getApplicationContext().startActivity(intent);
            finish();

        } else {

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

                                } else if (currentItem.getItemFilePath().contains(".png") ||
                                        currentItem.getItemFilePath().contains(".PNG")) {
                                    ManageFile.copyFile(currentItem.getItemFilePath(),
                                            destFilePath);
                                }

                                // Audio Conversion and move
                                String destAudioPath = projectPath + "/"
                                        + Master.AUDIOS_FOLDER
                                        + "/" + projectTitle + "." + currentItem.getOrder() + ".wav";

                                convertAudio(currentItem.getItemAudioPath(), destAudioPath);

                                if (i == list.size() - 1) {
                                    if (progressDialog != null && progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    Intent intent = new Intent(AddProjectDetails.this, OngoingProjects.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(intent);
                                    finish();
                                }

                            } else {
                                destFilePath = projectPath + "/"
                                        + Master.VIDEOS_FOLDER
                                        + "/" + projectTitle + "." + currentItem.getOrder() + ".mp4";
                                // Copy Video to Project Folder
                                ManageFile.copyFile(currentItem.getItemFilePath(), destFilePath);


                                if (i == list.size() - 1) {
                                    if (progressDialog != null && progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    Intent intent = new Intent(AddProjectDetails.this, OngoingProjects.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(intent);
                                    finish();
                                }
                            }

                            // Setting file path
                            currentItem.setItemFilePath(destFilePath);
                            currentItem.setOriginal(false);

                            // Update all paths in database
                            mDb.projectItemDao().updateItem(currentItem);
                        }
                    }

                    // Use the first file to generate thumbnail of project
                    currentProject.setFirstFileThumb(list.get(0).getItemFilePath());
                    mDb.projectDao().updateItem(currentProject);


                }
            });

            // Send broadcast to finish the Create Project
            Intent finishCreateProject = new Intent("finish_activity");
            sendBroadcast(finishCreateProject);
        }

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

    public void convertAudio(final String sourcePath, final String destinationPath) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

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
                AndroidAudioConverter.with(getApplicationContext())
                        .setFile(sourceFile) // Your current audio file
                        .setFormat(AudioFormat.WAV) // Your desired audio format
                        .setCallback(callback) // A callback to know when conversion is finished
                        .convert(); // Start conversion

            }
        });

    }

    public void makeUI() {

        getSupportActionBar().setTitle("Add Project Details");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Setup the title onto EditText
        editProjectTitle.setText(projectTitle);
        if (!projectDesc.equals(" "))
            editProjectDesc.setText(projectDesc);

        editProjectDesc.requestFocus();

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
        spinVideoLang.setAdapter(spinnerArrayAdapter);

        spinVideoLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        spinChannel.setAdapter(spinnerArrayAdapter2);

        spinChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    final EditText channelDesc = mView.findViewById(R.id.channel_descrip);
                    Button add = mView.findViewById(R.id.add);

                    final Spinner channelLang = mView.findViewById(R.id.channel_lang);

                    String[] lang = new String[]{
                            "Choose spinChannel language",
                            "English",
                            "Hindi"
                    };

                    final List<String> channelLangList = new ArrayList<>(Arrays.asList(lang));

                    final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                            AddProjectDetails.this, R.layout.spinner_layout, channelLangList) {
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
                    channelLang.setAdapter(spinnerArrayAdapter);

                    channelLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

                            if (channelName.getText().toString() != null && channelDesc.getText().toString() != null) {

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
        spinSubChannel.setAdapter(spinnerArrayAdapter3);

        spinSubChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

}
