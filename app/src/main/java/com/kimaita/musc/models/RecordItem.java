package com.kimaita.musc.models;

public abstract class RecordItem {
    public static final int TYPE_DATE = 0;
    public static final int TYPE_GENERAL = 1;

    abstract public int getType();
}
