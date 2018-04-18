package lokavidya.iitb.com.lvcreate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    EditText mEnterEmailOrPhoneNoEditText;
    Button mGenerateOtpButton;

    EditText mEnterOtp;
    Button mVerifyOtp;
    String uuid;

    String mEmailOrPhone;
    private NetworkCommunicator networkCommunicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mEnterEmailOrPhoneNoEditText = findViewById(R.id.et_enter_email_phone_no);
        mGenerateOtpButton = findViewById(R.id.btn_generate_otp);
        networkCommunicator = NetworkCommunicator.getInstance();

        mEnterOtp = findViewById(R.id.et_enter_otp);
        mVerifyOtp = findViewById(R.id.btn_verify_otp);

        mGenerateOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailOrPhone = mEnterEmailOrPhoneNoEditText.getText().toString().trim();
                if (mEmailOrPhone.length() == 0) {
                    mEnterEmailOrPhoneNoEditText.requestFocus();
                    mEnterEmailOrPhoneNoEditText.setError("Please enter valid mobile number");
                    return;
                } else {
                    if (mEmailOrPhone.matches(PHONE_NUMBER_REGEX)) {
                        sendOTP("phone", mEmailOrPhone);
                    } else {
                        sendOTP("email", mEmailOrPhone);
                    }
                }
            }
        });

        mVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEnterOtp.getText().toString().length() == 0) {
                    mEnterOtp.setError("Please enter otp!");
                    return;
                }else {
                    verifyOTP(mEnterOtp.getText().toString());
                }
            }
        });

    }

    void verifyOTP(String otp) {
        Master.showProgressDialog(ForgotPasswordActivity.this, "Verifying OTP!");
        JSONObject userJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("otp", otp);
            userJsonObject.put("user", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        networkCommunicator.data(Master.getOTPCheckAPI(),
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

                                if (res.equals("200") && message.equals("OTP verified.")) {
                                    uuid = obj.getString("uuid");
                                    Bundle args = new Bundle();
                                    args.putString("uuid", uuid);
                                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                                    intent.putExtras(args);
                                    startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (networkCommunicator == null) {
            networkCommunicator = NetworkCommunicator.getInstance();
        }
    }

    void sendOTP(String type, String data) {
        Master.showProgressDialog(ForgotPasswordActivity.this, "Sending OTP!");
        JSONObject userJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        try {
            if(type.equals("phone")) {
                jsonObject.put("phone", data);
            }else {
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

        mEnterOtp.setVisibility(View.VISIBLE);
        mVerifyOtp.setVisibility(View.VISIBLE);

        mEnterOtp.requestFocus();
    }

}
