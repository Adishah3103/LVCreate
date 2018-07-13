package lokavidya.iitb.com.lvcreate.dbUtils;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import lokavidya.iitb.com.lvcreate.model.ProjectItem;

@Database(entities = {ProjectItem.class}, version = 1, exportSchema = false)
public abstract class ProjectItemDb extends RoomDatabase {

    private static final String DATABASE_NAME = "project_item_db";
    private static final Object LOCK = new Object();
    private static ProjectItemDb sInstance;

    public static ProjectItemDb getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d("ProjectItemDb", "Creating new Database instance.");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        ProjectItemDb.class, ProjectItemDb.DATABASE_NAME)
                        // Run this on UI thread temporarily
                        .allowMainThreadQueries()
                        .build();
            }
        }
        Log.d("ProjectItemDb", "Getting new Database instance.");
        return sInstance;
    }

    public abstract ProjectItemDao projectItemDao();
}
