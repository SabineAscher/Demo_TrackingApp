package fh.kufstein.uebungsblatt05_versuch6;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    //Adapter übernimmt nicht das Eventhandling --> sagt nur, dass geklickt wird,
    // erzeugt dafür das Event und gibt den geklickten Track mit

    public interface OnTrackEditListener {
        void onTrackEdit(Track track); //Event an sich, bekommt Track übergeben, das Event kann von außen abgearbeitet werden
    }

    public interface OnTrackMapListener {
        void onTrackMap(Track track);
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView subtitle;
        public TextView description;
        public Track track;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);

            this.title = itemView.findViewById(R.id.track_title);
            this.subtitle = itemView.findViewById(R.id.track_subtitle);
            this.description = itemView.findViewById(R.id.track_description);

            //wenn jemand auf den Button klickt, soll auf den mapListener die onTrack() aufgerufem werden
            itemView.findViewById(R.id.button_map)
                    .setOnClickListener((View v) -> {
                        if (mapListener != null) mapListener.onTrackMap(track);
                    });

            itemView.findViewById(R.id.button_edit)
                    .setOnClickListener((View v) -> {
                        if (editListener != null) editListener.onTrackEdit(track);
                    });
        }
    }

    protected OnTrackEditListener editListener;
    protected OnTrackMapListener mapListener;
    protected TrackDao dao;
    protected List<Track> tracks;

    public TrackAdapter(TrackDao dao) {
        this.dao = dao;
        loadTracks();
    }

    public void loadTracks() {
        this.tracks = dao.getAllTracks();
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View card = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.track_view, parent, false); //false weil es noch nicht in die RCV hinzugefügt werden soll  --> das macht onBind
        return new TrackViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.title.setText(track.title);
        holder.subtitle.setText(track.getDuration());
        holder.description.setText(track.description);
        holder.track = track;
    }

    @Override
    public int getItemCount() {
        return tracks != null ? tracks.size() : 0;
    }


    //setter bieten den Vorteil, dass man auch null setzten kann, wenn man Listener entfernen will --> braucht damit keine eigene remove() mehr
    public void setEditListener(OnTrackEditListener editListener) {
        this.editListener = editListener;
    }

    public void setMapListener(OnTrackMapListener mapListener) {
        this.mapListener = mapListener;
    }
}

