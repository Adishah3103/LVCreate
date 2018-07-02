package lokavidya.iitb.com.lvcreate.model;

import android.graphics.Bitmap;

public class ProjectItem {

    private String itemFilePath;
    private String itemTitle;
    private Bitmap itemThumb;

    public ProjectItem(String itemFilePath, String itemTitle, Bitmap itemThumb) {
        this.itemFilePath = itemFilePath;
        this.itemTitle = itemTitle;
        this.itemThumb = itemThumb;
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
}
