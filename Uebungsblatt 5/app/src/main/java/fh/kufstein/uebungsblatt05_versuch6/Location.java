package fh.kufstein.uebungsblatt05_versuch6;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

//mit der Annotation wird angegeben, dass aus Location eine Tabelle werden soll
@Entity
public class Location {

    //automatisches Erstellen eines primary keys
    @PrimaryKey(autoGenerate = true)
    public long id;

    public double latitude;
    public double longitude;
    public double altidude;
    public Date timestamp;
    public long trackId;

    //tmsp mit Datum belegen
    public void setTimestamp(long timestamp) {
        this.timestamp = new Date(timestamp);
    }

    //liefert eine Location mit allen wichtigen Daten zurück -- mit Track verknüpft
    public static Location from(android.location.Location location, Track track) {
        Location l = new Location();
        l.latitude = location.getLatitude();
        l.longitude = location.getLongitude();
        l.altidude = location.getAltitude();
        l.setTimestamp(location.getTime());
        l.trackId = track.id;
        return l;
    }
}

