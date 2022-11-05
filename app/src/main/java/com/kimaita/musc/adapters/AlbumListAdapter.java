package com.kimaita.musc.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kimaita.musc.R;
import com.kimaita.musc.models.Album;

import java.io.IOException;

public class AlbumListAdapter extends ListAdapter<Album, AlbumListAdapter.AlbumViewHolder> {
    public AlbumListAdapter(@NonNull DiffUtil.ItemCallback<Album> diffCallback, Context context, OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
        this.mContext = context;
    }

    private final OnItemClickListener listener;
    private final Context mContext;

    public interface OnItemClickListener {
        void onItemClick(Album album);
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_itemview, parent, false);
        return new AlbumViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.bindAlbum(getItem(position), listener);
    }

    public static class AlbumDiff extends DiffUtil.ItemCallback<Album> {

        @Override
        public boolean areItemsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
            return oldItem.getSavedAlbumID().equals(newItem.getSavedAlbumID());
        }
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewArt;
        private final TextView textViewTitle;
        private final TextView textViewArtist;
        private final TextView textViewSongCount;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewArt = itemView.findViewById(R.id.album_art);
            textViewTitle = itemView.findViewById(R.id.album_title);
            textViewArtist = itemView.findViewById(R.id.album_mins);
            textViewSongCount = itemView.findViewById(R.id.album_song_count);
        }

        public void bindAlbum(Album album, OnItemClickListener listener) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                try {
                    imageViewArt.setImageBitmap(mContext.getContentResolver().loadThumbnail(album.getAlbumUri(), new Size(200, 200), null));
                    Log.i("AlbumAdapter", "Loading Thumbnail from: " + album.getAlbumUri().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://media/external/audio/albumart"), album.getSavedAlbumID());
                    Glide.with(mContext)
                            .load(contentUri)
                            .placeholder(R.drawable.ic_music_note_24)
                            .fitCenter()
                            .into(imageViewArt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            textViewTitle.setText(album.getAlbumTitle());
            textViewTitle.setSelected(true);
            textViewArtist.setText(album.getArtist());
            textViewSongCount.setText(mContext.getString(R.string.song_count, album.getSongCount()));
            itemView.setOnClickListener(v -> listener.onItemClick(album));
        }

    }
}
