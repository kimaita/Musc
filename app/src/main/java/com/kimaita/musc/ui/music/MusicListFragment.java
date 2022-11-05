package com.kimaita.musc.ui.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kimaita.musc.R;
import com.kimaita.musc.adapters.AlbumListAdapter;
import com.kimaita.musc.adapters.ArtistListAdapter;
import com.kimaita.musc.adapters.TrackListAdapter;
import com.kimaita.musc.databinding.FragmentMusicListBinding;

public class MusicListFragment extends Fragment {

    private FragmentMusicListBinding binding;
    private static final String ARG_PARAM = "param1";
    private static final String ARG_PARAM_LENGTH = "param2";
    private String mParam1;
    private int mParamLength;
    private MusicViewModel musicViewModel;
    private TrackListAdapter trackListAdapter;
    private TrackListAdapter offDeviceTrackListAdapter;
    private ArtistListAdapter artistListAdapter;
    private AlbumListAdapter albumListAdapter;

    public MusicListFragment() {        /* Required empty public constructor*/ }

    public static MusicListFragment newInstance(String param1, int param2) {
        MusicListFragment fragment = new MusicListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param1);
        args.putInt(ARG_PARAM_LENGTH, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM);
            mParamLength = getArguments().getInt(ARG_PARAM_LENGTH);
        }
        musicViewModel = new ViewModelProvider(this).get(MusicViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMusicListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = binding.recyclerSonglist;
        initializeAdapters();
        if (mParamLength == 3) {
            switch (mParam1) {
                case "Albums":
                    mRecyclerView.setAdapter(albumListAdapter);
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    musicViewModel.getOnDeviceAlbumList().observe(getViewLifecycleOwner(), albums -> albumListAdapter.submitList(albums));
                    break;
                case "Artists":
                    mRecyclerView.setAdapter(artistListAdapter);
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    musicViewModel.getOnDeviceArtistList().observe(getViewLifecycleOwner(), artists -> artistListAdapter.submitList(artists));
                    break;
                default:
                    mRecyclerView.setAdapter(trackListAdapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    musicViewModel.getOnDeviceSongList().observe(getViewLifecycleOwner(), songs -> trackListAdapter.submitList(songs));
            }
        } else if (mParamLength == 2) {
            if (mParam1.equals("Tracks")) {
                mRecyclerView.setAdapter(offDeviceTrackListAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                musicViewModel.getOffDeviceSongList().observe(getViewLifecycleOwner(), songs -> offDeviceTrackListAdapter.submitList(songs));

            } else if (mParam1.equals("Artists")) {
                mRecyclerView.setAdapter(artistListAdapter);
                mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                musicViewModel.getOffDeviceArtistList().observe(getViewLifecycleOwner(), artists -> artistListAdapter.submitList(artists));
            }
        }
        /*binding.searchSongList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                trackListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                trackListAdapter.getFilter().filter(newText);
                return false;
            }
        });*/
    }


    private void initializeAdapters() {
        trackListAdapter = new TrackListAdapter(new TrackListAdapter.SongDiff(), getContext(), song -> Navigation.findNavController(requireView()).navigate(
                MusicFragmentDirections.actionNavigationPlayerToSongInfoFragment(song.getSavedID())));
        offDeviceTrackListAdapter = new TrackListAdapter(new TrackListAdapter.SongDiff(), getContext(), song -> Navigation.findNavController(requireView()).navigate(
                MusicFragmentDirections.actionNavigationPlayerToOffDeviceSongInfoFragment(song.getSpotifyID())));

        albumListAdapter = new AlbumListAdapter(new AlbumListAdapter.AlbumDiff(), getContext(), album -> {

        });
        artistListAdapter = new ArtistListAdapter(new ArtistListAdapter.ArtistDiff(), getContext(), artist -> {
            /*Navigation.findNavController(requireView()).navigate(
                MusicFragmentDirections.actionNavigationPlayerToArtistInfoFragment(artist.getSavedArtistID(), artist.getArtistName(), new int[]{artist.getAlbumCount(), artist.getArtistSongCount()}))*/
        });

    }
}