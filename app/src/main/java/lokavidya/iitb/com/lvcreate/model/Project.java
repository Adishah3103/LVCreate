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
    private int channelId;

    @ColumnInfo(name = "sub_channel_id")
    private int subChannelId;

    @ColumnInfo(name = "project_lang")
    private String projectLanguage;

    @ColumnInfo(name = "first_file_thumb")
    private String firstFileThumb;

    @ColumnInfo(name = "is_uploaded")
    private Boolean isUploaded;

    @Ignore
    public Project(long id, String title, String desc, int channelId, int subChannelId, String projectLanguage, String firstFileThumb, Boolean isUploaded) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.channelId = channelId;
        this.subChannelId = subChannelId;
        this.projectLanguage = projectLanguage;
        this.firstFileThumb = firstFileThumb;
        this.isUploaded = isUploaded;
    }

    public Project(String title, String desc, int channelId, int subChannelId, String projectLanguage, String firstFileThumb, Boolean isUploaded) {
        this.title = title;
        this.desc = desc;
        this.channelId = channelId;
        this.subChannelId = subChannelId;
        this.projectLanguage = projectLanguage;
        this.firstFileThumb = firstFileThumb;
        this.isUploaded = isUploaded;
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

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getSubChannelId() {
        return subChannelId;
    }

    public void setSubChannelId(int subChannelId) {
        this.subChannelId = subChannelId;
    }

    public String getProjectLanguage() {
        return projectLanguage;
    }

    public void setProjectLanguage(String projectLanguage) {
        this.projectLanguage = projectLanguage;
    }

    public String getFirstFileThumb() {
        return firstFileThumb;
    }

    public void setFirstFileThumb(String firstFileThumb) {
        this.firstFileThumb = firstFileThumb;
    }

    public Boolean getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(Boolean uploaded) {
        isUploaded = uploaded;
    }
}
