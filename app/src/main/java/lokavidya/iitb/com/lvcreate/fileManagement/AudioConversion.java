package lokavidya.iitb.com.lvcreate.fileManagement;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

public class AudioConversion {

    // String for destination to be returned
    String destinationFile;

    public String convert(Context context, String sourcePath) {

        // Source file
        File sourceFile = new File(Environment.getExternalStorageDirectory(), sourcePath);

        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                // So fast? Love it!
                Log.i("Convert", convertedFile.getAbsolutePath());
                destinationFile = convertedFile.getAbsolutePath();
            }

            @Override
            public void onFailure(Exception error) {
                // Oops! Something went wrong
                error.printStackTrace();
            }
        };
        AndroidAudioConverter.with(context)
                // Your current audio file
                .setFile(sourceFile)

                // Your desired audio format
                .setFormat(AudioFormat.WAV)

                // An callback to know when conversion is finished
                .setCallback(callback)

                // Start conversion
                .convert();

        return destinationFile;
    }

}
