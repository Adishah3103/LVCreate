package lokavidya.iitb.com.lvcreate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.network.NetworkCommunicator;
import lokavidya.iitb.com.lvcreate.network.NetworkException;
import lokavidya.iitb.com.lvcreate.network.NetworkResponse;
import lokavidya.iitb.com.lvcreate.util.Master;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String PHONE_NUMBER_REGEX = "(^)([\\d]){10}$";

    // Fields for views
    TextInputEditText mEnterEmailOrPhoneNoEditText;
    TextView mGenerateOtpButton, mSignupButton;
    private Toolbar toolbar;
    private View toolbarView;
    private ImageView ivBack;

    // Fields for Information storing
    String type;
    String uuid;
    String mEmailOrPhone;

    // Fields for Utils
    private NetworkCommunicator networkCommunicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);

        // Bind views here
        mEnterEmailOrPhoneNoEditText = findViewById(R.id.et_login_email_id);
        mGenerateOtpButton = findViewById(R.id.tv_reset_btn);
        networkCommunicator = NetworkCommunicator.getInstance();
        mSignupButton = findViewById(R.id.tv_sign_up);
        configureToolBar();

        // Generate OTP onClick
        mGenerateOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailOrPhone = mEnterEmailOrPhoneNoEditText.getText().toString().trim();
                if (mEmailOrPhone.length() == 0) {
                    mEnterEmailOrPhoneNoEditText.requestFocus();
                    mEnterEmailOrPhoneNoEditText.setError("Enter valid mobile number");
                    return;
                } else {
                    if (mEmailOrPhone.matches(PHONE_NUMBER_REGEX)) {
                        type = "phone";
                        sendOTP("phone", mEmailOrPhone);
                    } else {
                        type = "email";
                        sendOTP("email", mEmailOrPhone);
                    }
                }
            }
        });

        // SignUp onClick
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this, SignUpActivity.class));
            }
        });

    }

    protected void configureToolBar() {

        toolbar = findViewById(R.id.forgot_pwd_toolbar);
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

    // Method to queue Send OTP API Call
    void sendOTP(String type, String data) {

        Master.showProgressDialog(ForgotPasswordActivity.this, "Sending OTP!");

        JSONObject userJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        try {
            if (type.equals("phone")) {
                jsonObject.put("phone", data);
            } else {
                jsonObject.put("email", data);
            }
            userJsonObject.put("user", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        networkCommunicator.data(Master.getForgotPasswordAPI(),
                Request.Method.POST,
                userJsonObject,
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
                                String res = obj.getString("status");
                                String message = obj.getString("message");

                                // Upon 200 Success, call otpGenerated method to close activity
                                if (res.equals("200") && message.equals("OTP Sent Please Generate New Password")) {
                                    uuid = obj.getString("uuid");
                                    otpGenerated();
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
                    }


                }, "ForgotPassword", ForgotPasswordActivity.this);
    }

    void otpGenerated() {
        Snackbar.make(mGenerateOtpButton, "OTP Sent to the mobile number!", Snackbar.LENGTH_LONG).show();

        Intent intent = new Intent(this, OTPVerificationActivity.class);
        intent.putExtra("id", mEmailOrPhone);
        intent.putExtra("type", type);
        startActivity(intent);
    }

}
