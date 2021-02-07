package fh.kufstein.uebungsblatt05_versuch6;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class TrackService extends Service {

    public final String CHANNEL = "gps-tracker-channel-1";
    /*
    Notification Channel, eine App kann beliebig viele Channels erzeugen
    User können die Wichtigkeit für jeden Channel konfigurieren
    Notifications der App müssen über den Channel publiziert werden
     */

    public final int ID = 12345;
    protected boolean tracking = false;

    protected FusedLocationProviderClient locationProviderClient;
    /*
    ermittelt letzte bekannte Position
    ermöglicht Konfiguartion der Genauigkeit und Settingsverhalten
    braucht dependency in build.gradle
     */

    protected LocationCallback locationCallback;
    protected Track currentTrack = null;
    protected TrackDao trackDao;

    //für den ersten Start kann die Methode onCreate implementiert werden
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        trackDao = AppDatabase.getInstance(this.getApplicationContext()).getTrackDao();
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this); //liefert letzte bekannte Position

        //zum Empfangen von Benachrichtigungen vom FusedLocationProvider wenn sich Standort ändert
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onLocationUpdates(locationResult.getLastLocation());
            }
        };
    }


    //wichtig für Bound Service, muss überschrieben werden
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //jeder startService() Aufruf triggert die onStartCommand()
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals("TOGGLE")) {
            if (tracking && currentTrack != null) {
                stopLocationUpdates(true);
                //starte Service --> benötigt App eindeutige ID und Notification
                startForeground(ID, createNotification(false));
            } else {
                startLocationUpdates();
                startForeground(ID, createNotification(true));
            }
        }
        else if (intent.getAction().equals("CONTINUE") && !tracking && currentTrack != null) {
            startLocationUpdates();
            startForeground(ID, createNotification(true));
        }
        else if (intent.getAction().equals("PAUSE") && tracking && currentTrack != null) {
            stopLocationUpdates(true);
            startForeground(ID, createNotification(false));
        }
        else if (intent.getAction().equals("STOP") && currentTrack != null) {
            stopLocationUpdates(false);
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            stopSelf(); //Service beendet sich selbst
        }

        return START_NOT_STICKY; //wenn der Service vom System gestoppt wurde, soll er gestoppt bleiben
    }


    private void startLocationUpdates() {
        //Location update definieren --> Konfiguration, die wir brauchen, die uns genau spezifiziert wie unsere Location gemessen werden soll
        LocationRequest lr = LocationRequest.create(); //neue LocationRequest anlegen
        lr.setInterval(5000);
        lr.setSmallestDisplacement(5);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //wenn die Permission gegeben wurde --> starte Tracking
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startTracking();
            //Location update starten
            locationProviderClient.requestLocationUpdates(lr, locationCallback, Looper.myLooper());
        }
        tracking = true;
    }

    private void stopLocationUpdates(boolean pause) {
        //Location update beenden
        locationProviderClient.removeLocationUpdates(locationCallback);
        tracking = false;

        if (!pause) {
            stopTracking();
        }
    }

    private void onLocationUpdates(android.location.Location lastLocation) {
        if (currentTrack != null) {
            Location l = Location.from(lastLocation, currentTrack); //liefert Location zurück, die zum currentTrack gehört
            trackDao.createLocation(l);
        }
    }

    private void startTracking() {
        if (currentTrack == null) {
            Track track = Track.now(); //Track mit Startzeit und Datum
            track.id = trackDao.createTrack(track); //createTrack liefert die TrackId zurück
            currentTrack = track;
        }
    }

    private void stopTracking() {
        if (currentTrack != null) {
            currentTrack.stop(); //befüllt die endzeit des Tracks
            trackDao.updateTrack(currentTrack);
            currentTrack = null;
        }
    }

    //erzeugen der Notification mithilfe von Builder
    //Builder wird genutzt, um viele unterschiedliche Konfig. von Objekten über einen einheitlichen Prozess herzustellen
    private Notification createNotification(boolean tracking) {
        //braucht einen Channel und Context
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL)
                .setSmallIcon(R.drawable.ic_navigation)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (tracking) {
            builder.addAction(
                    R.drawable.ic_pause,
                    "Pause",
                    getPendingIntent("PAUSE"));
        } else {
            builder.addAction(
                    R.drawable.ic_play,
                    "Continue",
                    getPendingIntent("CONTINUE"));
        }

        builder.addAction(
                R.drawable.ic_stop,
                "Stop",
                getPendingIntent("STOP"));

        return builder.build();
    }

    //Verweis auf ein vom System verwaltetes Token, das die ürsprünglichen Daten beschreibt, die zum Abrufen verwendet wurden
    private PendingIntent getPendingIntent(String action) {
        Intent intent = new Intent(this, TrackService.class);
        intent.setAction(action);

        return PendingIntent.getService(this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT); //wenn bereits vorhanden, beibehalten aber Daten ersetzten
    }



    //Notification Channels erst mit Android8 eingeführt --> kann also nur von => 8 genutzt werden
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(getString(R.string.app_name));
            //mit dem NotificationManager wird Notification und Channel erstellt erstellt
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
