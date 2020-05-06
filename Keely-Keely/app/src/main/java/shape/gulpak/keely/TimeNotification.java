package shape.gulpak.keely;
        import android.app.NotificationChannel;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.ContentResolver;
        import android.content.Context;
        import android.content.Intent;
        import android.media.Ringtone;
        import android.media.RingtoneManager;
        import android.net.Uri;
        import androidx.core.app.NotificationCompat;

public class TimeNotification extends BroadcastReceiver {
    public static final String NOTIFICATION_CHANNEL_ID = "1110" ;
    private final static String default_notification_channel_id = "default" ;
    boolean sound;
    @Override
    public void onReceive(Context context, Intent intent) {
        sound=intent.getBooleanExtra("sound",false);
        Intent contentIntent = new Intent(context, MainActivity.class);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, contentIntent, 0);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), default_notification_channel_id ) ;
        mBuilder.setContentTitle("Нагадування")
                .setContentText("Саме час перевірити свої знання")
               .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.gatinha_icon_w_small) ;
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSound(null);
        mBuilder.setAutoCancel( true ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;
        if(sound)playNotificationSound( context);

    }
    public void playNotificationSound(Context context)
    {
        try
        {

            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +context.getPackageName() + "/raw/maycom");
            Ringtone r = RingtoneManager.getRingtone(context, alarmSound);
            r.play();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}