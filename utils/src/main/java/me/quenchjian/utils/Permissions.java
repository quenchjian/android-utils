package me.quenchjian.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public final class Permissions {

  private static final String TAG = "Permissions";
  private static final int REQ_CODE = Build.VERSION_CODES.M;
  private static final List<String> EMPTY = Collections.emptyList();

  public static void request(Fragment fragment, Callback callback, String... perms) {
    Objects.requireNonNull(fragment);
    Objects.requireNonNull(callback);
    request(fragment.requireActivity(), callback, perms);
  }

  public static void request(Activity activity, Callback callback, String... perms) {
    Objects.requireNonNull(activity);
    Objects.requireNonNull(callback);
    if (check(activity, perms)) {
      callback.onPermissionResult(Arrays.asList(perms), EMPTY, EMPTY);
      return;
    }
    if (activity instanceof ComponentActivity) {
      ((ComponentActivity) activity)
          .registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            RequestResult r = parseResult(activity, result);
            callback.onPermissionResult(r.granted, r.denied, r.permanentDenied);
          })
          .launch(perms);
    } else {
      Log.w(Permissions.TAG, "Must call Permissions.onResult to handle result in Activity.onPermissionResult method");
      ActivityCompat.requestPermissions(activity, perms, REQ_CODE);
    }
  }

  public static void onResult(Activity activity, Callback callback, String[] perms, int[] grantResults) {
    Objects.requireNonNull(activity);
    Objects.requireNonNull(callback);
    Objects.requireNonNull(perms);
    Objects.requireNonNull(grantResults);
    Map<String, Boolean> result = new HashMap<>();
    for (int i = 0; i < perms.length; i++) {
      result.put(perms[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
    }
    RequestResult r = parseResult(activity, result);
    callback.onPermissionResult(r.granted, r.denied, r.permanentDenied);
  }

  private static boolean check(Context context, String... perms) {
    Objects.requireNonNull(context);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return true;
    }
    for (String perm : perms) {
      if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  @NonNull
  private static RequestResult parseResult(Activity activity, Map<String, Boolean> result) {
    Objects.requireNonNull(activity);
    Objects.requireNonNull(result);
    RequestResult r = new RequestResult();
    for (String perm : result.keySet()) {
      if (result.get(perm) == Boolean.TRUE) {
        r.granted.add(perm);
      } else {
        r.denied.add(perm);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
          r.permanentDenied.add(perm);
        }
      }
    }
    return r;
  }

  private Permissions() {
    throw new RuntimeException("prevent reflection");
  }

  public interface Callback {

    void onPermissionResult(List<String> granted, List<String> denied, List<String> permanentDenied);
  }

  private static class RequestResult {

    final List<String> granted = new ArrayList<>();
    final List<String> denied = new ArrayList<>();
    final List<String> permanentDenied = new ArrayList<>();
  }
}
