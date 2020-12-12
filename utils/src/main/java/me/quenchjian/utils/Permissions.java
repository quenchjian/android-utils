package me.quenchjian.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

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
import java.util.List;

public final class Permissions {

  private static final String TAG = "Permissions";
  private static final int REQ_CODE = Build.VERSION_CODES.M;
  private static final List<String> EMPTY = Collections.emptyList();

  public static void request(Fragment fragment, Callback callback, String... perms) {
    request(fragment.requireActivity(), callback, perms);
  }

  public static void request(Activity activity, Callback callback, String... perms) {
    if (Build.VERSION.SDK_INT < REQ_CODE || check(activity, perms)) {
      callback.onPermissionResult(Arrays.asList(perms), EMPTY, EMPTY);
      return;
    }
    if (activity instanceof FragmentActivity) {
      FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
      PermissionFragment frag = (PermissionFragment) fm.findFragmentByTag("PermissionFragment");
      if (frag == null) {
        frag = new PermissionFragment();
        fm.beginTransaction().add(frag, "PermissionFragment").commitNow();
      }
      frag.request(callback, perms);
    } else {
      Log.w(Permissions.TAG,
          "Must call Permissions.onResult to handle result in Activity.onPermissionResult method");
      ActivityCompat.requestPermissions(activity, perms, REQ_CODE);
    }
  }

  public static void onResult(Activity activity, Callback callback, String[] perms) {
    List<String> granted = new ArrayList<>();
    List<String> denied = new ArrayList<>();
    List<String> permanentDenied = new ArrayList<>();
    for (String perm : perms) {
      if (ContextCompat.checkSelfPermission(activity, perm) == PackageManager.PERMISSION_GRANTED) {
        granted.add(perm);
      } else {
        denied.add(perm);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
          permanentDenied.add(perm);
        }
      }
    }
    callback.onPermissionResult(granted, denied, permanentDenied);
  }

  private static boolean check(Context context, String... perms) {
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

  private Permissions() {
    throw new RuntimeException("prevent reflection");
  }

  public interface Callback {

    void onPermissionResult(List<String> granted, List<String> denied,
        List<String> permanentDenied);
  }

  public static class PermissionFragment extends Fragment {

    private Callback callback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRetainInstance(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
      Permissions.onResult(requireActivity(), callback, permissions);
    }

    private void request(Callback callback, String... perms) {
      this.callback = callback;
      ActivityCompat.requestPermissions(requireActivity(), perms, REQ_CODE);
    }
  }
}
