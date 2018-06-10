package android.sunshine.Utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.sunshine.MainActivity;
import android.sunshine.R;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

public class NotificationUtils {
    public final static String WEATHER_NOTIFICATION_CHANNEL_ID = "notification-channel";
    public final static int WEATHER_NOTIFICATION_ID = 0;
    public final static int WEATHER_PENDING_INTENT_ID = 10;
    public static void notify_user_when_data_changed(Context context){
        NotificationManager notification_manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    WEATHER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notification_manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notfication_builder = new
                NotificationCompat.Builder(context, WEATHER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_sunshine_icon)
                .setContentTitle(context.getString(R.string.weather_notification_title))
                .setContentText(context.getString(R.string.weather_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.weather_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            notfication_builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notification_manager.notify(WEATHER_NOTIFICATION_ID, notfication_builder.build());
    }

    public static PendingIntent contentIntent(Context context){
        Intent main_activity_intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context,
                WEATHER_PENDING_INTENT_ID,
                main_activity_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
