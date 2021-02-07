package fh.kufstein.uebungsblatt05_versuch6;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface TrackDao {

    @Query("SELECT * FROM Track")
    public List<Track> getAllTracks();

    @Query("SELECT * FROM Track WHERE id = :id")
    public Track getTrack(long id);

    @Query("SELECT * FROM Location")
    public List<Location> getAllLocations();

    @Query("SELECT * FROM Location WHERE trackId = :trackId ORDER BY timestamp")
    public List<Location> getLocations(long trackId);

    @Insert
    public long createTrack(Track track);

    @Update
    public void updateTrack(Track track);

    @Insert
    public void createLocation(Location location);


    @Delete
    public void deleteTrack(Track track);

    @Delete
    public void deleteLocation(Location location);
}

