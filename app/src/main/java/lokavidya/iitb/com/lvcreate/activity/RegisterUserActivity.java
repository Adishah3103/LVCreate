package lokavidya.iitb.com.lvcreate.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.network.NetworkCommunicator;
import lokavidya.iitb.com.lvcreate.network.NetworkException;
import lokavidya.iitb.com.lvcreate.network.NetworkResponse;
import lokavidya.iitb.com.lvcreate.util.Master;

public class RegisterUserActivity extends AppCompatActivity {

    EditText name, email, phone, aadhar, password, cnfPassword;
    Button register;
    private NetworkCommunicator networkCommunicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        networkCommunicator = NetworkCommunicator.getInstance();

        name = findViewById(R.id.reg_name);
        email = findViewById(R.id.reg_email);
        phone = findViewById(R.id.reg_mobile);
        aadhar = findViewById(R.id.reg_aadhar);
        password = findViewById(R.id.reg_pass);
        cnfPassword = findViewById(R.id.reg_cnfPass);

        register = findViewById(R.id.reg_btn_register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUesr();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (networkCommunicator == null) {
            networkCommunicator = NetworkCommunicator.getInstance();
        }
    }

    void registerUesr() {
        String strName = name.getText().toString();
        String strEmail = email.getText().toString();
        String strPhone = phone.getText().toString();
        String strAadhar = aadhar.getText().toString();
        String pass = password.getText().toString();
        String cngPass = cnfPassword.getText().toString();

        if(pass.equals(cngPass)) {
            if(validate(strName, strEmail, strPhone)) {
                Master.showProgressDialog(RegisterUserActivity.this, getString(R.string.pdialog_loading));
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

    void registerUser(final String strName, final String strEmail, final String strPhone, String strAadhar, String pass) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject userObject = new JSONObject();
            userObject.put("name", strName);
            userObject.put("email", strEmail);
            userObject.put("phone", strPhone);
            //userObject.put("secondary_phones", new JSONArray());
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
                                        Toast.makeText(RegisterUserActivity.this, "User already registered!", Toast.LENGTH_LONG).show();
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

    void proceedToLogin(String strName,String  strEmail,String  strPhone) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        sharedPreferences.edit().putString("idToken","LoggedIn").apply();
        sharedPreferences.edit().putString("UserName",strName).apply();
        sharedPreferences.edit().putString("UserPhone",strPhone).apply();

        Toast.makeText(RegisterUserActivity.this, "Welcome " + name, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegisterUserActivity.this, DashboardActivity.class);
        finishAffinity();
        startActivity(intent);
    }

    boolean validate(String name, String email, String phone) {
        if(name.length() <= 0) {
            return false;
        }else if(email.length() <= 0) {
            return false;
        }else if(phone.length() != 10) {
            return false;
        }else {
            return true;
        }
    }
}
