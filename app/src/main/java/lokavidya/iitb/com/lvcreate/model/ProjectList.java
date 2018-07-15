package lokavidya.iitb.com.lvcreate.model;

/**
 * Created by Aditya on 16-07-2018.
 */

public class ProjectList {

    private String name;
    private String itemImgUrl;
    private String duration;

    public ProjectList(String name, String itemImgUrl, String duration) {
        this.name = name;
        this.itemImgUrl = itemImgUrl;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemImgUrl() {
        return itemImgUrl;
    }

    public void setItemImgUrl(String itemImgUrl) {
        this.itemImgUrl = itemImgUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
