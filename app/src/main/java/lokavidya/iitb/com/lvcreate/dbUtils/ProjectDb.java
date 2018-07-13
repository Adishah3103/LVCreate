package lokavidya.iitb.com.lvcreate.dbUtils;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import lokavidya.iitb.com.lvcreate.model.Project;
import lokavidya.iitb.com.lvcreate.model.ProjectItem;

@Database(entities = {ProjectItem.class, Project.class}, version = 1, exportSchema = false)
public abstract class ProjectDb extends RoomDatabase {

    private static final String DATABASE_NAME = "project_db";
    private static final Object LOCK = new Object();
    private static ProjectDb sInstance;

    public static ProjectDb getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d("ProjectDb", "Creating new Database instance.");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        ProjectDb.class, ProjectDb.DATABASE_NAME)
                        // Run this on UI thread temporarily
                        .allowMainThreadQueries()
                        .build();
            }
        }
        Log.d("ProjectDb", "Getting new Database instance.");
        return sInstance;
    }

    public abstract ProjectItemDao projectItemDao();

    public abstract ProjectDao projectDao();

}
