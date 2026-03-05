package edu.ustudio.expensetracker.core.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

public class ImageStorageHelper {

    public static String copyImageToAppStorage(Context context, Uri sourceUri) {

        try {

            File dir = new File(context.getFilesDir(), "expense_images");

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = UUID.randomUUID().toString() + ".jpg";

            File destFile = new File(dir, fileName);

            InputStream in = context.getContentResolver().openInputStream(sourceUri);
            FileOutputStream out = new FileOutputStream(destFile);

            byte[] buffer = new byte[4096];
            int len;

            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            in.close();
            out.close();

            return destFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}