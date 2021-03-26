package ddwucom.mobile.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class NetworkManager {
    public static final String TAG = "sera";

    private Context context;

    public NetworkManager(Context context){        this.context = context;    }

    /* 주소(address)에 접속하여 문자열 데이터를 수신한 후 반환 */
    public String downloadContents(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        String result = null;
        String key;

        if (!isOnline()) return null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            stream = getNetworkConnection(conn);
            result = readStreamToString(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }

        return result;
    }


    /* 주소를 전달받아 bitmap 다운로드 후 반환 */
    public Bitmap downloadImage(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        Bitmap result = null;
        Log.d(TAG, "NEt) downLoadImage address: " + address);
        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            stream = getNetworkConnection(conn);
            result = readStreamToBitmap(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }
        Log.d(TAG, "NEt) downLoadImage Result: " + result);
        return result;
    }
    /* InputStream을 전달받아 비트맵으로 변환 후 반환 */
    private Bitmap readStreamToBitmap(InputStream stream) {
        return BitmapFactory.decodeStream(stream);
    }


    /* InputStream을 전달받아 문자열로 변환 후 반환 */
    private String readStreamToString(InputStream stream){
        StringBuilder result = new StringBuilder();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine = bufferedReader.readLine();

            while (readLine != null) {
                result.append(readLine + "\n");
                readLine = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }


    /* 네트워크 환경 조사 */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    /* URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환
     * 네이버용을 수정 - ClientID, ClientSeceret 추가 strings.xml 에서 읽어옴*/
    private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {

        // 클라이언트 아이디 및 시크릿 그리고 요청 URL 선언
//        conn.setReadTimeout(3000);
//        conn.setConnectTimeout(3000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("content-type", "application/json");
        conn.setDoInput(true);
        if (conn.getResponseCode() < 200 && conn.getResponseCode() > 300) {
            throw new IOException("HTTP error code: " + conn.getResponseCode());
        }
        return conn.getInputStream();
    }
}