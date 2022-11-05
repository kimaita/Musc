package com.kimaita.musc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

public class Converters {

    @TypeConverter
    public static Bitmap toBitmap(byte[] byteArray) {
        return byteArray == null ? null : BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    @TypeConverter
    public static byte[] fromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bitmap.recycle();
        } else {
            return null;
        }
        return stream.toByteArray();
    }
    @TypeConverter
    public static Uri toUri(String uriString){
        return uriString == null ? null: Uri.parse(uriString);
    }

    @TypeConverter
    public static String toString(Uri uri){
        return uri == null ? null : uri.toString();
    }

}
