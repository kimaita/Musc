package com.kimaita.musc.ui.music.songs;

import static com.kimaita.musc.ui.dashboard.DashboardFragment.formatPlayTime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kimaita.musc.R;
import com.kimaita.musc.databinding.SongDetailsFragmentBinding;
import com.kimaita.musc.models.Song;

public class SongDetailsFragment extends Fragment {

    private SongDetailsFragmentBinding binding;
    private static final String ARG_SONG = "paramSong";
    private Song mSong;
    private Long mSongID;
    private SongInfoViewModel mViewModel;

    public static SongDetailsFragment newInstance(Long songID) {
        SongDetailsFragment fragment = new SongDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SONG, songID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        mSongID = getArguments().getLong(ARG_SONG);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = SongDetailsFragmentBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(SongInfoViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getmSong(mSongID).observe(getViewLifecycleOwner(), song -> {
            mSong = song;
            binding.detailsAlbum.setText(getString(R.string.song_details_album, mSong.getSongAlbum()));
            binding.detailsArtist.setText(getString(R.string.song_details_artist, mSong.getSongArtist()));
            binding.detailsGenre.setText(getString(R.string.song_details_genre, mSong.getSongGenre()));
            binding.detailsLength.setText(getString(R.string.song_details_size, formatPlayTime(mSong.getSongLength())));
            if (mSong.getSpotifyUri() != null) {
                binding.detailsRelease.setText(song.getSpotifyUri());
            }
        });
    }
}