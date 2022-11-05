package com.kimaita.musc;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.kimaita.musc.models.Artist;
import com.kimaita.musc.models.PlayRecord;
import com.kimaita.musc.models.PlaySession;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationService extends NotificationListenerService {

    private static final String TAG = "Notif_ListenerService";
    private static final String TAG_POSTED = "Notification Posted";
    private static final String TAG_REMOVED = "Notification Removed";
    private static final String TAG_CONNECTED = "Listener Connected";
    private static final String mediaTemplate = "android.app.Notification$MediaStyle";
    private static final String VLC = "org.videolan.vlc";
    private static final String SPOTIFY = "com.spotify.music";
    final ArrayList<PlayRecord> plays = new ArrayList<>();
    final ArrayList<PlaySession> sessions = new ArrayList<>();
    final ArrayList<Artist> artists = new ArrayList<>();
    PlayRecord record = new PlayRecord();
    PlaySession session = new PlaySession();
    Artist artist = new Artist();
    Context context;
    Long pauseTime;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    @Override
    public void onListenerConnected() {
        Log.i(TAG, "Notification Listener connected");
        StatusBarNotification[] sbNotification = getActiveNotifications();

        for (StatusBarNotification statusBarNotification : sbNotification) {
            Bundle extras = statusBarNotification.getNotification().extras;
            if (!isValidSBN(extras))
                continue;
            Notification.Action[] actions = statusBarNotification.getNotification().actions;
            session = new PlaySession();
            artist = new Artist();
            String packageName = statusBarNotification.getPackageName();
            long postTimeFound = statusBarNotification.getPostTime();
            String title = extras.get(Notification.EXTRA_TITLE).toString();
            String text = extras.get(Notification.EXTRA_TEXT).toString();
            String mediaSession = getToken(extras.get(Notification.EXTRA_MEDIA_SESSION).toString());

            session.setPlayer(packageName);
            session.setSessionID(mediaSession);
            session.setStartTime(postTimeFound);
            if (sessions.size() == 0) {
                sessions.add(session);
                Log.i(TAG_CONNECTED, "No Sessions. Added: " + session.getSessionID());
            } else if (!sessions.contains(session)) {
                previousSession(sessions).setEndTime(postTimeFound);
                previousSession(sessions).setSessionLength(previousSession(sessions).getEndTime() - previousSession(sessions).getStartTime());
                sessions.add(session);
                Log.i(TAG_CONNECTED, "Added Session: " + session.getSessionID() + " Size now: " + sessions.size());
            }
            if (isVLC(packageName)) {
                text = extractArtistVLC(text);
            }

            if (plays.size() > 0) {
                if (!sameSong(title, text)) {
                    record = new PlayRecord();
                    record.setSongTitle(title);
                    record.setMediaSession(mediaSession);
                    record.setStartTime(postTimeFound);
                    record.setSongArtist(text);
                    if (isSpotify(packageName)) {

                    }
                    if (previousRecord(plays).getEndTime() == null) {
                        previousRecord(plays).setEndTime(postTimeFound);
                        if (pauseTime != null) {
                            previousRecord(plays).setDuration((postTimeFound - previousRecord(plays).getStartTime()) - pauseTime);
                        } else {
                            previousRecord(plays).setDuration(postTimeFound - previousRecord(plays).getStartTime());
                        }
                    } else {
                        pauseTime = postTimeFound - previousRecord(plays).getEndTime();
                        Log.i(TAG_CONNECTED, "Paused for: " + pauseTime / 1000 + "s");
                        previousRecord(plays).setDuration((postTimeFound - previousRecord(plays).getStartTime()) - pauseTime);
                    }
                    pauseTime = null;
                    Log.i(TAG_CONNECTED, previousRecord(plays).getSongTitle() + " played for: " + previousRecord(plays).getDuration() / 1000 + "s");

                    plays.add(record);
                    Log.i(TAG_CONNECTED, "Added: " + record.getSongTitle() + " by " + record.getSongArtist() + " now: " + plays.size());
                } else {

                    if (record.getSongArtist().equals(" ")) {
                        record.setSongArtist(text);
                    }
                    if (!isPlaying(actions)) {
                        if (record.getEndTime() == null) {
                            record.setEndTime(postTimeFound);
                            Log.i(TAG_CONNECTED, record.getSongTitle() + " Paused at: " + record.getEndTime());
                        }
                        if (record.getDuration() == null) {
                            record.setDuration(record.getEndTime() - record.getStartTime());
                        }
                    } else if (record.getEndTime() != null) {
                        if (pauseTime == null) {
                            pauseTime = postTimeFound - record.getEndTime();
                        } else {
                            pauseTime += postTimeFound - record.getEndTime();
                        }
                        record.setEndTime(null);
                        Log.i(TAG_CONNECTED, record.getSongTitle() + " Resumed at: " + postTimeFound + ", Paused for: " + pauseTime / 1000 + "s");
                    }
                }
            } else {
                record = new PlayRecord();
                record.setSongTitle(title);
                record.setMediaSession(mediaSession);
                record.setStartTime(postTimeFound);
                record.setSongArtist(text);
                if (isSpotify(packageName)) {

                }
                plays.add(record);
                Log.i(TAG_CONNECTED, "Added: " + record.getSongTitle() + " by " + record.getSongArtist() + " now: " + plays.size());
            }
            if (artists.size() == 0) {
                if (isVLC(packageName)) {
                    text = extractArtistVLC(text);
                }
                if (text.split(", ").length > 1) {
                    for (String individualArtist : text.split(", ")) {
                        artist = new Artist();
                        artist.setArtistName(individualArtist);
                        artists.add(artist);
                    }
                } else {
                    artist = new Artist();
                    artist.setArtistName(text);
                    artists.add(artist);
                }
            } else {
                if (isVLC(packageName)) {
                    text = extractArtistVLC(text);
                }
                if (text.split(", ").length > 1) {
                    for (String individualArtist : text.split(", ")) {
                        artist = new Artist();
                        artist.setArtistName(individualArtist);
                        if (!artists.contains(artist)) artists.add(artist);
                    }
                } else {
                    artist = new Artist();
                    artist.setArtistName(text);
                    if (!artists.contains(artist)) artists.add(artist);
                }
            }
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Bundle extras = sbn.getNotification().extras;
        if (!isValidSBN(extras))
            return;

        String packageName = sbn.getPackageName();
        long postTime = sbn.getPostTime();
        String title = extras.get(Notification.EXTRA_TITLE).toString();
        String text = extras.get(Notification.EXTRA_TEXT).toString();
        Notification.Action[] actions = sbn.getNotification().actions;
        String mediaSession = getToken(extras.get(Notification.EXTRA_MEDIA_SESSION).toString());

        session = new PlaySession();
        session.setPlayer(packageName);
        session.setSessionID(mediaSession);
        session.setStartTime(postTime);

        if (isVLC(packageName)) {
            text = extractArtistVLC(text);
        }
        if (plays.size() > 0) {
            if (!sameSong(title, text)) {
                record = new PlayRecord();
                record.setSongTitle(title);
                record.setMediaSession(mediaSession);
                record.setStartTime(postTime);
                record.setSongArtist(text);
                if (isSpotify(packageName)) {

                }
                if (previousRecord(plays).getEndTime() == null) {
                    previousRecord(plays).setEndTime(postTime);
                    if (pauseTime != null) {
                        previousRecord(plays).setDuration((postTime - previousRecord(plays).getStartTime()) - pauseTime);
                    } else {
                        previousRecord(plays).setDuration(postTime - previousRecord(plays).getStartTime());
                    }
                } else {
                    pauseTime = postTime - previousRecord(plays).getEndTime();
                    Log.i("Notification Posted", "Paused for: " + pauseTime / 1000 + "s");
                    previousRecord(plays).setDuration((postTime - previousRecord(plays).getStartTime()) - pauseTime);
                }
                pauseTime = null;
                Log.i("Notification Posted", previousRecord(plays).getSongTitle() + " played for: " + previousRecord(plays).getDuration() / 1000 + "s");
                plays.add(record);
                Log.i("Notification Posted", "Added: " + record.getSongTitle() + " by " + record.getSongArtist() + " now: " + plays.size());
            } else {
                if (!isPlaying(actions)) {
                    if (record.getEndTime() == null) {
                        record.setEndTime(postTime);
                        Log.i(TAG_POSTED, record.getSongTitle() + " Paused at: " + record.getEndTime());
                    }
                    if (record.getDuration() == null) {
                        record.setDuration(record.getEndTime() - record.getStartTime());
                    }
                } else if (record.getEndTime() != null) {
                    if (pauseTime == null) {
                        pauseTime = postTime - record.getEndTime();
                    } else {
                        pauseTime += postTime - record.getEndTime();
                    }
                    record.setEndTime(null);
                    Log.i(TAG_POSTED, record.getSongTitle() + " Resumed at: " + postTime + ", Paused for: " + pauseTime / 1000 + "s");
                }
            }
        } else {
            record = new PlayRecord();
            record.setSongTitle(title);
            record.setMediaSession(mediaSession);
            record.setStartTime(postTime);
            record.setSongArtist(text);
            if (isSpotify(packageName)) {
                //Log.i("Spotify", new SpotifyBroadcastReceiver().getSpotifyTrackID());
            }
            plays.add(record);
            Log.i("Notification Posted", "No Records. Added: " + record.getSongTitle() + " by " + record.getSongArtist() + " now: " + plays.size());
        }

        if (sessions.size() > 0) {
            if (!sessions.contains(session)) {
                sessions.add(session);
                previousSession(sessions).setEndTime(postTime);
                previousSession(sessions).setSessionLength(previousSession(sessions).getEndTime() - previousSession(sessions).getStartTime());
                Log.i("Notification Posted", "Added Session: " + session.getSessionID() + " Size now: " + sessions.size());
            }
        } else {
            sessions.add(session);
            Log.i("Notification Posted", "No Sessions. Added: " + session.getSessionID());
        }

        if (artists.size() == 0) {
            if (isVLC(packageName)) {
                text = extractArtistVLC(text);
            }
            if (text.split(", ").length > 1) {
                for (String individualArtist : text.split(", ")) {
                    artist = new Artist();
                    artist.setArtistName(individualArtist);
                    artists.add(artist);
                }
            } else {
                artist = new Artist();
                artist.setArtistName(text);
                artists.add(artist);
            }
        } else {
            if (isVLC(packageName)) {
                text = extractArtistVLC(text);
            }
            if (text.split(", ").length > 1) {
                for (String individualArtist : text.split(", ")) {
                    artist = new Artist();
                    artist.setArtistName(individualArtist);
                    if (!artists.contains(artist)) artists.add(artist);
                }
            } else {
                artist = new Artist();
                artist.setArtistName(text);
                if (!artists.contains(artist)) artists.add(artist);
            }
        }
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Bundle extras = sbn.getNotification().extras;
        if (!isValidSBN(extras))
            return;
        long stopTime = System.currentTimeMillis();
        if (plays.size() > 0) {
            if (previousRecord(plays).getEndTime() == null) {
                previousRecord(plays).setEndTime(stopTime);
                previousRecord(plays).setDuration(stopTime - previousRecord(plays).getStartTime());
            }
            Log.i(TAG_REMOVED, "Last song played for:" + previousRecord(plays).getDuration() / 1000 + "s");
            writeToFile(plays, "playRecords");
            plays.clear();
        }
        if (sessions.size() > 0) {
            previousSession(sessions).setEndTime(stopTime);
            previousSession(sessions).setSessionLength(previousSession(sessions).getEndTime() - previousSession(sessions).getStartTime());
            Log.i(TAG_REMOVED, previousSession(sessions).getPlayer() + " playback dismissed at: " + stopTime);
            writeToFile(sessions, "playSessions");
            sessions.clear();
        }
        if (artists.size() > 0) {
            writeToFile(artists, "artists");
            artists.clear();
        }


    }

    private boolean isValidSBN(Bundle extras) {
        if (extras.get(Notification.EXTRA_TEMPLATE) == null) {
            return false;
        } else return extras.get(Notification.EXTRA_TEMPLATE).equals(mediaTemplate);
    }

    private boolean sameSong(String title, String artist) {
        return (previousRecord(plays).getSongTitle().equals(title) && previousRecord(plays).getSongArtist().equals(artist));
    }

    private String getToken(String str) {
        // Get the regex to be checked
        String regex = "@";
        String token = "";
        int len = str.length();
        // Create a pattern from regex
        Pattern pattern = Pattern.compile(regex);
        // Create a matcher for the input String
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            token = str.substring((matcher.start() + 1), len);
        }
        return token;
    }

    private void writeToFile(ArrayList arrayList, String fileName) {
        File file = new File(context.getFilesDir(), fileName);

        try {
            FileOutputStream fs = new FileOutputStream(file, true);
            ObjectOutputStream ofs = new ObjectOutputStream(fs);
            ofs.writeObject(arrayList);
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private PlaySession previousSession(ArrayList<PlaySession> arrayList) {
        return arrayList.get(arrayList.size() - 1);
    }

    private PlayRecord previousRecord(ArrayList<PlayRecord> arrayList) {
        return arrayList.get(arrayList.size() - 1);
    }

    private boolean isVLC(String packageName) {
        return packageName.equals(VLC);
    }

    private boolean isSpotify(String packageName) {
        return packageName.equals(SPOTIFY);
    }

    private String extractArtistVLC(String str) {
        String regex = " - ";
        String artist = " ";
        int len = str.length();
        // Create a pattern from regex
        Pattern pattern = Pattern.compile(regex);
        // Create a matcher for the input String
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            artist = str.substring(0, matcher.start());
        }
        return artist;
    }

    private boolean isPlaying(Notification.Action[] actions) {
        boolean isPlaying = false;
        for (Notification.Action action : actions) {
            if (action.title.equals("Play")) {
                isPlaying = false;

            } else if (action.title.equals("Pause")) {
                isPlaying = true;
            }
        }
        return isPlaying;
    }
}
