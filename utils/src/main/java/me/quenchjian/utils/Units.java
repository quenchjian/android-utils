package me.quenchjian.utils;

import android.content.Context;
import android.util.ArrayMap;

import java.util.Map;

@SuppressWarnings("unused")
public final class Units {

  private static final Map<Integer, Integer> DP_PX_CACHE = new ArrayMap<>(10);
  private static final Map<Integer, Integer> SP_PX_CACHE = new ArrayMap<>(10);

  public static int dpToPx(Context context, int dp) {
    return DP_PX_CACHE.computeIfAbsent(dp, k -> (int) (dp * context.getResources().getDisplayMetrics().density));
  }

  public static int spToPx(Context context, int sp) {
    return SP_PX_CACHE.computeIfAbsent(sp, k -> (int) (sp * context.getResources().getDisplayMetrics().scaledDensity));
  }

  private Units() {
    throw new RuntimeException("prevent reflection");
  }
}
