package lokavidya.iitb.com.lvcreate.util;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Master {

    // We store all the CONSTANT STRING Values here

    /**
     * SSO API's Start here
     **/

    private static final String SSO_SERVER_URL = "http://ssonew.lokavidya.com/api";
    /**
     * CMS API's Start here
     **/

    private static final String CMS_SERVER_URL = "https://lvcms.herokuapp.com/api/v1/";

    public static String getLoginAPI() {
        return SSO_SERVER_URL + "/login";
    }

    public static String getRegistrationAPI() {
        return SSO_SERVER_URL + "/signup";
    }

    public static String getForgotPasswordAPI() {
        return SSO_SERVER_URL + "/forgot";
    }

    public static String getOTPCheckAPI() {
        return SSO_SERVER_URL + "/check";
    }

    public static String getResetPasswordAPI() {
        return SSO_SERVER_URL + "/reset";
    }

    public static String getMostViewedAPI() {
        return SSO_SERVER_URL + "search/views";
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
