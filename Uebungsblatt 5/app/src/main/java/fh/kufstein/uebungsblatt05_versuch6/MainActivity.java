package fh.kufstein.uebungsblatt05_versuch6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TrackDao dao = AppDatabase.getInstance(getApplicationContext()).getTrackDao();
        TrackAdapter adapter = new TrackAdapter(dao); //übergeben des Daos an den Adapter

        //auf den Adapter die eingenen Interfaces setzen --> gibt das Event weiter
        adapter.setEditListener((Track track) -> {
            Intent editIntent = new Intent(this, TrackEditActivity.class);
            editIntent.putExtra("track_id", track.id);
            startActivity(editIntent);
        });

        adapter.setMapListener((Track track) -> {
            Intent mapIntent = new Intent(this, MapActivity.class);
            mapIntent.putExtra("track_id", track.id);
            startActivity(mapIntent);
        });

        RecyclerView recyclerView = findViewById(R.id.track_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //Konfig. der RV durch LayoutManager, Zuständig für Anordnung der Elemente. Ein Container fragt bei einer Neudarstellung immer den LM, wie er seine Elemente anordnen soll
        recyclerView.setAdapter(adapter);
    }

    //wird mit Klick auf FAB aufgrufen --> permission muss im Manifest eingetragen werden
    public void startTracking(View view) {
        //wenn die Permission nicht gegranted ist
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //frag die Permission nach --> request Code in dem Fall egal
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }

        //wenn wir die Permission hätten --> Settingsabfrage machen
        else {

            //Konfiguration, die wir brauchen, die uns genau spezifiziert wie unsere Location gemessen werden soll
            LocationRequest request = LocationRequest.create();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //Genauigkeit --> wie genau soll die Abfrage sein?
            request.setInterval(5000); //alle 5 Sekunden soll die Location wieder neu abgefragt werden
            request.setSmallestDisplacement(5); //Location wird nur geupdatet, wenn sie sich um 5 Meter ändert

            //mit dem können wir einen SettingsRequest beim Client abfragen
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(request); //builder muss wissen, welche Art von Locations wir abfragen wollen

            //über Location Settings kann sichergestellt werden, dass GPS aktiviert ist --> überprüft das
            SettingsClient settingsClient = LocationServices.getSettingsClient(this);
            settingsClient.checkLocationSettings(builder.build()) //überprüfe die Location Settings --> liefert uns ein Objekt zurück, wo wir Listener hinterlegen können
                    //wenn die Permission erteilt ist und alles okay ist, dann starte das Tracking
                    .addOnSuccessListener(this, (LocationSettingsResponse response) -> {
                        Intent intent = new Intent(this, TrackService.class);
                        intent.setAction("TOGGLE");
                        startService(intent);
                    })

                    //wenn das Tracking aus irgendeinen Grund (z.B. kein GPS vorhanden) nicht möglich ist --> Exception werfen
                    .addOnFailureListener(this, (Exception e) -> {
                        //wenn die Exception lösbar ist
                        if (e instanceof ResolvableApiException) {
                            ResolvableApiException resolvable = (ResolvableApiException) e;

                            //dann versuche sie zu lösen, Request Code beliebig
                            try {
                                resolvable.startResolutionForResult(this, 456);
                            } catch (IntentSender.SendIntentException sendIntentException) {
                                sendIntentException.printStackTrace();
                            }
                        }
                    });
        }
    }
}