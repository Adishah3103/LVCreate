package lokavidya.iitb.com.lvcreate.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class WelcomeActivity extends FragmentActivity implements View.OnClickListener {

    public static int MY_REQUEST_CODE3;
    public int check = 0;
    boolean fromUpload;
    TextView signUpButton, signInButton;
    SharedPreferences sharedPreferences;
    EditText phone, password;
    private NetworkCommunicator networkCommunicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent i = getIntent();
        fromUpload = i.getBooleanExtra("fromUpload", false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putBoolean("Skip", false).apply();
        if (!sharedPreferences.getString("idToken", "N/A").equals("N/A")/* && sharedPreferences.getString("lokavidyaToken", "N/A").equals("N/A")*/) {
            Intent projectsIntent = new Intent(WelcomeActivity.this, DashboardActivity.class);
            startActivity(projectsIntent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        networkCommunicator = NetworkCommunicator.getInstance();

        signInButton = findViewById(R.id.si_signin);
        signUpButton = findViewById(R.id.sign_up_button);

        phone = findViewById(R.id.et_login_email_id);
        password = findViewById(R.id.et_login_password);

        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (networkCommunicator == null) {
            networkCommunicator = NetworkCommunicator.getInstance();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.si_signin:
                String mobile = phone.getText().toString();
                String pass = password.getText().toString();

                if (mobile.length() == 10) {
                    if (pass.length() > 0) {
                        Master.showProgressDialog(WelcomeActivity.this, getString(R.string.pdialog_loading));
                        checkLoginInfo(mobile, pass);
                    } else {
                        password.setError("Enter valid password!");
                    }
                } else {
                    phone.setError("Enter valid mobile no");
                }
                break;
            case R.id.sign_up_button:
                Intent intent = new Intent(this, RegisterUserActivity.class);
                startActivity(intent);
                break;
            /*case R.id.skipbutton:
                check = 3;
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WelcomeActivity.this);
                sharedPreferences.edit().putBoolean("Skip", true).apply();
                Intent i = new Intent(this, DashboardActivity.class);

                if (weHavePermissionToReadContacts()) {
                    startActivity(i);
                    finish();
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_REQUEST_CODE3);
                }*/
        }
    }

    void checkLoginInfo(final String mobile, String pass) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject userObject = new JSONObject();
            userObject.put("phone", mobile);
            userObject.put("password", pass);
            userObject.put("password_confirmation", pass);
            jsonObject.put("user", userObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        networkCommunicator.data(Master.getLoginAPI(),
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
                                String res = obj.get("status").toString();
                                if (res.equals("200")) {
                                    requestLogin(obj.get("session_name").toString(), mobile);
                                } else if (res.equals("404")) {
                                    Toast.makeText(WelcomeActivity.this, "You are not a member of LokaVidya!\nPlease register first!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(WelcomeActivity.this, "Please enter valid credentials", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_technical_issue), Toast.LENGTH_LONG).show();
                    }


                }, "Login", getApplicationContext());

    }

    void requestLogin(String name, String strPhone) {
        sharedPreferences.edit().putString("idToken","LoggedIn").apply();
        sharedPreferences.edit().putString("UserName",name).apply();
        sharedPreferences.edit().putString("UserPhone",strPhone).apply();

        Toast.makeText(WelcomeActivity.this, "Welcome " + name, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(WelcomeActivity.this, DashboardActivity.class);
        finish();
        startActivity(intent);
    }

    private boolean weHavePermissionToReadContacts() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else return false;
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
