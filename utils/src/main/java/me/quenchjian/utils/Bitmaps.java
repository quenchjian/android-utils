package me.quenchjian.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public final class Bitmaps {

  public static final Bitmap EMPTY = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);

  @NonNull
  public static Bitmap fromDrawable(Drawable drawable) {
    if (drawable == null) {
      return EMPTY;
    }
    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    }
    int width = Math.max(drawable.getIntrinsicWidth(), 1);
    int height = Math.max(drawable.getIntrinsicHeight(), 1);
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);
    return bitmap;
  }

  private Bitmaps() {
    throw new RuntimeException("prevent reflection");
  }
}
