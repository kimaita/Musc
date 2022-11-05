package com.kimaita.musc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.kimaita.musc.R;
import com.kimaita.musc.models.Artist;

public class ArtistListAdapter extends ListAdapter<Artist, ArtistListAdapter.ArtistViewHolder> {
    public ArtistListAdapter(@NonNull DiffUtil.ItemCallback<Artist> diffCallback, Context context, OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
        this.mContext = context;
    }

    private final OnItemClickListener listener;
    private final Context mContext;

    public interface OnItemClickListener {
        void onItemClick(Artist artist);
    }

    @NonNull
    @Override
    public ArtistListAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_itemview, parent, false);
        return new ArtistListAdapter.ArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistListAdapter.ArtistViewHolder holder, int position) {
        holder.bindArtist(getItem(position), listener);
    }

    public static class ArtistDiff extends DiffUtil.ItemCallback<Artist> {

        @Override
        public boolean areItemsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
            return oldItem.getSavedArtistID().equals(newItem.getSavedArtistID());
        }
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView imageViewArt;
        private final TextView textViewTitle;
        private final TextView textViewAlbumCount;
        private final TextView textViewSongCount;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewArt = itemView.findViewById(R.id.artist_dp);
            textViewTitle = itemView.findViewById(R.id.artist_name);
            textViewAlbumCount = itemView.findViewById(R.id.artist_time);
            textViewSongCount = itemView.findViewById(R.id.artist_songs);
        }

        public void bindArtist(Artist artist, ArtistListAdapter.OnItemClickListener listener) {

            textViewTitle.setText(artist.getArtistName());
            if (artist.getAlbumCount() != null)
                textViewAlbumCount.setText(mContext.getString(R.string.album_count, artist.getAlbumCount()));
            textViewSongCount.setText(mContext.getString(R.string.song_count, artist.getArtistSongCount()));
            itemView.setOnClickListener(v -> listener.onItemClick(artist));
        }

    }
}
