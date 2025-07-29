package com.example.expense.util;

import android.widget.ImageView;
import androidx.databinding.BindingAdapter;
import com.bumptech.glide.Glide;
import com.example.expense.R;

public class BindingAdapters {
    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(view);
        }
    }
}
