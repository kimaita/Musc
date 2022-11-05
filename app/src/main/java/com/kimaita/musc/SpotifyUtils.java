package com.kimaita.musc;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.RetrofitError;
import spotify.api.authorization.ClientCredentialsFlow;
import spotify.models.authorization.ClientCredentialsFlowTokenResponse;

public class SpotifyUtils {

    private static final String TAG = "SpotifyUtils";
    private final Context mContext;
    private final SpotifyService spotifyService;

    public SpotifyUtils(Context context) {
        mContext = context;
        SpotifyApi mApi = initSpotifyApi();
        spotifyService = mApi.getService();
    }

    private SpotifyApi initSpotifyApi() {
        SpotifyApi spotifyApi = new SpotifyApi();
        if (SpotifyCredentialsHandler.getToken(mContext) != null) {
            spotifyApi.setAccessToken(SpotifyCredentialsHandler.getToken(mContext));
        } else {
            String token = getAccessToken();
            spotifyApi.setAccessToken(token);
        }
        return spotifyApi;
    }


    public String getAccessToken() {
        final String CLIENT_ID = "50556a6bf67e4377a454964fd6391f91";
        final String CLIENT_SECRET = "281527eeceb946ff9b6410628d9b863b";
        String token = "";
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<String> result = es.submit(new Callable<String>() {
            @Override
            public String call() {
                String accessToken = "";
                try {
                    ClientCredentialsFlow clientCredentialsFlow = new ClientCredentialsFlow();
                    ClientCredentialsFlowTokenResponse response = clientCredentialsFlow.getClientCredentialToken(
                            CLIENT_ID,
                            CLIENT_SECRET);
                    accessToken = response.getAccessToken();
                    long expiresIn = response.getExpiresIn();
                    SpotifyCredentialsHandler.setToken(mContext, accessToken, expiresIn, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return accessToken;
            }
        });
        try {
            token = result.get();
            Log.i("GetAccessToken", "Fetched Token:" + token);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        es.shutdown();
        return token;
    }

    public Track searchTrack(String query) {
        Track searchedTrack = new Track();
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<TracksPager> result = es.submit(() -> {
            TracksPager items = new TracksPager();
            try {
                items = spotifyService.searchTracks(query);
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                error.printStackTrace();
                Log.e(TAG, spotifyError.getLocalizedMessage());
            }
            return items;
        });
        try {
            searchedTrack = result.get().tracks.items.get(0);
            Log.i(TAG, "Search result: " + searchedTrack.name + " uri: " + searchedTrack.uri);
        } catch (Exception e) {
            // failed
            e.printStackTrace();
        }
        es.shutdown();

        return searchedTrack;
    }

    public Track getTrack(String id) {
        Track track = new Track();
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Track> result = es.submit(new Callable<Track>() {
            public Track call() throws Exception {
                Track item = new Track();
                try {
                    item = spotifyService.getTrack(id);
                } catch (RetrofitError error) {
                    SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                    error.printStackTrace();
                    Log.e(TAG, spotifyError.getLocalizedMessage());
                }
                return item;
            }
        });
        try {
            track = result.get();
            Log.i(TAG, "Search result: " + track.name);
        } catch (Exception e) {
            // failed
            e.printStackTrace();
        }
        es.shutdown();

        return track;

    }

    public AudioFeaturesTrack getTrackAudioFeatures(String id) {
        AudioFeaturesTrack trackFeatures = new AudioFeaturesTrack();
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<AudioFeaturesTrack> result = es.submit(() -> {
            AudioFeaturesTrack item = new AudioFeaturesTrack();
            try {
                item = spotifyService.getTrackAudioFeatures(id);
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                error.printStackTrace();
                Log.e(TAG, spotifyError.getLocalizedMessage());
            }
            return item;
        });
        try {
            trackFeatures = result.get();
            Log.i(TAG, "Search result: " + trackFeatures.analysis_url + " uri " + trackFeatures.uri);
        } catch (Exception e) {
            // failed
            e.printStackTrace();
        }
        es.shutdown();

        return trackFeatures;
    }


}
