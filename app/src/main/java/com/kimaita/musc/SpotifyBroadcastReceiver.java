package com.kimaita.musc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SpotifyBroadcastReceiver extends BroadcastReceiver {

    static final class BroadcastTypes {
        static final String SPOTIFY_PACKAGE = "com.spotify.music";
        static final String METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(BroadcastTypes.METADATA_CHANGED)) {


        }
    }


}
