package lokavidya.iitb.com.lvcreate.fileManagement;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class ProjectFolderCreation {

    public boolean createFolderStructure(Context context, String folderName) {

        boolean status = true;

        try {
            String directoryPath = context.getExternalFilesDir("Projects").getAbsolutePath();
            Log.d("AAD", "Project folder : " + directoryPath);

            String imagesFolderPath = "images";
            String videosFolderPath = "videos";
            String audioFolderPath = "audio";

            File dir;

            dir = new File(directoryPath, folderName);
            status &= createFolder(dir);

            String projectFolder = directoryPath + "/" + folderName;

            dir = new File(projectFolder, imagesFolderPath);
            status &= createFolder(dir);

            dir = new File(projectFolder, videosFolderPath);
            status &= createFolder(dir);

            dir = new File(projectFolder, audioFolderPath);
            status &= createFolder(dir);

        } catch (Exception e) {
            Log.e("AAD", "Error in making folders");
            status &= false;
        }
        return status;
    }


    public boolean createFolder(File folderDirectory) {
        if (!folderDirectory.exists()) {
            if (!folderDirectory.mkdirs()) {
                Log.e("AAD", "folder creation failed");
                return false;
            } else {
                Log.d("AAD", "created folder");
                return true;
            }

        }
        return false;
    }

    public void deleteFolder() {

    }

}
