package com.kimaita.musc.ui.dashboard;

import static java.time.temporal.ChronoUnit.HOURS;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.kimaita.musc.BottomSheetArtistDetails;
import com.kimaita.musc.BottomSheetSongDetails;
import com.kimaita.musc.R;
import com.kimaita.musc.SpotifyCredentialsHandler;
import com.kimaita.musc.databinding.FragmentDashboardBinding;
import com.kimaita.musc.models.Artist;
import com.kimaita.musc.models.DashboardEntry;
import com.kimaita.musc.models.PlayRecord;
import com.kimaita.musc.models.PlaySession;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    SongListAdapter songListAdapter;
    ArtistListAdapter artistListAdapter;
    private Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        artistListAdapter = new ArtistListAdapter(new ArtistListAdapter.EntryDiff(), getContext(), dashboardEntry -> {
            BottomSheetArtistDetails bottomSheet = new BottomSheetArtistDetails(dashboardEntry, requireActivity().getApplication());
            bottomSheet.setCancelable(true);
            //BottomSheetBehavior.from(container.findViewById(R.id.sheet_song_details)).setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
            bottomSheet.show(getParentFragmentManager(), "ArtistDetailsSheet");

        });
        songListAdapter = new SongListAdapter(new SongListAdapter.EntryDiff(), getContext(), dashboardEntry -> {
            BottomSheetSongDetails bottomSheet = new BottomSheetSongDetails(dashboardEntry);
            bottomSheet.setCancelable(true);
            //BottomSheetBehavior.from(container.findViewById(R.id.sheet_song_details)).setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
            bottomSheet.show(getParentFragmentManager(), "SongDetailsSheet");
        });
        binding.recyclerSongs.setAdapter(songListAdapter);
        binding.recyclerSongs.setLayoutManager(new LinearLayoutManager(mContext));
        binding.recyclerArtists.setAdapter(artistListAdapter);
        LinearLayoutManager hLinearLayoutManager = new LinearLayoutManager(mContext);
        hLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.recyclerArtists.setLayoutManager(hLinearLayoutManager);

       /* dashboardViewModel.getOffDeviceSongs().observe(getViewLifecycleOwner(), new Observer<List<DashboardEntry>>() {
            @Override
            public void onChanged(List<DashboardEntry> dashboardEntries) {
                if (dashboardEntries != null && dashboardEntries.size() > 0) {
                    //fetchOffDeviceSongData(dashboardEntries);
                }

            }
        });*/
        return binding.getRoot();
    }

    private void fetchOffDeviceSongData(List<DashboardEntry> dashboardEntries) {
        String query = null;
        String token = SpotifyCredentialsHandler.getToken(requireContext());
        for (DashboardEntry entry : dashboardEntries) {
            //q=album:gold%20artist:abba&type=album
            query = getString(R.string.search_query_filters, entry.getTitle(), entry.getName());
            Log.i("Fetching metadata", "Fetching for: " + query);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dashboardViewModel.insertSessions(loadFileSessions().toArray(new PlaySession[0]));
        dashboardViewModel.insertArtists(loadFileArtists().toArray(new Artist[0]));
        dashboardViewModel.insertRecords(loadFileRecords().toArray(new PlayRecord[0]));

        dashboardViewModel.getTopSongsList().observe(getViewLifecycleOwner(), dashboardEntries -> {
            songListAdapter.submitList(dashboardEntries);
        });
        dashboardViewModel.getTopArtistsList().observe(getViewLifecycleOwner(), dashboardEntries -> artistListAdapter.submitList(dashboardEntries));
        dashboardViewModel.getTotalPlayTime().observe(getViewLifecycleOwner(), aLong -> {
            if (aLong != null) {
                binding.playtime.setText(formatPlayTime(aLong));
            } else {
                binding.playtime.setText(R.string.no_records);
            }
        });
    }

    private ArrayList<PlayRecord> loadFileRecords() {
        File file = new File(requireContext().getFilesDir(), "playRecords");
        ArrayList<PlayRecord> playRecord = new ArrayList<>();
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                playRecord = (ArrayList<PlayRecord>) ois.readObject();
                fis.close();
                if (file.delete()) {
                    Toast.makeText(getContext(), "Loaded", Toast.LENGTH_SHORT).show();
                }
                syncSongIDs(playRecord);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return playRecord;
    }

    private ArrayList<PlaySession> loadFileSessions() {
        File file = new File(requireContext().getFilesDir(), "playSessions");
        ArrayList<PlaySession> playSessions = new ArrayList<>();
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                playSessions = (ArrayList<PlaySession>) ois.readObject();
                ois.close();
                if (file.delete()) {
                    Toast.makeText(getContext(), "Loaded", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return playSessions;
    }

    private ArrayList<Artist> loadFileArtists() {
        File file = new File(requireContext().getFilesDir(), "artists");
        ArrayList<Artist> artists = new ArrayList<>();
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                artists = (ArrayList<Artist>) ois.readObject();
                fis.close();
                if (file.delete()) {
                    Toast.makeText(getContext(), "Loaded", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return artists;
    }


    public static class SongListAdapter extends ListAdapter<DashboardEntry, SongListAdapter.ListViewHolder> {

        private final OnItemClickListener listener;
        private final Context mContext;

        protected SongListAdapter(@NonNull DiffUtil.ItemCallback<DashboardEntry> diffCallback, Context context, OnItemClickListener listener) {
            super(diffCallback);
            this.listener = listener;
            mContext = context;
        }

        public interface OnItemClickListener {
            void onItemClick(DashboardEntry dashboardEntry);
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

        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_itemview, parent, false);
            return new ListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
            holder.bind(getItem(position), listener);
        }

        private class ListViewHolder extends RecyclerView.ViewHolder {
            MaterialTextView songTitle, songArtist, songPlayDuration, songPlayCount;
            ImageView songArt;

            public ListViewHolder(@NonNull View itemView) {
                super(itemView);
                songArt = itemView.findViewById(R.id.song_art);
                songTitle = itemView.findViewById(R.id.song_title);
                songArtist = itemView.findViewById(R.id.song_artist);
                songPlayDuration = itemView.findViewById(R.id.song_plays_time);
                songPlayCount = itemView.findViewById(R.id.song_plays_count);
            }

            public void bind(DashboardEntry item, OnItemClickListener listener) {
                /*try {
                    Uri contentUri = ContentUris.withAppendedId(
                            sArtworkUri, song.getAlbumID());
                    Glide.with(mContext)
                            .load(contentUri)
                            .placeholder(R.drawable.ic_music_note_24)
                            .fitCenter()
                            .into(songArt);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                songTitle.setText(item.getTitle());
                songArtist.setText(item.getName());
                songPlayCount.setText(mContext.getString(R.string.song_plays, item.getPlays()));
                songPlayDuration.setText(mContext.getString(R.string.song_play_duration, new SimpleDateFormat("mm:ss ").format(item.getDuration())));
                itemView.setOnClickListener(v -> listener.onItemClick(item));
            }
        }
    }

    public static class ArtistListAdapter extends ListAdapter<DashboardEntry, ArtistListAdapter.ListViewHolder> {
        private final OnItemClickListener listener;
        private final Context mContext;

        protected ArtistListAdapter(@NonNull DiffUtil.ItemCallback<DashboardEntry> diffCallback, Context context, OnItemClickListener listener) {
            super(diffCallback);
            this.listener = listener;
            this.mContext = context;
        }

        public interface OnItemClickListener {
            void onItemClick(DashboardEntry dashboardEntry);
        }

        public static class EntryDiff extends DiffUtil.ItemCallback<DashboardEntry> {
            @Override
            public boolean areItemsTheSame(@NonNull DashboardEntry oldItem, @NonNull DashboardEntry newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull DashboardEntry oldItem, @NonNull DashboardEntry newItem) {
                return oldItem.getName().equals(newItem.getName());
            }
        }

        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_itemview, parent, false);
            return new ListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
            holder.bind(getItem(position), listener);
        }

        private class ListViewHolder extends RecyclerView.ViewHolder {
            MaterialTextView artistName, songCount, artistListenTime;

            public ListViewHolder(@NonNull View itemView) {
                super(itemView);
                artistName = itemView.findViewById(R.id.artist_name);
                songCount = itemView.findViewById(R.id.artist_songs);
                artistListenTime = itemView.findViewById(R.id.artist_time);
            }

            public void bind(DashboardEntry item, OnItemClickListener listener) {
                String[] artist = item.getName().split(", ");
                artistName.setSelected(true);
                artistName.setText(artist[0]);
                songCount.setText(mContext.getString(R.string.song_count, item.getPlays()));
                artistListenTime.setText(formatPlayTime(item.getDuration()));
                itemView.setOnClickListener(v -> listener.onItemClick(item));
            }
        }
    }

    public static class AlbumListAdapter extends ListAdapter<DashboardEntry, AlbumListAdapter.ListViewHolder> {

        protected AlbumListAdapter(@NonNull DiffUtil.ItemCallback<DashboardEntry> diffCallback) {
            super(diffCallback);
        }

        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_itemview, parent, false);
            return new ListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        }

        private static class ListViewHolder extends RecyclerView.ViewHolder {
            public ListViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }

    private void syncSongIDs(ArrayList<PlayRecord> records) {
        for (PlayRecord record : records) {
            if (record.getRecordSongID() == null) {
                ContentResolver musicResolver = requireActivity().getApplication().getContentResolver();
                Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String selection;
                String[] selectionArgs;
                if (record.getSongArtist().equalsIgnoreCase("Unknown Artist") || record.getSongArtist().equals("")) {
                    selection = MediaStore.Audio.Media.TITLE + " LIKE ?";
                    selectionArgs = new String[]{record.getSongTitle()};
                } else {
                    selection = MediaStore.Audio.Media.TITLE + " LIKE ? AND " + MediaStore.Audio.Media.ARTIST + " LIKE ? COLLATE NOCASE";
                    selectionArgs = new String[]{record.getSongTitle(), record.getSongArtist()};
                }
                String[] projection = {MediaStore.Audio.Media._ID};
                Cursor musicCursor = musicResolver.query(musicUri, projection, selection, selectionArgs, null);
                if (musicCursor == null) {
                    // query failed, handle error.
                    return;
                } else if (!musicCursor.moveToFirst()) {
                    // no media on the device
                    return;
                } else {
                    int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    if (musicCursor.moveToFirst()) {
                        record.setRecordSongID(musicCursor.getLong(idColumn));
                        Log.i("SyncSongIDS", "Set ID for " + record.getSongTitle() + " - " + record.getRecordSongID());
                    }
                }
                musicCursor.close();
            }
        }
    }


    public static String formatPlayTime(long time) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), TimeZone.getDefault().toZoneId());
            LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());
            long hours = HOURS.between(start, end);
            return hours + ":" + new SimpleDateFormat("mm:ss ", Locale.getDefault()).format(time);
        } else {
            return time / 3600000 + ":" + new SimpleDateFormat("mm:ss ").format(time);
        }
    }

}