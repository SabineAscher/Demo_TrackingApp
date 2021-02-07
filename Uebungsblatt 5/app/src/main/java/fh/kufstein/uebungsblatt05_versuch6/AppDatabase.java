package fh.kufstein.uebungsblatt05_versuch6;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/*
Room ist eine Abstraktion über die SQLite Datenbank, ähnlich ORM
braucht dependency in build.gradle
arbeitet mit Annotationen
 */

//Angabe aller Entitäten, die über die DB verarbeitet werden sollen
@Database(entities = {Location.class, Track.class}, version = 1)
@TypeConverters({Converters.class})  //verwende den Converter bei date/tmsp
public abstract class AppDatabase extends RoomDatabase {

    protected static AppDatabase instance;

    //Room basiert auf dem DAO
    public abstract TrackDao getTrackDao();

    //singleton -- liefert die eine Instanz der AppDatabase zurück
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, "tracks")
                    .allowMainThreadQueries() //Deaktiviert die Hauptthread Abfrage für Room - Room stellt nämlich sicher, dass auf die DB im Hauptthread niemals zugegriffen wird, da dies den Hauptthread sperren und eine ANR auslösen kann
                    .build();
        }
        return instance;
    }

}

