package lokavidya.iitb.com.lvcreate.fileManagement;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import lokavidya.iitb.com.lvcreate.util.Master;

public class MoveTask extends AsyncTask<Void, Void, Void> /* Params, Progress, Result */ {


    private final String inputPath, outputPath;
    private final String fileName;
    private final boolean isFirstTask, isLastTask;
    private final Context context;

    public MoveTask(Context context, String inputPath, String outputPath, boolean isFirstTask, boolean isLastTask) {
        this.context = context;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.isFirstTask = isFirstTask;
        this.isLastTask = isLastTask;
        this.fileName = inputPath.substring(inputPath.lastIndexOf("/") + 1);
    }

    @Override
    protected void onPreExecute() {
        log("got the move task!" + fileName);
        if (isFirstTask) {
            Master.showProgressDialog(context, "Moving...");
        }

    }

    @Override
    protected Void doInBackground(Void... params) {

        log("started moving: " + fileName);
        ManageFile fm = new ManageFile();
        fm.moveFile(inputPath, outputPath);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        log("completed moving: " + fileName);
        if (isLastTask) {
            Master.dismissProgressDialog();
        }
    }

    private void log(String msg) {
        Log.d("AAD", "MoveTask: " + msg);
    }
}