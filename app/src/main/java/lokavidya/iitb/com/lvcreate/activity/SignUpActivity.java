package lokavidya.iitb.com.lvcreate.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.network.NetworkCommunicator;
import lokavidya.iitb.com.lvcreate.network.NetworkException;
import lokavidya.iitb.com.lvcreate.network.NetworkResponse;
import lokavidya.iitb.com.lvcreate.util.Master;

public class SignUpActivity extends AppCompatActivity {

    EditText name, email, phone, aadhar, password, cnfPassword;
    TextView register;
    private NetworkCommunicator networkCommunicator;
    private Toolbar toolbar;
    private View toolbarView;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        networkCommunicator = NetworkCommunicator.getInstance();
        configureToolBar();
        name = findViewById(R.id.et_sign_up_user_name);
        email = findViewById(R.id.et_sign_up_email_id);
        phone = findViewById(R.id.et_sign_up_phone_number);
        aadhar = findViewById(R.id.et_sign_up_aadhar_number);
        password = findViewById(R.id.et_signup_password);
        cnfPassword = findViewById(R.id.et_sign_up_confirm_password);

        register = findViewById(R.id.btn_sign_up_next);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUesr();

            }
        });

    }

    protected void configureToolBar() {

        toolbar = findViewById(R.id.signup_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetsAbsolute(0, 0);
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        toolbarView = layoutInflater.inflate(R.layout.back_button_layout, null);
        ivBack = toolbarView.findViewById(R.id.iv_back_icon);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(toolbarView);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (networkCommunicator == null) {
            networkCommunicator = NetworkCommunicator.getInstance();
        }
    }

    // First invoked when Register button is clicked
    // Validation and Text Retrieval
    void registerUesr() {
        String strName = name.getText().toString();
        String strEmail = email.getText().toString();
        String strPhone = phone.getText().toString();
        String strAadhar = aadhar.getText().toString();
        String pass = password.getText().toString();
        String cngPass = cnfPassword.getText().toString();

        if(pass.equals(cngPass)) {
            if(validate(strName, strEmail, strPhone)) {
                Master.showProgressDialog(SignUpActivity.this, getString(R.string.pdialog_loading));
                registerUser(strName, strEmail, strPhone, strAadhar, pass);
            }else {
                name.setError("Please verify it!");
                email.setError("Please verify it!");
                phone.setError("Please verify it!");
                Toast.makeText(this, "Please fill all details correctly!", Toast.LENGTH_LONG).show();
            }
        }else {
            cnfPassword.setError("Password must be same!");
        }
    }

    // Method overloading with all the params to register a user
    // JSON Parsing and volley network calls
    void registerUser(final String strName, final String strEmail, final String strPhone, String strAadhar, String pass) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject userObject = new JSONObject();
            userObject.put("name", strName);
            userObject.put("email", strEmail);
            userObject.put("phone", strPhone);
            userObject.put("aadhaar", strAadhar);
            userObject.put("password", pass);
            userObject.put("password_confirmation", pass);
            jsonObject.put("user", userObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        networkCommunicator.data(Master.getRegistrationAPI(),
                Request.Method.POST,
                jsonObject,
                false, new NetworkResponse.Listener() {

                    @Override
                    public void onResponse(Object result) {
                        Master.dismissProgressDialog();
                        String response = (String) result;
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (obj != null) {
                            try {
                                if(obj.has("status")) {
                                    String res = obj.get("status").toString();
                                    if (res.equals("200")) {
                                        proceedToLogin(strName, strEmail, strPhone);
                                    }
                                }else {
                                    if(obj.has("phone")) {
                                        Toast.makeText(SignUpActivity.this, "User already registered!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new NetworkResponse.ErrorListener() {
                    @Override
                    public void onError(NetworkException error) {
                        Master.dismissProgressDialog();
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Mobile number already exists!", Toast.LENGTH_LONG).show();
                    }

                }, "Register", getApplicationContext());

    }

    // Once user has registered successfully
    // Login the user and save the login instance with Shared Pref's
    void proceedToLogin(String strName, String  strEmail, String  strPhone) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        sharedPreferences.edit().putString("idToken","LoggedIn").apply();
        sharedPreferences.edit().putString("UserName",strName).apply();
        sharedPreferences.edit().putString("UserPhone",strPhone).apply();

        Toast.makeText(SignUpActivity.this, "Welcome " + name, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
        finishAffinity();
        startActivity(intent);
    }

    // Basic Boilerplate validation
    boolean validate(String name, String email, String phone) {
        if(name.length() <= 0) {
            return false;
        }else if(email.length() <= 0) {
            return false;
        }else if(phone.length() != 10) {
            return false;
        }else if(password.length() < 8){
            return false;
        }else {
            return true;
        }
    }
}
