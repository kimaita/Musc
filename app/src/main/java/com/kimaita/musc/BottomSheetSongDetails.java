package com.kimaita.musc;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.kimaita.musc.models.DashboardEntry;
import com.kimaita.musc.models.Song;

import java.text.SimpleDateFormat;


public class BottomSheetSongDetails extends BottomSheetDialogFragment {

    private final DashboardEntry entry;
    FloatingActionButton fabPlay;
    MaterialTextView textSongTitle;
    MaterialTextView textSongLength;
    MaterialTextView textSongArtist;
    MaterialTextView textSongAlbum;
    MaterialTextView textSongGenre;
    MaterialTextView textSongYear;
    MaterialTextView textSongPlayCount;
    MaterialTextView textSongPlayTime;
    ImageView imageSongArt;
    Song mSong = new Song();
    private boolean isStreamed = false;
    private boolean isMissing = false;


    public BottomSheetSongDetails(DashboardEntry dashboardEntry) {
        this.entry = dashboardEntry;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.song_details,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        fabPlay = v.findViewById(R.id.fab_play_song);
        textSongTitle = v.findViewById(R.id.song_detail_title);
        textSongLength = v.findViewById(R.id.song_detail_len);
        textSongArtist = v.findViewById(R.id.song_detail_artist);
        textSongAlbum = v.findViewById(R.id.song_detail_album);
        textSongGenre = v.findViewById(R.id.song_detail_genre);
        textSongYear = v.findViewById(R.id.song_detail_yr);
        textSongPlayCount = v.findViewById(R.id.song_detail_plays);
        textSongPlayTime = v.findViewById(R.id.song_detail_playtime);
        imageSongArt = v.findViewById(R.id.song_detail_art);
        fabPlay.setVisibility(View.GONE);
        fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if (entry.getImage() != null) {
            if (entry.getImage().length != 0) {
                imageSongArt.setImageBitmap(BitmapFactory.decodeByteArray(entry.getImage(), 0, entry.getImage().length));
            }
        }
        textSongTitle.setText(entry.getTitle());
        textSongArtist.setText(entry.getName());
        textSongPlayCount.setText(getString(R.string.song_plays, entry.getPlays()));
        textSongPlayTime.setText(getString(R.string.song_details_playtime, new SimpleDateFormat("mm:ss ").format(entry.getDuration())));
        mSong = initSong();
        if (isMissing || isStreamed) {
            textSongAlbum.setVisibility(View.GONE);
            textSongGenre.setVisibility(View.GONE);
            textSongYear.setVisibility(View.GONE);
            textSongLength.setVisibility(View.GONE);
        } else {
            textSongAlbum.setText(getString(R.string.song_details_album, mSong.getSongAlbum()));
            if (mSong.getSongGenre() == null) {
                textSongGenre.setVisibility(View.GONE);
            } else {
                textSongGenre.setText(getString(R.string.song_details_genre, mSong.getSongGenre()));
            }
            if (mSong.getSongYear() == 0) {
                textSongYear.setVisibility(View.GONE);
            } else {
                textSongYear.setText(getString(R.string.song_details_year, mSong.getSongYear()));
            }
            textSongLength.setText(getString(R.string.song_details_size, new SimpleDateFormat("m:ss ").format(mSong.getSongLength())));
        }
    }

    private Song initSong() {
        Song song = new Song();
        if (entry.getIdentifier() == null || entry.getIdentifier() == 1) {
            isStreamed = true;
        } else {
            Uri contentUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, entry.getIdentifier());
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getContext(), contentUri);
            song.setSongGenre(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
            song.setSongAlbum(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            song.setSongLength(Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));

            if (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR) != null) {
                song.setSongYear(Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)));
            }

            Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
            Cursor detailsCursor = getActivity().getContentResolver().query(musicUri, null, selection, null, null);

            if (detailsCursor == null) {
                // query failed, handle error.
                isMissing = true;
                Log.e("SongDetails", "Couldn't create Cursor");
            } else if (!detailsCursor.moveToFirst()) {
                //media not found on the device
                isMissing = true;
            } else {
                int idColumn = detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                while (detailsCursor.moveToNext()) {
                    if (entry.getIdentifier() == detailsCursor.getLong(idColumn)) {
                        if (song.getSongYear() == 0) {
                            song.setSongYear(detailsCursor.getInt(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)));
                        }
                    }
                }

            }
            assert detailsCursor != null;
            detailsCursor.close();
            retriever.release();
        }
        return song;
    }
}

