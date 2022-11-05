package com.kimaita.musc.ui.music.songs;

import static com.kimaita.musc.ui.dashboard.DashboardFragment.formatPlayTime;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.kimaita.musc.R;
import com.kimaita.musc.databinding.FragmentSongPlayHistoryBinding;
import com.kimaita.musc.models.RecordDate;
import com.kimaita.musc.models.RecordDets;
import com.kimaita.musc.models.RecordItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SongPlayHistoryFragment extends Fragment {

    private FragmentSongPlayHistoryBinding binding;
    private SongInfoViewModel viewModel;
    private static final String ARG_PARAM1 = "param1";
    private Long mSongID;

    public SongPlayHistoryFragment() {
        // Required empty public constructor
    }

    public static SongPlayHistoryFragment newInstance(Long id) {
        SongPlayHistoryFragment fragment = new SongPlayHistoryFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSongID = getArguments().getLong(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongPlayHistoryBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(SongInfoViewModel.class);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecordsAdapter recordAdapter = new RecordsAdapter(new RecordsAdapter.RecordDiff(), getContext());
        binding.recyclerSongHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerSongHistory.setAdapter(recordAdapter);
        viewModel.getRecords(mSongID).observe(getViewLifecycleOwner(), recordDets -> recordAdapter.submitList(groupDataIntoDates(recordDets)));
        viewModel.getSongPlaytime(mSongID).observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                if (aLong != null) {
                    binding.playHistoryDuration.setText(getString(R.string.song_details_playtime, formatPlayTime(aLong)));
                } else {
                    binding.playHistoryDuration.setText(R.string.no_playtime);
                    binding.playHistoryRecyclerLabel.setVisibility(View.GONE);
                    binding.recyclerSongHistory.setVisibility(View.GONE);
                }
            }
        });
    }

    public static class RecordsAdapter extends ListAdapter<RecordItem, RecyclerView.ViewHolder> {
        private final Context mContext;

        public RecordsAdapter(@NonNull DiffUtil.ItemCallback<RecordItem> diffCallback, Context context) {
            super(diffCallback);
            mContext = context;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getType();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_play_details, parent, false);
            RecyclerView.ViewHolder viewHolder = null;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {

                case RecordItem.TYPE_DATE:
                    View v1 = inflater.inflate(R.layout.item_date_label, parent,
                            false);
                    viewHolder = new DateViewHolder(v1);
                    break;

                case RecordItem.TYPE_GENERAL:
                    View v2 = inflater.inflate(R.layout.song_play_details, parent, false);
                    viewHolder = new RecordViewHolder(v2);
                    break;
            }

            assert viewHolder != null;
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            switch (holder.getItemViewType()) {

                case RecordItem.TYPE_GENERAL:
                    RecordDets record = (RecordDets) getItem(position);
                    RecordViewHolder recordViewHolder = (RecordViewHolder) holder;
                    recordViewHolder.bindRecord(record, mContext);
                    break;

                case RecordItem.TYPE_DATE:
                    RecordDate dateItem = (RecordDate) getItem(position);
                    DateViewHolder dateViewHolder = (DateViewHolder) holder;
                    dateViewHolder.bindDate(dateItem);
                    break;
            }
        }

        public static class RecordDiff extends DiffUtil.ItemCallback<RecordItem> {
            
            @Override
            public boolean areItemsTheSame(@NonNull RecordItem oldItem, @NonNull RecordItem newItem) {
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull RecordItem oldItem, @NonNull RecordItem newItem) {
                return false;
            }
        }

        private static class RecordViewHolder extends RecyclerView.ViewHolder {
            TextView itemViewStart, itemViewDuration;
            ShapeableImageView itemAppIcon;

            public RecordViewHolder(@NonNull View itemView) {
                super(itemView);
                itemViewStart = itemView.findViewById(R.id.play_start_time);
                itemViewDuration = itemView.findViewById(R.id.play_duration);
                itemAppIcon = itemView.findViewById(R.id.icon_player_app);
            }

            public void bindRecord(RecordDets item, Context context) {
                itemViewStart.setText(context.getString(R.string.record_starttime, new SimpleDateFormat("H:mm a").format(item.getStartTime())));
                itemViewDuration.setText(context.getString(R.string.song_details_playtime, formatPlayTime(item.getDuration())));
                try {
                    itemAppIcon.setImageDrawable(context.getPackageManager().getApplicationIcon(item.getPackageName()));
                } catch (PackageManager.NameNotFoundException e) {
                    itemAppIcon.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }

        private static class DateViewHolder extends RecyclerView.ViewHolder {
            TextView itemViewDay;

            public DateViewHolder(View v1) {
                super(v1);
                itemViewDay = v1.findViewById(R.id.play_date);
            }

            public void bindDate(RecordDate date) {
                try {
                    Date day = new SimpleDateFormat("yyyy-MM-dd ").parse(date.getDate());
                    itemViewDay.setText(new SimpleDateFormat("MMM dd, yyyy").format(day));
                } catch (ParseException e) {
                    e.printStackTrace();
                    itemViewDay.setText(date.getDate());
                }
            }
        }

    }

    public static List<RecordItem> groupDataIntoDates(List<RecordDets> playRecordList) {

        HashMap<String, List<RecordDets>> groupedHashMap = new HashMap<>();

        for (RecordDets playRecord : playRecordList) {
            String date = new SimpleDateFormat("yyyy-MM-dd ").format(playRecord.getStartTime());

            if (groupedHashMap.containsKey(date)) {
                // The key is already in the HashMap; add the pojo object
                // against the existing key.
                groupedHashMap.get(date).add(playRecord);
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                List<RecordDets> list = new ArrayList<>();
                list.add(playRecord);
                groupedHashMap.put(date, list);
            }
        }

        // We linearly add every item into the consolidatedList.
        List<RecordItem> consolidatedList = new ArrayList<>();

        for(String date :groupedHashMap.keySet())
        {
            RecordDate dateItem = new RecordDate();
            dateItem.setDate(date);
            consolidatedList.add(dateItem);

            consolidatedList.addAll(groupedHashMap.get(date));
        }

        return consolidatedList;
    }

}