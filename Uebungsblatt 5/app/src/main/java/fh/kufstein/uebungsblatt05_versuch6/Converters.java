package fh.kufstein.uebungsblatt05_versuch6;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;

//Klasse zum Umwandeln von Date bzw. timestamp
public class Converters {


    //wenn der übergebene tsmp null ist, dann gib auch null zurück, ansonsten mach ein Date draus
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null: new Date(value);
    }

    //wenn das übergebene Date null ist, dann gib auch null zurück, ansonsten mach einen tmsp draus
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null: date.getTime();
    }
}

