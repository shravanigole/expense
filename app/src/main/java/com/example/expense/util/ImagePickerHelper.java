package com.example.expense.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

public class ImagePickerHelper {
    private final Fragment fragment;
    private final ImageSelectedCallback callback;
    private final ActivityResultLauncher<String> imagePicker;

    public interface ImageSelectedCallback {
        void onImageSelected(Uri imageUri);
    }

    public ImagePickerHelper(Fragment fragment, ImageSelectedCallback callback) {
        this.fragment = fragment;
        this.callback = callback;
        this.imagePicker = fragment.registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    callback.onImageSelected(uri);
                }
            }
        );
    }

    public void pickImage() {
        imagePicker.launch("image/*");
    }

    public static Intent createChooser(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        return Intent.createChooser(intent, "Select Image");
    }
}