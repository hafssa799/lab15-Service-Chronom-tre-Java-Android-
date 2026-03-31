package sg.vantagepoint.servicechronometrejava;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChronometreService extends Service {

    private final IBinder binder = new LocalBinder();
    private int secondes = 0;
    private boolean isRunning = false;
    private ScheduledExecutorService executor;
    private static final int NOTIFICATION_ID = 1001;
    private NotificationManager notificationManager;

    public class LocalBinder extends Binder {
        public ChronometreService getService() {
            return ChronometreService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        creerNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Gère l'arrêt via l'action "STOP"
        if (intent != null && "STOP".equals(intent.getAction())) {
            isRunning = false;
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }

        // Démarre le chrono s'il n'est pas déjà lancé
        if (!isRunning) {
            isRunning = true;
            startForeground(NOTIFICATION_ID, creerNotification());
            demarrerChronometre();
        }
        return START_STICKY;
    }

    private void demarrerChronometre() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (isRunning) {
                secondes++;
                updateNotification();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void creerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "chrono_channel", "Chrono Channel", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification creerNotification() {
        return new NotificationCompat.Builder(this, "chrono_channel")
                .setContentTitle("Chronomètre actif")
                .setContentText("Temps écoulé : " + getTempsFormate())
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void updateNotification() {
        notificationManager.notify(NOTIFICATION_ID, creerNotification());
    }

    // Méthode publique utilisée par l'Activity pour l'affichage
    public String getTempsFormate() {
        int m = secondes / 60;
        int s = secondes % 60;
        return String.format("%02d:%02d", m, s);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        if (executor != null) executor.shutdownNow();
        super.onDestroy();
    }
}