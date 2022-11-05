package com.kimaita.musc;

import static com.kimaita.musc.ui.dashboard.DashboardFragment.formatPlayTime;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.imageview.ShapeableImageView;
import com.kimaita.musc.models.DashboardEntry;
import com.kimaita.musc.models.Song;
import com.kimaita.musc.ui.dashboard.DashboardViewModel;

public class BottomSheetArtistDetails extends BottomSheetDialogFragment {

    private final DashboardEntry entry;
    private final Application application;

    public BottomSheetArtistDetails(DashboardEntry dashboardEntry, Application application) {
        this.entry = dashboardEntry;
        this.application = application;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.artist_details,
                container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ShapeableImageView artistPic = view.findViewById(R.id.artist_details_dp);
        TextView textArtistName = view.findViewById(R.id.artist_details_name);
        TextView textArtistPlayTime = view.findViewById(R.id.artist_details_playtime);
        RecyclerView recyclerViewSongs = view.findViewById(R.id.artist_details_songs);
        TextView labelShowSongs = view.findViewById(R.id.show_song_list);
        labelShowSongs.setOnClickListener(v -> {
            if (recyclerViewSongs.getVisibility() == View.GONE) {
                recyclerViewSongs.setVisibility(View.VISIBLE);
                labelShowSongs.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_arrow_drop_up_24), null);
            } else if (recyclerViewSongs.getVisibility() == View.VISIBLE) {
                recyclerViewSongs.setVisibility(View.GONE);
                labelShowSongs.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_arrow_drop_down_24), null);

            }
        });
        ArtistSongListAdapter adapter = new ArtistSongListAdapter(new ArtistSongListAdapter.EntryDiff(), application);
        DashboardViewModel viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        recyclerViewSongs.setAdapter(adapter);
        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel.getArtistRecords(entry.getName()).observe(getViewLifecycleOwner(), adapter::submitList
        );
        textArtistName.setText(entry.getName());
        textArtistPlayTime.setText(getString(R.string.song_details_playtime, formatPlayTime(entry.getDuration())));

    }

    public static class ArtistSongListAdapter extends ListAdapter<DashboardEntry, ArtistSongListAdapter.ListViewHolder> {

        private final Application application;

        public ArtistSongListAdapter(@NonNull DiffUtil.ItemCallback<DashboardEntry> diffCallback, Application application) {
            super(diffCallback);
            this.application = application;
        }

        @NonNull
        @Override
        public ArtistSongListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_itemview, parent, false);
            return new ListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ArtistSongListAdapter.ListViewHolder holder, int position) {
            holder.bindSong(getItem(position));

        }

        public static class EntryDiff extends DiffUtil.ItemCallback<DashboardEntry> {
            @Override
            public boolean areItemsTheSame(@NonNull DashboardEntry oldItem, @NonNull DashboardEntry newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull DashboardEntry oldItem, @NonNull DashboardEntry newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }
        }

        private class ListViewHolder extends RecyclerView.ViewHolder {
            TextView textSongTitle, textSongLength, textSongPlays, textSongPlayTime;
            ImageView songArt;


            public ListViewHolder(@NonNull View itemView) {
                super(itemView);
                textSongTitle = itemView.findViewById(R.id.song_title);
                textSongLength = itemView.findViewById(R.id.song_artist);
                textSongPlays = itemView.findViewById(R.id.song_plays_count);
                textSongPlayTime = itemView.findViewById(R.id.song_plays_time);
                songArt = itemView.findViewById(R.id.song_art);
            }

            public void bindSong(DashboardEntry entry) {
                Song song = getSong(entry.getIdentifier(), application);
                textSongTitle.setText(entry.getTitle());
                textSongTitle.setMaxLines(2);
                textSongPlays.setText("Plays: " + entry.getPlays());
                if (entry.getIdentifier() == null || entry.getIdentifier() == 0) {
                    textSongLength.setText("For: " + formatPlayTime(entry.getDuration()));
                    textSongPlayTime.setVisibility(View.GONE);
                } else {
                    textSongPlayTime.setText("For: " + formatPlayTime(entry.getDuration()));
                    textSongLength.setText("Length: " + formatPlayTime(song.getSongLength()));
                }
                if (entry.getImage() != null) {
                    if (entry.getImage().length != 0) {
                        songArt.setImageBitmap(BitmapFactory.decodeByteArray(entry.getImage(), 0, entry.getImage().length));
                    }
                } else if (song.getAlbumArt() != null) {
                    songArt.setImageBitmap(song.getAlbumArt());
                }
            }

        }
    }

    public static Song getSong(Long id, Application application) {
        Song song = new Song();
        if (id != null) {
            Uri songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
            ContentResolver musicResolver = application.getContentResolver();
            String[] projection = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION};
            Cursor musicCursor = musicResolver.query(songUri, projection, null, null, null);
            if (musicCursor == null) {
                // query failed, handle error.
                Log.w("MusicCursor", "Couldn't initialise cursor.");

            } else if (!musicCursor.moveToFirst()) {
                // no media on the device
                Log.w("MusicCursor", "Couldn't find: " + id);
            } else {
                int lengthColumn = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                if (musicCursor.moveToFirst()) {
                    song.setSongLength(musicCursor.getLong(lengthColumn));
                }
            }
            assert musicCursor != null;
            musicCursor.close();
        }
        return song;
    }


}
