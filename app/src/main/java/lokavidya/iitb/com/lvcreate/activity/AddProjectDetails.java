package lokavidya.iitb.com.lvcreate.activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lokavidya.iitb.com.lvcreate.R;

public class AddProjectDetails extends AppCompatActivity {

    public List<String> videolangList;
    public List<String> channellist;
    public List<String> subchannellist;

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
        final Spinner videolang = (Spinner) findViewById(R.id.video_lang);
        final Spinner channel = (Spinner) findViewById(R.id.channel);
        final Spinner subchannel = (Spinner) findViewById(R.id.subchannel);


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

         videolangList = new ArrayList<>(Arrays.asList(video));
         channellist = new ArrayList<>(Arrays.asList(channels));
        subchannellist = new ArrayList<>(Arrays.asList(subchannnels));




        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_layout,videolangList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        videolang.setAdapter(spinnerArrayAdapter);

        videolang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
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
                this,R.layout.spinner_layout,channellist){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
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

                if(selectedItemText.equals("Add new Channel"))

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
                            AddProjectDetails.this,R.layout.spinner_layout,channellanglist){
                        @Override
                        public boolean isEnabled(int position){
                            if(position == 0)
                            {
                                // Disable the first item from Spinner
                                // First item will be use for hint
                                return false;
                            }
                            else
                            {
                                return true;
                            }
                        }
                        @Override
                        public View getDropDownView(int position, View convertView,
                                                    ViewGroup parent) {
                            View view = super.getDropDownView(position, convertView, parent);
                            TextView tv = (TextView) view;
                            if(position == 0){
                                // Set the hint text color gray
                                tv.setTextColor(Color.GRAY);
                            }
                            else {
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
                            if(position > 0){
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

                            if(channelName.getText().toString()!=null && channeldescription.getText().toString()!= null)
                            {

                                String newchannel = channelName.getText().toString();
                                channellist.add(newchannel);
                                Toast.makeText(getApplicationContext(),"Channel created succesfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                            else
                            {

                                Toast.makeText(getApplicationContext(),"Please enter all details", Toast.LENGTH_SHORT).show();
                            }





                        }
                    });






                    dialog.show();


                }

                if(position > 0){
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
                this,R.layout.spinner_layout,subchannellist){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        subchannel.setAdapter(spinnerArrayAdapter3);

        subchannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
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
}
