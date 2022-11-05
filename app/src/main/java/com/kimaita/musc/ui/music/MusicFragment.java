package com.kimaita.musc.ui.music;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.kimaita.musc.R;
import com.kimaita.musc.databinding.FragmentMusicBinding;

public class MusicFragment extends Fragment {


    private FragmentMusicBinding binding;
    private final String[] onDeviceTitles = new String[]{"Tracks", "Albums", "Artists"};
    private final String[] offDeviceTitles = new String[]{"Tracks", "Artists"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMusicBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.pager.setAdapter(new PagerFragmentsAdapter(this, onDeviceTitles));
        new TabLayoutMediator(binding.tabLayout, binding.pager,
                (tab, position) -> tab.setText(onDeviceTitles[position])).attach();

        binding.topAppBar.setOnClickListener(v -> {
            if (binding.topAppBar.getTitle().equals("On-Device Music")) {
                binding.topAppBar.setTitle("Off-Device Music");
                binding.pager.setAdapter(new PagerFragmentsAdapter(this, offDeviceTitles));
                new TabLayoutMediator(binding.tabLayout, binding.pager,
                        (tab, position) -> tab.setText(offDeviceTitles[position])).attach();

            } else if (binding.topAppBar.getTitle().equals("Off-Device Music")) {
                binding.topAppBar.setTitle("On-Device Music");
                binding.pager.setAdapter(new PagerFragmentsAdapter(this, onDeviceTitles));
                new TabLayoutMediator(binding.tabLayout, binding.pager,
                        (tab, position) -> tab.setText(onDeviceTitles[position])).attach();
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.music_fragment_menu, menu);
        Log.i("MenuItems", "Inflated");
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_songs:
                binding.searchSongList.setVisibility(View.VISIBLE);
                break;
            case R.id.sort_songs:
                Log.i("MenuItems", "Sort Selected");
                break;
        }
        return true;
    }

    private static class PagerFragmentsAdapter extends FragmentStateAdapter {

        private final String[] titles;

        public PagerFragmentsAdapter(MusicFragment musicFragment, String[] titles) {
            super(musicFragment);
            this.titles = titles;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return MusicListFragment.newInstance(titles[position], titles.length);
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }

}