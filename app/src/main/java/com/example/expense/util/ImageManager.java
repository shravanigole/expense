package com.example.expense.util;
import android.graphics.ImageDecoder;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.graphics.BitmapFactory;
import java.io.InputStream;

public class ImageManager {
    private static final String IMAGE_DIRECTORY = "trip_images";
    private final Context context;
    private final File imageDir;

    public ImageManager(Context context) {
        this.context = context;
        this.imageDir = new File(context.getFilesDir(), IMAGE_DIRECTORY);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
    }

    public String saveImage(Uri imageUri) throws IOException {
        Bitmap bitmap;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), imageUri);
            bitmap = ImageDecoder.decodeBitmap(source, (decoder, info, src) -> {
                decoder.setMutableRequired(true);
                decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
            });
        } else {
            // For older versions, use ContentResolver with InputStream
            try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri)) {
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        }

        String fileName = generateFileName();
        File file = new File(imageDir, fileName);

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
        }

        return file.getAbsolutePath();
    }

    public void deleteImage(String imagePath) {
        if (imagePath != null) {
            File file = new File(imagePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return "TRIP_" + timeStamp + ".jpg";
    }

    public File getImageFile(String fileName) {
        return new File(imageDir, fileName);
    }
}