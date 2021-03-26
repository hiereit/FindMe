package ddwucom.mobile.finalproject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class MyParserForItemInfo {
    public enum TagType { NONE, TITLE, ITEM_NAME,
                        LOST_DATE, LOST_HOR,
                        LOST_LOC, LOST_PLACE, LOST_SENAME,
                        UNIQ, ATC_ID, LOST_CAT,
                        ORG_NAME, STATUS, ORG_TEL, IMAGE}; //enumeration type 정의

    final static String TAG_ITEM = "item";              //item TAG

    final static String TAG_TITLE = "lstSbjt";          // 분실신고 시 게시글 제목
    final static String TAG_NAME = "lstPrdtNm";         //물품명
    final static String TAG_LOST_DATE = "lstYmd";       //분실일자 ex) 2017-11-01
    final static String TAG_LOST_HOR = "lstHor";        //습득 시간 (hh)

    final static String TAG_LOST_LOC = "lstLctNm";      //분실지역명 ex) 대전광역시
    final static String TAG_LOST_PLACE = "lstPlace";    //분실장소
    final static String TAG_LOST_SENAME = "lstSeNm";    //분실장소 구분명

    final static String TAG_UNIQ = "uniq";              //특이사항
    final static String TAG_ATC_ID= "atcId";            //관리ID
    final static String TAG_LOST_CAT ="prdtClNm";       //카테고리

    final static String TAG_ORG_NAME = "orgNm";         // 보관기관 이름
    final static String TAG_ITEM_STATUS = "lstSteNm";   //분실물 상태명
    final static String TAG_ORG_TEL = "tel";            // 보관기관 연락처

    final static String TAG_ITEM_IMAGE = "lstFilePathImg"; // 분실물 이미지명

    public MyParserForItemInfo() {
    }

    public ItemInfoDTO parse(String xml){ //dto 객체 반환

        ItemInfoDTO result = null;
        TagType tagType = TagType.NONE;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            result = new ItemInfoDTO(); //dto 객체 설정
                        } else if (parser.getName().equals(TAG_TITLE)) {
                            if (result != null) tagType = TagType.TITLE;
                        } else if (parser.getName().equals(TAG_NAME)) {
                            if (result != null) tagType = TagType.ITEM_NAME;
                        } else if (parser.getName().equals(TAG_LOST_DATE)) {
                            if (result != null) tagType = TagType.LOST_DATE;
                        } else if (parser.getName().equals(TAG_LOST_HOR)) {
                            if (result != null) tagType = TagType.LOST_HOR;
                        } else if (parser.getName().equals(TAG_LOST_LOC)) {
                            if (result != null) tagType = TagType.LOST_LOC;
                        } else if (parser.getName().equals(TAG_LOST_PLACE)) {
                            if (result != null) tagType = TagType.LOST_PLACE;
                        } else if (parser.getName().equals(TAG_LOST_SENAME)) {
                            if (result != null) tagType = TagType.LOST_SENAME;
                        } else if (parser.getName().equals(TAG_UNIQ)) {
                            if (result != null) tagType = TagType.UNIQ;
                        } else if (parser.getName().equals(TAG_ATC_ID)) {
                            if (result != null) tagType = TagType.ATC_ID;
                        } else if (parser.getName().equals(TAG_LOST_CAT)) {
                            if (result != null) tagType = TagType.LOST_CAT;
                        } else if (parser.getName().equals(TAG_ORG_NAME)) {
                            if (result != null) tagType = TagType.ORG_NAME;
                        } else if (parser.getName().equals(TAG_ITEM_STATUS)) {
                            if (result != null) tagType = TagType.STATUS;
                        } else if (parser.getName().equals(TAG_ORG_TEL)) {
                            if (result != null) tagType = TagType.ORG_TEL;
                        } else if (parser.getName().equals(TAG_ITEM_IMAGE)) {
                            if (result != null) tagType = TagType.IMAGE;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            //resultList.add(dto);
                            //dto = null;             //dto 비워주기 >> 새로운 dto 준비
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case TITLE:
                                result.setTitle(parser.getText());
                                break;
                            case ITEM_NAME:
                                result.setItemName(parser.getText());
                                break;
                            case LOST_DATE:
                                result.setLostDate(parser.getText());
                                break;
                            case LOST_HOR:
                                result.setLostHor(parser.getText());
                                break;
                            case LOST_LOC:
                                result.setLostLoc(parser.getText());
                                break;
                            case LOST_PLACE:
                                result.setLostPlace(parser.getText());
                                break;
                            case LOST_SENAME:
                                result.setLostSeName(parser.getText());
                                break;
                            case UNIQ:
                                result.setUniq(parser.getText());
                                break;
                            case ATC_ID:
                                result.setAtcId(parser.getText());
                                break;
                            case LOST_CAT:
                                result.setCategory(parser.getText());
                                break;
                            case ORG_NAME:
                                result.setOrgName(parser.getText());
                                break;
                            case STATUS:
                                result.setItemStatus(parser.getText());
                                break;
                            case ORG_TEL:
                                result.setOrgTel(parser.getText());
                                break;
                            case IMAGE:
                                result.setItemImageFileLink(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
