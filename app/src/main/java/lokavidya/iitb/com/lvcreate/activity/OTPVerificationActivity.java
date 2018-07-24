package lokavidya.iitb.com.lvcreate.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class OTPVerificationActivity extends AppCompatActivity implements View.OnClickListener {

    // Fields for generic views
    private Toolbar toolbar;
    private View toolbarView;
    private ImageView ivBackIcon;
    private TextView tvTitle;
    private ImageView ivBack;
    String uuid;

    // Fields for OTP Form views
    private TextView mResendOTPTextView;
    private TextView mClickHereTextView;
    private EditText mOTPEditText1;
    private EditText mOTPEditText2;
    private EditText mOTPEditText3;
    private EditText mOTPEditText4;
    private TextView mOTPVerifyTextView;
    private boolean otpEditTextStatus;

    private TextView mOTPProgressBarTextView;


    private ProgressBar mOTPProgressBar;
    private CountDownTimer mCountDownTimer;
    int i = 0;
    private String from;
    private String forgotData;
    private boolean isCountdownRunning;


    private String userEmail = "";
    private String userName = "";
    private String userPhoneNumber = "";

    private String deeplinkMemberInviteData;
    private int groupId;
    private int roleId;
    private NetworkCommunicator networkCommunicator;
    private BroadcastReceiver otpSMSBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("------ in Broadcast");
            if (intent.getAction().equalsIgnoreCase("otp_sms_receive")) {
                final String message = intent.getStringExtra("message");

                Log.d("otp message", message + "");

                try {
                    setOTPData(message.trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };

    protected void configureToolBar() {

        toolbar = findViewById(R.id.otp_verify_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetsAbsolute(0, 0);
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        toolbarView = layoutInflater.inflate(R.layout.layout_back_button, null);
        ivBack = toolbarView.findViewById(R.id.iv_back_icon);
        ivBack.setOnClickListener(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(toolbarView);
        }

    }

    private void initializeViews() {
        networkCommunicator = NetworkCommunicator.getInstance();
        mOTPProgressBar = findViewById(R.id.pb_otp);
        mOTPProgressBarTextView = findViewById(R.id.tv_otp_progress);
        mResendOTPTextView = findViewById(R.id.tv_resend_otp);
        mClickHereTextView = findViewById(R.id.tv_otp_click_here);
        mOTPEditText1 = findViewById(R.id.et_otp_1);
        mOTPEditText2 = findViewById(R.id.et_otp_2);
        mOTPEditText3 = findViewById(R.id.et_otp_3);
        mOTPEditText4 = findViewById(R.id.et_otp_4);
        mOTPVerifyTextView = findViewById(R.id.tv_otp_verify);
        mOTPEditText1.requestFocus();

    }

    private void setOnClickListener() {
        mOTPVerifyTextView.setOnClickListener(this);
        mResendOTPTextView.setOnClickListener(this);
        mClickHereTextView.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        // Check if we have permission to RECEIVE_SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                Toast.makeText(this, "Granting permission is necessary!", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        0);
            }
        }

        configureToolBar();
        initializeViews();
        setOnClickListener();

        setOnOTPEditTextListener();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back_icon:
                finish();
                break;

            case R.id.tv_resend_otp:
                hitResendOtp();
                break;

            case R.id.tv_otp_click_here:
                hitResendOtp();
                break;

            case R.id.tv_otp_verify:
                // Hide keyboard
                InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                methodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (otpEditTextStatus) {
                    verifyOtp();
                }
                break;
        }
    }

    private void verifyOtp() {
        // Retrieve 4 Digit OTP Code from Textviews and call verifyOTP method with String
        if (mOTPEditText1.getText().toString().length() == 0 ||
                mOTPEditText2.getText().toString().length() == 0 ||
                mOTPEditText3.getText().toString().length() == 0 ||
                mOTPEditText4.getText().toString().length() == 0) {
            Toast.makeText(this, "Please enter otp!", Toast.LENGTH_LONG).show();
            return;
        } else {
            String str = mOTPEditText1.getText().toString() +
                    mOTPEditText2.getText().toString() +
                    mOTPEditText3.getText().toString() +
                    mOTPEditText4.getText().toString();
            verifyOTP(str);
        }
    }

    // Network Queue and JSON Parsing goes here
    void verifyOTP(String otp) {
        Master.showProgressDialog(this, "Verifying OTP!");

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
                                    Intent intent = new Intent(OTPVerificationActivity.this, ResetPasswordActivity.class);
                                    intent.putExtras(args);
                                    startActivity(intent);

                                } else {
                                    Snackbar.make(mOTPVerifyTextView, "OTP is wrong\nPlease enter correct otp.", Snackbar.LENGTH_LONG).show();
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


                }, "ForgotPassword", OTPVerificationActivity.this);

    }

    // Method to Resend OTP similar to Send OTP
    private void hitResendOtp() {
        String data = getIntent().getStringExtra("id");
        String type = getIntent().getStringExtra("type");
        Master.showProgressDialog(OTPVerificationActivity.this, "Resending OTP!");
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

                                if (res.equals("200") && message.equals("OTP Sent Please Generate New Password")) {
                                    uuid = obj.getString("uuid");
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


                }, "ForgotPassword", OTPVerificationActivity.this);
    }

    private void checkOTPEditTextStatus() {


        if (!TextUtils.isEmpty(mOTPEditText1.getText().toString()) &&
                !TextUtils.isEmpty(mOTPEditText2.getText().toString()) &&
                !TextUtils.isEmpty(mOTPEditText3.getText().toString()) &&
                !TextUtils.isEmpty(mOTPEditText4.getText().toString())) {
            otpEditTextStatus = true;
            enableOTPVerifyButton();
        } else {
            disableOTPVerifyButton();
            otpEditTextStatus = false;
        }

    }

    private void disableOTPVerifyButton() {
        mOTPVerifyTextView.setBackgroundResource(R.drawable.verify_btn_bg);
    }

    private void enableOTPVerifyButton() {
        mOTPVerifyTextView.setBackgroundResource(R.drawable.verify_btn_enabled_bg);
    }

    private void setCountDownTimer() {
        mOTPProgressBar.setProgress(i);


        mCountDownTimer = new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {
                long progress = (300000 - millisUntilFinished) / 1000;
                mOTPProgressBar.setProgress((int) ((progress / 60.0) * 100));

//                Log.d("progress", progress + "");
//                Log.d("set progress", (int) ((progress / 60.0) * 100) + "");

                mOTPProgressBarTextView.setText(String.valueOf(progress).concat(" sec"));

//                Log.d("progress value", String.valueOf(millisUntilFinished / 1000) + "");
//                Log.d("progress bar", String.valueOf((progress / 60.0) * 100) + "");

            }

            public void onFinish() {
                mOTPProgressBar.setProgress(500);
                mOTPProgressBarTextView.setText("Timeout!");
                isCountdownRunning = false;
            }

        }.start();
        isCountdownRunning = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(otpSMSBroadcastReceiver);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (networkCommunicator == null) {
            networkCommunicator = NetworkCommunicator.getInstance();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(otpSMSBroadcastReceiver, new IntentFilter("otp_sms_receive"));
    }

    // Listeners to toggle enable/disable OTP Verify button
    private void setOnOTPEditTextListener() {
        mOTPEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("count ", count + "");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("edit text 1 ", s.toString() + "");
                checkOTPEditTextStatus();
                if (!TextUtils.isEmpty(mOTPEditText1.getText().toString())) {
                    mOTPEditText2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mOTPEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count == 1) {
                    mOTPEditText1.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("edit text 2 ", s.toString() + "");

                checkOTPEditTextStatus();
                if (!TextUtils.isEmpty(mOTPEditText2.getText().toString())) {
                    mOTPEditText3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mOTPEditText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count == 1) {
                    mOTPEditText2.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("edit text 3 ", s.toString() + "");
                checkOTPEditTextStatus();
                if (!TextUtils.isEmpty(mOTPEditText3.getText().toString())) {
                    mOTPEditText4.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mOTPEditText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count == 1) {
                    mOTPEditText3.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("edit text 4 ", s.toString() + "");

                checkOTPEditTextStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setOTPData(String otpMessage) {

        if (otpMessage != null && otpMessage.length() == 4) {
            mOTPEditText1.setText(String.valueOf(otpMessage.charAt(0)));
            mOTPEditText2.setText(String.valueOf(otpMessage.charAt(1)));
            mOTPEditText3.setText(String.valueOf(otpMessage.charAt(2)));
            mOTPEditText4.setText(String.valueOf(otpMessage.charAt(3)));
            mOTPEditText4.setSelection(String.valueOf(otpMessage.charAt(3)).length());
        }

    }


}
