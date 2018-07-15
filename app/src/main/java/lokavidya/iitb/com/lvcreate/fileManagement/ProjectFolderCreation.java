package lokavidya.iitb.com.lvcreate.fileManagement;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class ProjectFolderCreation {

    public boolean createFolderStructure(Context context, String projectFolderName) {

        boolean status = false;

        try {
            String directoryPath = context.getExternalFilesDir("Projects").getAbsolutePath();
            Log.d("AAD", "Project folder : " + directoryPath);

            String imagesFolderPath = "/" + projectFolderName + "/images/";
            String videosFolderPath = "/" + projectFolderName + "/videos/";
            String audioFolderPath = "/" + projectFolderName + "/audio/";

            File dir;

            dir = new File(directoryPath + "/" + projectFolderName + "/");
            createFolder(dir);

            dir = new File(directoryPath + imagesFolderPath);
            createFolder(dir);

            dir = new File(directoryPath + videosFolderPath);
            createFolder(dir);

            dir = new File(directoryPath + audioFolderPath);
            createFolder(dir);
            status = true;
        } catch (Exception e) {
            Log.e("AAD", "Error in making folders");
            status = false;
        }
        return status;
    }


    public void createFolder(File folderDirectory) {
        if (!folderDirectory.exists()) {
            Log.d("AAD", "created folder");
            folderDirectory.mkdir();
        }
    }

}
