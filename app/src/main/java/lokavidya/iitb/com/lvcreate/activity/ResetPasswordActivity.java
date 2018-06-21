package lokavidya.iitb.com.lvcreate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

public class ResetPasswordActivity extends AppCompatActivity {

    // Fields for views
    EditText passwordEdt;
    EditText confirmPasswordEdt;
    TextView mConfirmButton;

    String uuid;

    private NetworkCommunicator networkCommunicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Bundle args = getIntent().getExtras();
        uuid = args.getString("uuid");

        passwordEdt = findViewById(R.id.et_new_password);
        confirmPasswordEdt = findViewById(R.id.et_confirm_password);
        mConfirmButton = findViewById(R.id.tv_reset_btn);

        // Get Singleton instance of NetworkCommunicator
        networkCommunicator = NetworkCommunicator.getInstance();

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!uuid.equals("")) {
                    if (passwordEdt.getText().toString().length() > 0) {
                        String pass = passwordEdt.getText().toString();
                        String cnfPass = confirmPasswordEdt.getText().toString();

                        if (pass.equals(cnfPass)) {
                            setNewPassword(pass);
                        } else {
                            confirmPasswordEdt.setError("Password must be same!");
                        }
                    } else {
                        passwordEdt.setError("Please enter valid password!");
                    }
                } else {
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.toast_technical_issue), Toast.LENGTH_LONG).show();
                    mConfirmButton.setEnabled(false);
                }
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

    // Volley Network call and JSON Parsing
    void setNewPassword(String password) {
        Master.showProgressDialog(ResetPasswordActivity.this, "Setting up new password for you");
        JSONObject userJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("uuid", uuid);
            jsonObject.put("password", password);
            userJsonObject.put("user", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        networkCommunicator.data(Master.getResetPasswordAPI(),
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

                                if (res.equals("200") && message.equals("Password Updated")) {
                                    Intent intent = new Intent(ResetPasswordActivity.this, WelcomeActivity.class);
                                    finishAffinity();
                                    startActivity(intent);
                                } else if (res.equals("400") && message.equals("Your new password cannot be same as last password")) {
                                    Toast.makeText(ResetPasswordActivity.this, "Your new password cannot be same as last password", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.toast_technical_issue), Toast.LENGTH_LONG).show();
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


                }, "ResetPassword", ResetPasswordActivity.this);
    }
}
