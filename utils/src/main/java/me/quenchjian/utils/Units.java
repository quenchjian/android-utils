package me.quenchjian.utils;

import android.content.Context;
import android.util.ArrayMap;

import java.util.Map;

public final class Units {

  private static final Map<Integer, Integer> DP_PX_CACHE = new ArrayMap<>(10);
  private static final Map<Integer, Integer> SP_PX_CACHE = new ArrayMap<>(10);

  public static int dpToPx(Context context, int dp) {
    Integer px = DP_PX_CACHE.get(dp);
    if (px == null) {
      px = (int) (dp * context.getResources().getDisplayMetrics().density);
      DP_PX_CACHE.put(dp, px);
    }
    return px;
  }

  public static int spToPx(Context context, int sp) {
    Integer px = SP_PX_CACHE.get(sp);
    if (px == null) {
      px = (int) (sp * context.getResources().getDisplayMetrics().density);
      SP_PX_CACHE.put(sp, px);
    }
    return px;
  }

  private Units() {
    throw new RuntimeException("prevent reflection");
  }
}
