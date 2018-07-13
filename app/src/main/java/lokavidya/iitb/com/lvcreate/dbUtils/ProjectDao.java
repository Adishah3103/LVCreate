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

    @Insert
    void insertItem(Project projectEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItem(Project projectEntry);

    @Delete
    void deleteItem(Project projectEntry);

    @Query("SELECT * FROM project_table WHERE _id = :id")
    Project loadItemById(int id);

}
