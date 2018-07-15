package lokavidya.iitb.com.lvcreate.util;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

public class CloudUploadLoader extends AsyncTaskLoader<String> {

    /**
     * WE DONT NEED THIS ANYMORE
     * THIS IS ASYNC TASK LOADER
     */

    private String fileUrl = null;

    public CloudUploadLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {

        // This is the input path for the .zip you created
        Uri baseUri = Uri.parse(String.valueOf(
                Environment.getExternalStorageDirectory()) + "/Sounds.zip");

        try {
            fileUrl = CloudStorage.uploadFile(getContext(),
                    "lvcms-development-testing",
                    "test11.zip", // Name of the .zip on the Bucket
                    baseUri);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileUrl;

    }


}
