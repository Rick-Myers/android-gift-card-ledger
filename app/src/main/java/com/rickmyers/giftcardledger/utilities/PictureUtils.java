package com.rickmyers.giftcardledger.utilities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;

import java.io.IOException;

public class PictureUtils {

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap scaledBitmap = BitmapFactory.decodeFile(path, options);

        return getOrientatedBitmap(path, scaledBitmap);
    }

    private static Bitmap getOrientatedBitmap(String path, Bitmap scaledBitmap) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientationString = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientationTag = orientationString != null ? Integer.parseInt(orientationString) :
                ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;


        switch (orientationTag) {
            case ExifInterface.ORIENTATION_ROTATE_90 :
                rotationAngle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180 :
                rotationAngle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270 :
                rotationAngle = 270;
                break;
            default:
                rotationAngle = 0;

        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                scaledBitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }
}
