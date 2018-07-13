package lokavidya.iitb.com.lvcreate.dbUtils;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import lokavidya.iitb.com.lvcreate.model.ProjectItem;

@Dao
public interface ProjectItemDao {

    @Query("SELECT * FROM project_item_table ORDER BY _id ASC")
    List<ProjectItem> loadAllProjectItems();

    @Insert
    void insertItem(ProjectItem projectItemEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItem(ProjectItem projectItemEntry);

    @Delete
    void deleteItem(ProjectItem projectItemEntry);

    @Query("SELECT * FROM project_item_table WHERE _id = :id")
    ProjectItem loadItemById(int id);

    @Query("DELETE FROM project_item_table WHERE _id = :id")
    abstract void deleteItemById(int id);

    @Query("DELETE FROM project_item_table WHERE item_project_id = :projectId")
    abstract void deleteItemsByProjectId(int projectId);

}
