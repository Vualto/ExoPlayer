package com.google.android.exoplayer2.demo.vudrm;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.MediaDrmCallback;
import com.vualto.vudrm.HttpKidSource;
import com.vualto.vudrm.widevine.AssetConfiguration;
import com.vualto.vudrm.widevine.WidevineCallback;
import java.net.URL;

public class VudrmHelper {
  public static final String TOKEN = "vualto-demo|2020-11-24T10:22:18Z|Ej3GRU8dDJmZ8+ni1rcV4Hoo3rjQh7IJUldVUT+TY4U=|a9ab289cd51ffd8145cf40a4800d690a9bab34bf";

  // Builds and returns a DrmSessionManager with the VUDRM callback
  public static DrmSessionManager getVudrmSessionManager(String streamUri, String token)
      throws Exception {
    AssetConfiguration assetConfiguration = new AssetConfiguration.Builder()
        .tokenWith(token)
        .kidProviderWith(
            new HttpKidSource(new URL(streamUri))
        ).build();
    MediaDrmCallback mediaDrmCallback = new WidevineCallback(assetConfiguration);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

      return new DefaultDrmSessionManager.Builder()
          .setUuidAndExoMediaDrmProvider(C.WIDEVINE_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER)
          .setMultiSession(/* check individual assets for that */ false)
          .build(mediaDrmCallback);

    }
    throw new Exception("unsupported android build version");
  }

  // Checks for vudrm.tech or drm.technology presence in the license URL
  public static Boolean useSdk(Context context, Uri uri) {
    return !uri.toString().contains("token=") && isVudrm(uri) && isNetworkAvailable(context);
  }

  // Checks for vudrm.tech or drm.technology presence in the license URL
  private static Boolean isVudrm(Uri uri) {
    String dns = uri.getAuthority();
    assert dns != null;
    return dns.contains("vudrm.tech") || dns.contains("drm.technology");
  }

  private static boolean isNetworkAvailable(Context context) {
    ConnectivityManager connectivityManager
        = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

}
