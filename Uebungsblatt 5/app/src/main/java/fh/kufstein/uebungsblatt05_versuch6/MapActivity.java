package fh.kufstein.uebungsblatt05_versuch6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import java.util.ArrayList;
import java.util.List;

//osmdroid ist eine freie MapView --> braucht dependency

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Context ctx = getApplicationContext(); //aus PPt von VO
        //Configuration ist eine Singelton Klasse, die die Konfig. für osmdroid übernimmt
        //load --> Lädt die Konfig. Wenn Einstellungen noch nicht festgelegt sind, werden sie mit Standardeinstellungen gefüllt
        //Wird verwendet, um Präferenzhierarchien aus Activitys oder XML zu erstellen
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx)); //aus PPt von VO

        MapView map = findViewById(R.id.map); //aus PPt von VO
        map.setTileSource(TileSourceFactory.MAPNIK); //aus PPt von VO, mapnik beschreibt die Kartenart
        map.setMultiTouchControls(true); //aus PPt von VO

        List<GeoPoint> geoPoints = new ArrayList<>();
        TrackDao dao = AppDatabase.getInstance(ctx).getTrackDao();

        Intent intent = getIntent();
        if (intent.hasExtra("track_id")) {
            long trackId = intent.getLongExtra("track_id", 0);
            List<Location> locations = dao.getLocations(trackId);

            //Konfiguration der MapView
            for (int j = 0; j < locations.size(); j++) {
                Location l = locations.get(j);
                GeoPoint g = new GeoPoint(l.latitude, l.longitude, l.altidude);
                geoPoints.add(g);

                if(j == 0){
                    IMapController mapController = map.getController(); //aus PPt von VO
                    mapController.setZoom(17f); //aus PPt von VO
                    GeoPoint startingPoint = new GeoPoint(l.latitude, l.longitude); //aus PPt von VO
                    mapController.setCenter(startingPoint); //aus PPt von VO
                }
            }
        }
        Polyline line = new Polyline();
        line.setPoints(geoPoints);
        map.getOverlayManager().add(line);
    }
}
