package lokavidya.iitb.com.lvcreate.dbUtils;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import lokavidya.iitb.com.lvcreate.model.Project;

@Dao
public interface ProjectDao {

    @Query("SELECT * FROM project_table ORDER BY _id ASC")
    List<Project> loadAllProject();

    /**
     * insertItem returns the ProjectId (long) which is actually stored in the database.
     * Use that for further queries
     */
    @Insert
    long insertItem(Project projectEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItem(Project projectEntry);

    @Delete
    void deleteItem(Project projectEntry);

    @Query("SELECT * FROM project_table WHERE _id = :id")
    Project loadItemById(long id);

    @Query("DELETE FROM project_table WHERE _id = :id")
    void deleteItemById(long id);

}
