package com.kimaita.musc.ui.trends;

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

import com.kimaita.musc.R;

public class TrendsFragment extends Fragment {

    private TrendsViewModel trendsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trendsViewModel =
                new ViewModelProvider(this).get(TrendsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_trends, container, false);

        return root;
    }
}