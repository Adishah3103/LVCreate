package lokavidya.iitb.com.lvcreate.fileManagement;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


//https://stackoverflow.com/questions/6683600/zip-compress-a-folder-full-of-files-on-android
public class ManageZip {

    public boolean zipFileAtPath(String sourceFolderPath, String locationFilePath) {
        final int BUFFER = 4096;

        File sourceFile = new File(sourceFolderPath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(locationFilePath);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {

                Log.d("AAD", "is directory : " + sourceFile.getPath());
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());

            } else {

                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourceFolderPath);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(getLastPathComponent(sourceFolderPath));

                Log.d("AAD", "writing file : " + entry.getName());

                entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            Log.e("AAD", "Zip fail");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
     *
     * Zips a subfolder
     *
     */

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;


        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {

            if (file.isDirectory()) {

                Log.d("AAD", "is directory : " + file.getPath());
                zipSubFolder(out, file, basePathLength);

            } else {


                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();

                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);

                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(relativePath);

                entry.setTime(file.lastModified()); // to keep modification time after unzipping

                Log.d("AAD", "entry in : " + entry.getName());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }

        /*
         * http://bethecoder.com/applications/tutorials/showTutorials.action?tutorialId=Java_ZipUtilities_ZipEmptyDirectory
         * https://stackoverflow.com/a/6120954
         * OR
         * https://stackoverflow.com/questions/740375/directories-in-a-zip-file-when-using-java-util-zip-zipoutputstream
         * */
        if (fileList.length == 0) {


            String unmodifiedFilePath = folder.getPath();

            String relativePath = unmodifiedFilePath
                    .substring(basePathLength) + "/";


            ZipEntry entry = new ZipEntry(relativePath);

            entry.setTime(folder.lastModified()); // to keep modification time after unzipping

            Log.d("AAD", "entry in : " + entry.getName());
            out.putNextEntry(entry);
        }
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }

}