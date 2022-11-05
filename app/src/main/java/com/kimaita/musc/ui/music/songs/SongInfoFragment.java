package com.kimaita.musc.ui.music.songs;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kimaita.musc.R;
import com.kimaita.musc.databinding.FragmentSongInfoBinding;
import com.kimaita.musc.models.Song;


public class SongInfoFragment extends Fragment {

    private FragmentSongInfoBinding binding;
    private Long mSongID;
    private Song mSong = new Song();
    private SongInfoViewModel mViewModel;
    private final String[] titles = new String[]{"Info", "Play History"};
    private Window window;

    public SongInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSongID = SongInfoFragmentArgs.fromBundle(getArguments()).getSongID();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        binding = FragmentSongInfoBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(SongInfoViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getSongTitleArt(mSongID).observe(getViewLifecycleOwner(), song -> {
            mSong = song;
            binding.topAppBar.setTitle(mSong.getSongTitle());
            binding.collapsingAppbar.setExpandedTitleColor(Color.parseColor("#FFFFFFFF"));
            binding.collapsingAppbar.setCollapsedTitleTextColor(getResources().getColor(R.color.design_default_color_on_primary));

            if (mSong.getAlbumArt() == null) {
                if (mSong.getSpotifyArtUri() != null) {
                    Glide.with(this)
                            .load(Uri.parse(mSong.getSpotifyArtUri()))
                            .into(binding.topAppbarAlbumart);
                }
            } else {
                binding.topAppbarAlbumart.setImageBitmap(mSong.getAlbumArt());
            }
            ViewPager2 viewPager2 = view.findViewById(R.id.pager_song_info);
            TabLayout tabLayout = view.findViewById(R.id.tabs_song_info);
            viewPager2.setAdapter(new PagerFragmentsAdapter(this));
            new TabLayoutMediator(tabLayout, viewPager2,
                    (tab, position) -> tab.setText(titles[position])).attach();

        });

        binding.infoAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    binding.topAppBar.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary));
                } else if (isShow) {
                    isShow = false;
                    binding.topAppBar.setBackgroundColor(Color.parseColor("#00000000"));
                }
            }
        });

        binding.fabPlaySong.setOnClickListener(v -> {
            Uri contentUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mSong.getSavedID());

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(contentUri, "audio/*");
            startActivity(intent);

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    private class PagerFragmentsAdapter extends FragmentStateAdapter {

        public PagerFragmentsAdapter(SongInfoFragment fragment) {
            super(fragment);

        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 1) {
                return SongPlayHistoryFragment.newInstance(mSongID);
            } else {
                return SongDetailsFragment.newInstance(mSongID);
            }
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }


}