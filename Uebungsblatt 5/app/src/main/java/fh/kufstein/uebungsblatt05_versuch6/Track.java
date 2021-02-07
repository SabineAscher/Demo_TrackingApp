package fh.kufstein.uebungsblatt05_versuch6;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity
public class Track {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String title;
    public String description;
    public Date start;
    public Date end;

    //Methode zur Berechnung der Duration eines Tracks
    public String getDuration() {
        if (start != null && end != null) {
            long time = end.getTime() - start.getTime();
            long diff;

            long hours = time / 1000 / 60 / 60; //von Millisekunden in Stunden

            diff = time - (hours * 1000 * 60 * 60);
            long minutes = diff / 1000 / 60; //von Millisekunden in Minuten

            diff = time - (minutes * 1000 * 60);
            long seconds = diff / 1000; //von Millisekunden in Sekunden

            return hours + "h " + minutes + "m " + seconds + "s";
        } else {
            return "";
        }
    }

    //ermitteln der Endzeit
    public void stop() {
        end = Calendar.getInstance().getTime();
    }

    //vorläufiges Belegen des Titels mit Date + Uhrzeit
    public static Track now() {
        Date now = Calendar.getInstance().getTime(); //liefert heutiges Datum mit Uhrzeit
        DateFormat df = DateFormat.getDateInstance();

        Track t = new Track();
        t.start = now;
        t.title = df.format(now);
        return t;
    }
}

