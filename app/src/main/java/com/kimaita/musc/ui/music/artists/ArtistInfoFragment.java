package com.kimaita.musc.ui.music.artists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kimaita.musc.R;
import com.kimaita.musc.models.Artist;

public class ArtistInfoFragment extends Fragment {

    private ArtistInfoViewModel mViewModel;
    private Artist mArtist = new Artist();

    public static ArtistInfoFragment newInstance() {
        return new ArtistInfoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtist.setSavedArtistID(ArtistInfoFragmentArgs.fromBundle(getArguments()).getArtistID());
        mArtist.setArtistName(ArtistInfoFragmentArgs.fromBundle(getArguments()).getArtistName());
        mArtist.setAlbumCount(ArtistInfoFragmentArgs.fromBundle(getArguments()).getArtistAlbumSongCount()[0]);
        mArtist.setArtistSongCount(ArtistInfoFragmentArgs.fromBundle(getArguments()).getArtistAlbumSongCount()[1]);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.artist_info_fragment, container, false);
    }



}