package lokavidya.iitb.com.lvcreate.fileManagement;

import android.annotation.SuppressLint;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class FileDataRetrieval {


    public static long getFileSizeInKB(String filePath) {

        long fileSizeInBytes = 0;

        try {
            File file = new File(filePath);
            /**
             * normally size is directly shown with 1000 as base and so on
             * say for 2494929 bytes explorer shows 2.49MB or 2.5MB but when
             * counted using 1024 as base it is 2.38MB which we will be using
             */
            if (file.exists()) {
                fileSizeInBytes = file.length();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return (fileSizeInBytes / 1024);
    }


    public static String getFormattedFileSize(long sizeInKB) {

        String displaySize = "";
        double m = sizeInKB / 1024.0;
        double g = sizeInKB / 1048576.0;
        double t = sizeInKB / 1073741824.0;

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            displaySize = dec.format(t).concat("TB");
        } else if (g > 1) {
            displaySize = dec.format(g).concat("GB");
        } else if (m > 1) {
            displaySize = dec.format(m).concat("MB");
        } else {
            displaySize = dec.format(sizeInKB).concat("KB");
        }

        return displaySize;
    }


    public static long getFileDurationInSeconds(String filePath) {

        long timeInMilliseconds = 0;

        try {

            File file = new File(filePath);

            //https://stackoverflow.com/questions/3936396/how-to-get-duration-of-a-video-file
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

            //use one of overloaded setDataSource() functions to set your data source
            retriever.setDataSource(file.getPath());
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            timeInMilliseconds = Long.parseLong(time);

            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (timeInMilliseconds / 1000);

    }


    @SuppressLint("DefaultLocale")
    public static String getFormattedFileDuration(long timeInSeconds) {
        return String.format("%02d:%02d",
                TimeUnit.SECONDS.toMinutes(timeInSeconds),
                timeInSeconds - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(timeInSeconds))
        );
    }
}
