package ddwucom.mobile.finalproject;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ItemInfoDTO implements Serializable {
    private int _id;
    private String title;       //제목
    private String itemName;    //물품명
    private String lostDate;    //습득일자 (YYYY-mm-dd)
    private String lostHor;     //습득 시간 (hh)

    private String lostLoc;     //분실지역명
    private String lostPlace;   //분실장소
    private String lostSeName;  //분실장소 구분명

    private String uniq;        //특이사항
    private String atcId;       //관리ID
    private String category;    //카테고리

    private String orgName;     //보관장소이름
    private String itemStatus;  //보관상태
    private String orgTel;      //보관장소 연락처

    private String itemImageFileLink;  //이미지 링크
    private Bitmap imgBitmap;

    public int get_id() {        return _id;    }
    public void set_id(int _id) {        this._id = _id;    }

    public String getTitle() {        return title;    }
    public void setTitle(String title) {        this.title = title;    }

    public String getItemName() {        return itemName;    }
    public void setItemName(String itemName) {        this.itemName = itemName;    }

    public String getLostDate() {        return lostDate;    }
    public void setLostDate(String lostDate) {        this.lostDate = lostDate;    }

    public String getLostHor() {        return lostHor;    }
    public void setLostHor(String lostHor) {        this.lostHor = lostHor;    }

    public String getLostLoc() {        return lostLoc;    }
    public void setLostLoc(String lostLoc) {        this.lostLoc = lostLoc;    }

    public String getLostPlace() {        return lostPlace;    }
    public void setLostPlace(String lostPlace) {        this.lostPlace = lostPlace;    }

    public String getLostSeName() {        return lostSeName;    }
    public void setLostSeName(String lostSeName) {       this.lostSeName = lostSeName;    }

    public String getUniq() {        return uniq;    }
    public void setUniq(String uniq) {        this.uniq = uniq;    }

    public String getAtcId() {        return atcId;    }
    public void setAtcId(String atcId) {        this.atcId = atcId;    }

    public String getCategory() {        return category;    }
    public void setCategory(String category) {        this.category = category;    }

    public String getOrgName() {        return orgName;    }
    public void setOrgName(String orgName) {        this.orgName = orgName;    }

    public String getItemStatus() {        return itemStatus;    }
    public void setItemStatus(String itemStatus) {        this.itemStatus = itemStatus;    }

    public String getOrgTel() {        return orgTel;    }
    public void setOrgTel(String orgTel) {        this.orgTel = orgTel;    }

    public String getItemImageFileLink() {        return itemImageFileLink;    }
    public void setItemImageFileLink(String itemImageFileLink) {        this.itemImageFileLink = itemImageFileLink;    }

    public Bitmap getImgBitmap() {        return imgBitmap;    }
    public void setImgBitmap(Bitmap imgBitmap) {        this.imgBitmap = imgBitmap;    }
}
