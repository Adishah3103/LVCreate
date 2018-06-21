package lokavidya.iitb.com.lvcreate.util;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Master {

    // We store all the CONSTANT STRING Values here

    private static final String serverURL = "http://ssonew.lokavidya.com/api";

    public static String getLoginAPI() {
        return serverURL + "/login";
    }

    public static String getRegistrationAPI() {
        return serverURL + "/signup";
    }

    public static String getForgotPasswordAPI() {
        return serverURL + "/forgot";
    }

    public static String getOTPCheckAPI() {
        return serverURL + "/check";
    }

    public static String getResetPasswordAPI() {
        return serverURL + "/reset";
    }

    private static ProgressDialog pDialog;

    private static String currentProjectName;

    public static JSONObject userObject;

    public static String getCurrentProjectName() {
        return currentProjectName;
    }

    public static void setCurrentProjectName(String currentProjectName) {
        Master.currentProjectName = currentProjectName;
    }

    public static String AUTH_USERNAME = "lv@cse.iitb.ac.in", AUTH_PASSWORD = "password";

    public static List<String> imageNames = new ArrayList<>();
    public static List<String> audioNames = new ArrayList<>();
    public static List<String> videosNames = new ArrayList<>();

    public static void showProgressDialog(Context context, String message) {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public static void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }
}
