package lokavidya.iitb.com.lvcreate.model;

public class HomeItem {

    private String itemTitle;
    private String itemDesc;
    private String itemImgUrl;

    public HomeItem(String itemTitle, String itemDesc, String itemImgUrl) {
        this.itemTitle = itemTitle;
        this.itemDesc = itemDesc;
        this.itemImgUrl = itemImgUrl;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getItemImgUrl() {
        return itemImgUrl;
    }

    public void setItemImgUrl(String itemImgUrl) {
        this.itemImgUrl = itemImgUrl;
    }
}
