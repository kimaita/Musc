package com.kimaita.musc.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.kimaita.musc.R;
import com.kimaita.musc.models.Song;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TrackListAdapter extends ListAdapter<Song, TrackListAdapter.SongViewHolder> implements Filterable {

    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("m:ss ");
    private final OnItemClickListener listener;
    private final Context mContext;
    private List<Song> searchTracks;

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();
                if (!query.isEmpty()) {
                    List<Song> filteredTracks = new ArrayList<>();
                    for (Song row : getCurrentList()) {
                        if (row.getSongTitle().toLowerCase().contains(query)) {
                            filteredTracks.add(row);
                        }
                    }
                    searchTracks = filteredTracks;
                } else {
                    searchTracks = getCurrentList();
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = searchTracks;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                searchTracks = (List<Song>) results.values;
                submitList(searchTracks);
            }
        };
    }

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    public TrackListAdapter(DiffUtil.ItemCallback<Song> diffCallback, Context context, OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
        this.mContext = context;
        this.searchTracks = getCurrentList();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViewArt;
        private final MaterialTextView textViewTitle;
        private final MaterialTextView textViewArtist;
        final public Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        public SongViewHolder(View itemView) {
            super(itemView);
            imageViewArt = itemView.findViewById(R.id.player_song_art);
            textViewArtist = itemView.findViewById(R.id.player_song_artist);
            textViewTitle = itemView.findViewById(R.id.player_song_title);
        }

        public void bindSong(Song song, OnItemClickListener listener) {
            try {
                Uri contentUri = ContentUris.withAppendedId(
                        sArtworkUri, song.getAlbumID());
                Glide.with(mContext)
                        .load(contentUri)
                        .placeholder(R.drawable.ic_music_note_24)
                        .fitCenter()
                        .into(imageViewArt);
            } catch (Exception e) {
                e.printStackTrace();
            }
            textViewTitle.setText(song.getSongTitle());
            textViewArtist.setText(song.getSongArtist());
            itemView.setOnClickListener(v -> listener.onItemClick(song));
        }
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate an item view.
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songlist_itemview, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        // Retrieve the data for that position. Add the data to the view holder.
        holder.bindSong(getItem(position), listener);
    }


    public static class SongDiff extends DiffUtil.ItemCallback<Song> {

        @Override
        public boolean areItemsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
            return oldItem.getSavedID().equals(newItem.getSavedID());
        }
    }

}
