package sg.vantagepoint.servicechronometrejava;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private TextView tvTemps;
    private ChronometreService mService;
    private boolean isBound = false;
    private final Handler handler = new Handler();

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ChronometreService.LocalBinder binder = (ChronometreService.LocalBinder) service;
            mService = binder.getService();
            isBound = true;
            actualiserUI(); // Lance la boucle de mise à jour du texte
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTemps = findViewById(R.id.tvTemps);
        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);

        // Demander la permission de notification (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChronometreService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        });

        btnStop.setOnClickListener(v -> {
            if (isBound) {
                unbindService(connection);
                isBound = false;
            }
            Intent intent = new Intent(this, ChronometreService.class);
            intent.setAction("STOP");
            startService(intent); // Envoie l'action STOP au service
            tvTemps.setText("00:00");
        });
    }

    private void actualiserUI() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isBound && mService != null) {
                    tvTemps.setText(mService.getTempsFormate());
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}