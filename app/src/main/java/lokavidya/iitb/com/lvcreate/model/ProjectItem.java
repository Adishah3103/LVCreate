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

    @ColumnInfo(name = "item_project_id")
    private long itemProjectId;

    @ColumnInfo(name = "item_file_path")
    private String itemFilePath;

    @ColumnInfo(name = "item_file_size")
    private long itemFileSize;

    @ColumnInfo(name = "item_is_audio")
    private Boolean itemIsAudio;

    @ColumnInfo(name = "item_audio_path")
    private String itemAudioPath;

    @ColumnInfo(name = "item_audio_file_size")
    private long itemAudioFileSize;

    @ColumnInfo(name = "item_file_duration")
    private long itemFileDuration;

    private int order;

    @ColumnInfo(name = "is_original")
    private boolean isOriginal;

    @Ignore
    public ProjectItem(int id, long itemProjectId, String itemFilePath, long itemFileSize, Boolean itemIsAudio, String itemAudioPath, long itemAudioFileSize, long itemFileDuration, int order, boolean isOriginal) {
        this.id = id;
        this.itemProjectId = itemProjectId;
        this.itemFilePath = itemFilePath;
        this.itemFileSize = itemFileSize;
        this.itemIsAudio = itemIsAudio;
        this.itemAudioPath = itemAudioPath;
        this.itemAudioFileSize = itemAudioFileSize;
        this.itemFileDuration = itemFileDuration;
        this.order = order;
        this.isOriginal = isOriginal;
    }

    public ProjectItem(long itemProjectId, String itemFilePath, long itemFileSize, Boolean itemIsAudio, String itemAudioPath, long itemAudioFileSize, long itemFileDuration, int order, boolean isOriginal) {
        this.itemProjectId = itemProjectId;
        this.itemFilePath = itemFilePath;
        this.itemFileSize = itemFileSize;
        this.itemIsAudio = itemIsAudio;
        this.itemAudioPath = itemAudioPath;
        this.itemAudioFileSize = itemAudioFileSize;
        this.itemFileDuration = itemFileDuration;
        this.order = order;
        this.isOriginal = isOriginal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getItemProjectId() {
        return itemProjectId;
    }

    public void setItemProjectId(long itemProjectId) {
        this.itemProjectId = itemProjectId;
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

    public long getItemAudioFileSize() {
        return itemAudioFileSize;
    }

    public void setItemAudioFileSize(long itemAudioFileSize) {
        this.itemAudioFileSize = itemAudioFileSize;
    }

    public long getItemFileDuration() {
        return itemFileDuration;
    }

    public void setItemFileDuration(long itemFileDuration) {
        this.itemFileDuration = itemFileDuration;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }
}
