package ru.geekbrains.android3_5.model.image.android;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CommonUtils {

    static void saveBitmapToDevice(Bitmap imgBitmap, String imgPath) {
        ByteArrayOutputStream imgStream = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, imgStream);

        try {
            FileOutputStream outputStream = new FileOutputStream(imgPath);
            outputStream.write(imgStream.toByteArray());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
