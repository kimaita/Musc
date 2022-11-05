package com.kimaita.musc.models;

public class RecordDets extends RecordItem {

    long startTime;
    long duration;
    String packageName;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public int getType() {
        return TYPE_GENERAL;
    }
}
