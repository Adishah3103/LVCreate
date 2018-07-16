package lokavidya.iitb.com.lvcreate.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.StorageObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class CloudStorage {

    private static final String TAG = "CloudStorage";
    static Storage storage = null;

    public static String uploadFile(Context context, String bucketName, String name, Uri uri)
            throws Exception {

        Storage storage = getStorage(context);
        StorageObject object = new StorageObject();
        object.setBucket(bucketName);
        File sdcard = Environment.getExternalStorageDirectory();
        //File file = new File(sdcard,filePath);
        File file = new File(uri.getPath());

        InputStream stream = new FileInputStream(file);

        StorageObject obj;

        try {
            String contentType = URLConnection.guessContentTypeFromStream(stream);
            InputStreamContent content = new InputStreamContent(contentType, stream);

            Storage.Objects.Insert insert = storage.objects().insert(bucketName, null, content);
            insert.setName(name);
            obj = insert.execute();

            Log.d(TAG, obj.getSelfLink());

            return obj.getSelfLink();
        } finally {
            stream.close();
        }
    }

    private static Storage getStorage(Context context) throws Exception {

        if (storage == null) {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            List<String> scopes = new ArrayList<String>();
            scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);

            Credential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountId("lvcms-testing@impactful-study-190010.iam.gserviceaccount.com") //Email
                    .setServiceAccountPrivateKeyFromP12File(getTempPkc12File(context))
                    .setServiceAccountScopes(scopes).build();

            storage = new Storage.Builder(httpTransport, jsonFactory,
                    credential).setApplicationName("MyApp")
                    .build();
        }

        return storage;
    }

    private static File getTempPkc12File(Context context) throws IOException {
        // xxx.p12 export from google API console
        InputStream pkc12Stream = context.getAssets().open("impactful.p12");
        File tempPkc12File = File.createTempFile("temp_pkc12_file", "p12");
        OutputStream tempFileStream = new FileOutputStream(tempPkc12File);
        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = pkc12Stream.read(bytes)) != -1) {
            tempFileStream.write(bytes, 0, read);
        }
        return tempPkc12File;
    }


}
