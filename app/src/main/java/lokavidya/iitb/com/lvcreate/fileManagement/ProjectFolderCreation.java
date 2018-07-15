package lokavidya.iitb.com.lvcreate.fileManagement;

import android.content.Context;
import android.util.Log;

import java.io.File;

import lokavidya.iitb.com.lvcreate.util.Master;

public class ProjectFolderCreation {

    public static boolean createFolderStructure(Context context, String folderName) {

        boolean status = true;

        try {
            String directoryPath = context.getExternalFilesDir(Master.ALL_PROJECTS_FOLDER).getAbsolutePath();
            Log.d("AAD", "Project folder : " + directoryPath);

            File dir;

            dir = new File(directoryPath, folderName);
            status &= createFolder(dir);

            String projectFolder = directoryPath + "/" + folderName;

            dir = new File(projectFolder, Master.IMAGES_FOLDER);
            status &= createFolder(dir);

            dir = new File(projectFolder, Master.VIDEOS_FOLDER);
            status &= createFolder(dir);

            dir = new File(projectFolder, Master.AUDIOS_FOLDER);
            status &= createFolder(dir);

        } catch (Exception e) {
            Log.e("AAD", "Error in making folders");
            status &= false;
        }
        return status;
    }


    public static boolean createFolder(File folderDirectory) {
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

    public static boolean removeFolder(String folderPath) {

        File directory;

        try {
            directory = new File(folderPath);

            if (!directory.exists())
                return true;
            if (!directory.isDirectory())
                return false;

            String[] list = directory.list();

            // Some JVMs return null for File.list() when the
            // directory is empty.
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    File entry = new File(directory, list[i]);

                    if (entry.isDirectory()) {
                        if (!removeFolder(entry.getPath()))
                            return false;
                    } else {
                        if (!entry.delete())
                            return false;
                    }
                }
            }
            return directory.delete();
        } catch (Exception e) {
            Log.e("AAD", "Deletion failed");
            e.printStackTrace();
        }
        return false;
    }
}
