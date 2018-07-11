package lokavidya.iitb.com.lvcreate.model;

import android.graphics.Bitmap;

public class ProjectItem {

    private String itemFilePath;
    private String itemTitle;
    private Bitmap itemThumb;
    private Boolean itemIsAudio;
    private String itemAudioPath;

    public ProjectItem(String itemFilePath, String itemTitle, Bitmap itemThumb, Boolean itemIsAudio, String itemAudioPath) {
        this.itemFilePath = itemFilePath;
        this.itemTitle = itemTitle;
        this.itemThumb = itemThumb;
        this.itemIsAudio = itemIsAudio;
        this.itemAudioPath = itemAudioPath;
    }

    public String getItemFilePath() {
        return itemFilePath;
    }

    public void setItemFilePath(String itemFilePath) {
        this.itemFilePath = itemFilePath;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public Bitmap getItemThumb() {
        return itemThumb;
    }

    public void setItemThumb(Bitmap itemThumb) {
        this.itemThumb = itemThumb;
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
