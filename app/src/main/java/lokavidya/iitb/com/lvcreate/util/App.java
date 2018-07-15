package lokavidya.iitb.com.lvcreate.util;

import android.app.Application;
import android.util.Log;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
                Log.i("AudioModule", "Loaded");
            }

            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
                Log.i("AudioModule", "FFMPEG Not supported");
            }
        });

    }

}
