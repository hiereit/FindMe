package ddwucom.mobile.finalproject;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class RepeatReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Hi all!", Toast.LENGTH_SHORT).show();

        intent  = new Intent(context.getApplicationContext(), SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 0);

        // notification 생성
        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(context.getApplicationContext(), context.getString(R.string.CHANNEL_ID))
                .setSmallIcon(R.drawable.ic_search_icon)
                .setContentTitle("분실물 앱 제목")
                .setContentText("등록한 분실물을 다시 검색해 보세요")
//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText("등록한 분실물을 다시 검색해 보세요")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        //화면에 알림을 표시
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());

        int notificationId = 100;
        notificationManager.notify(notificationId, builder.build());
    }
}
