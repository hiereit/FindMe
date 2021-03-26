package ddwucom.mobile.finalproject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class MyParserForSearch {
    public enum TagType { NONE, TITLE, ITEM_NAME, ATC_ID, LOST_PLACE, LOST_DATE, ITEM_CAT}; //enumeration type 정의

    final static String TAG_ITEM = "item";

    final static String TAG_TITLE = "lstSbjt";
    final static String TAG_NAME = "lstPrdtNm";
    final static String TAG_ATCID = "atcId";
    final static String TAG_LOST_PLACE = "lstPlace";
    final static String TAG_LOST_DATE = "lstYmd";
    final static String TAG_CAT = "prdtClNm";

    public MyParserForSearch() {
    }

    public ArrayList<ItemSearchInfoDTO> parse(String xml){
        ArrayList<ItemSearchInfoDTO> resultList = new ArrayList();
        ItemSearchInfoDTO dto = null;
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
                            dto = new ItemSearchInfoDTO(); //dto 객체 설정
                        }  else if (parser.getName().equals(TAG_TITLE)) {
                            if (dto != null) tagType = TagType.TITLE;
                        } else if (parser.getName().equals(TAG_NAME)) {
                            if (dto != null) tagType = TagType.ITEM_NAME;
                        } else if (parser.getName().equals(TAG_ATCID)) {
                            if (dto != null) tagType = TagType.ATC_ID;
                        }else if (parser.getName().equals(TAG_LOST_PLACE)) {
                            if (dto != null) tagType = TagType.LOST_PLACE;
                        } else if (parser.getName().equals(TAG_LOST_DATE)) {
                            if (dto != null) tagType = TagType.LOST_DATE;
                        } else if (parser.getName().equals(TAG_CAT)) {
                            if (dto != null) tagType = TagType.ITEM_CAT;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            resultList.add(dto);
                            dto = null;             //dto 비워주기 >> 새로운 dto 준비
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case TITLE:
                                dto.setTitle(parser.getText());
                                break;
                            case ITEM_NAME:
                                dto.setItemName(parser.getText());
                                break;
                            case ATC_ID:
                                dto.setAtcId(parser.getText());
                                break;
                            case LOST_PLACE:
                                dto.setLostPlace(parser.getText());
                                break;
                            case LOST_DATE:
                                dto.setLostDate(parser.getText());
                                break;
                            case ITEM_CAT:
                                dto.setCategory(parser.getText());
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
        return resultList;
    }
}


