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
import java.util.HashMap;

public class VudrmHelper {
  // Static token for testing purposes only
  public static final String TOKEN = "vualto-demo|2021-01-08T14:45:39Z|Qxm2il1qufWoGMSb/JR7BGrI/Ssm2tVnCaho1GYD9tQ=|24d1dfd5ad968b58ed36fc688ecb1af2e88c6720";

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

  // Returns a VUDRM token valid for the given asset
  public static String getTokenFor(String assetId) {
    // Dummy method, assets should have individual tokens
    return TOKEN;
  }

  // Checks for vudrm.tech or drm.technology presence in the license URL and internet connection
  public static Boolean useSdk(Context context, Uri uri) {
    return isVudrm(uri) &&
        // Let exoPlayer demo handle loading downloaded licenses when offline
        isNetworkAvailable(context);
  }

  // Checks for vudrm.tech or drm.technology presence in the license URL
  private static Boolean isVudrm(Uri uri) {
    return false ;
//    String dns = uri.getAuthority();
//    assert dns != null;
//    return dns.contains("vudrm.tech") || dns.contains("drm.technology") ;
  }

  private static boolean isNetworkAvailable(Context context) {
    ConnectivityManager connectivityManager
        = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

}
