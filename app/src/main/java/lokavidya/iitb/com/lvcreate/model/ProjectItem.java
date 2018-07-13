package lokavidya.iitb.com.lvcreate.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "project_item_table")
public class ProjectItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;

    private String itemFilePath;
    private long itemFileSize;
    private long itemFileDuration;
    private Boolean itemIsAudio;
    private String itemAudioPath;

    @Ignore
    public ProjectItem(int id, String itemFilePath, long itemFileSize, long itemFileDuration, Boolean itemIsAudio, String itemAudioPath) {
        this.id = id;
        this.itemFilePath = itemFilePath;
        this.itemFileSize = itemFileSize;
        this.itemFileDuration = itemFileDuration;
        this.itemIsAudio = itemIsAudio;
        this.itemAudioPath = itemAudioPath;
    }

    public ProjectItem(String itemFilePath, long itemFileSize, long itemFileDuration, Boolean itemIsAudio, String itemAudioPath) {
        this.itemFilePath = itemFilePath;
        this.itemFileSize = itemFileSize;
        this.itemFileDuration = itemFileDuration;
        this.itemIsAudio = itemIsAudio;
        this.itemAudioPath = itemAudioPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemFilePath() {
        return itemFilePath;
    }

    public void setItemFilePath(String itemFilePath) {
        this.itemFilePath = itemFilePath;
    }

    public long getItemFileSize() {
        return itemFileSize;
    }

    public void setItemFileSize(long itemFileSize) {
        this.itemFileSize = itemFileSize;
    }

    public long getItemFileDuration() {
        return itemFileDuration;
    }

    public void setItemFileDuration(long itemFileDuration) {
        this.itemFileDuration = itemFileDuration;
    }

    public Boolean getItemIsAudio() {
        return itemIsAudio;
    }

    public void setItemIsAudio(Boolean itemIsAudio) {
        this.itemIsAudio = itemIsAudio;
    }

    public String getItemAudioPath() {
        return itemAudioPath;
    }

    public void setItemAudioPath(String itemAudioPath) {
        this.itemAudioPath = itemAudioPath;
    }
}
