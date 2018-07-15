package lokavidya.iitb.com.lvcreate.fileManagement;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import lokavidya.iitb.com.lvcreate.util.Master;

public class ZipTask extends AsyncTask<Void, Void, Void> /* Params, Progress, Result */ {


    private final String sourceFolderPath, outputFolderPath;
    private final String folderName;
    private final Context context;

    public ZipTask(Context context, String sourceFolderPath, String outputFolderPath) {
        this.context = context;
        this.sourceFolderPath = sourceFolderPath;
        this.outputFolderPath = outputFolderPath;
        this.folderName = sourceFolderPath.substring(sourceFolderPath.lastIndexOf("/") + 1);
    }

    @Override
    protected void onPreExecute() {
        log("got the zip task!" + folderName);
        Master.showProgressDialog(context, "Zipping...");

    }

    @Override
    protected Void doInBackground(Void... params) {

        log("started zipping: " + folderName);
        ManageZip zfd = new ManageZip();
        zfd.zipFileAtPath(sourceFolderPath, outputFolderPath);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        log("completed zipping: " + folderName);
        Master.dismissProgressDialog();
    }

    private void log(String msg) {
        Log.d("AAD", "Zipask: " + msg);
    }
}