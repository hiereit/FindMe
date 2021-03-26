package ddwucom.mobile.finalproject;

import java.io.Serializable;

public class ItemSearchInfoDTO implements Serializable {
    private int _id;
    private String title;
    private String itemName;
    private String atcId;
    private String lostPlace;
    private String lostDate;
    private String category;

    public int get_id() {        return _id;    }
    public void set_id(int _id) {        this._id = _id;    }

    public String getTitle() {        return title;    }
    public void setTitle(String title) {        this.title = title;    }


    public String getItemName() {        return itemName;    }
    public void setItemName(String itemName) {        this.itemName = itemName;    }

    public String getAtcId() {        return atcId;    }
    public void setAtcId(String atcId) {        this.atcId = atcId;    }

    public String getLostPlace() {        return lostPlace;    }
    public void setLostPlace(String lostPlace) {        this.lostPlace = lostPlace;    }

    public String getLostDate() {        return lostDate;    }
    public void setLostDate(String lostDate) {        this.lostDate = lostDate;    }

    public String getCategory() {        return category;    }
    public void setCategory(String category) {        this.category = category;    }
}
