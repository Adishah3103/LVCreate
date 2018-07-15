package lokavidya.iitb.com.lvcreate.fileManagement;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ManageFile {

    public void copyFile(String inputFileFullPath, String outputFileFullPath) {

        InputStream in = null;
        OutputStream out = null;

        String outputDirectory = outputFileFullPath.substring(0, outputFileFullPath.lastIndexOf("/")) + "/";

        Log.d("AAD", "output directory : " + outputDirectory);
        long total = 1, done = 0;

        try {

            Log.d("AAD", "copying : " + inputFileFullPath.substring(inputFileFullPath.lastIndexOf("/") + 1));
            //create output directory if it doesn't exist
            File dir = new File(outputDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File originalFile = new File(inputFileFullPath);
            total = originalFile.length();

            in = new FileInputStream(inputFileFullPath);
            out = new FileOutputStream(outputFileFullPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                done += 1024;
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("File Copy : Not Found ", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("File Copy Crash ", e.getMessage());
        }

    }


    public void moveFile(String inputFileFullPath, String outputFileFullPath) {

        copyFile(inputFileFullPath, outputFileFullPath);
        deleteFile(inputFileFullPath);
    }


    public void deleteFile(String fileFullPath) {

        try {
            // delete the original file
            new File(fileFullPath).delete();
            Log.d("File Delete : ", "Deleted old file successfully");

        } catch (Exception e) {
            Log.e("File Delete Crash ", e.getMessage());
        }
    }

}
