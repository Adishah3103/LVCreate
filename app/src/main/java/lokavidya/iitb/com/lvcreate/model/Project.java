package lokavidya.iitb.com.lvcreate.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "project_table")
public class Project {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;

    private String title;

    private String desc;

    @ColumnInfo(name = "channel_id")
    private int channedId;

    @ColumnInfo(name = "sub_channel_id")
    private int subChannedId;

    @ColumnInfo(name = "project_lang")
    private String projectLanguage;

    @Ignore
    public Project(long id, String title, String desc, int channedId, int subChannedId, String projectLanguage) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.channedId = channedId;
        this.subChannedId = subChannedId;
        this.projectLanguage = projectLanguage;
    }

    public Project(String title, String desc, int channedId, int subChannedId, String projectLanguage) {
        this.title = title;
        this.desc = desc;
        this.channedId = channedId;
        this.subChannedId = subChannedId;
        this.projectLanguage = projectLanguage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getChannedId() {
        return channedId;
    }

    public void setChannedId(int channedId) {
        this.channedId = channedId;
    }

    public int getSubChannedId() {
        return subChannedId;
    }

    public void setSubChannedId(int subChannedId) {
        this.subChannedId = subChannedId;
    }

    public String getProjectLanguage() {
        return projectLanguage;
    }

    public void setProjectLanguage(String projectLanguage) {
        this.projectLanguage = projectLanguage;
    }
}
