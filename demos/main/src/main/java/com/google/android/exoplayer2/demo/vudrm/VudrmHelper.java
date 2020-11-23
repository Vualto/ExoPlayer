package com.google.android.exoplayer2.demo.vudrm;

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
import java.net.URI;
import java.net.URL;

public class VudrmHelper {
  public static final String TOKEN = "vualto-demo|2020-11-23T21:24:49Z|Ej3GRU8dDJmZ8+ni1rcV4Hoo3rjQh7IJUldVUT+TY4U=|8022f7990c3a29dd2d2780d947ea7babd6738964";

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
  public static Boolean useSdk(Uri uri) {
    String q = uri.getQuery();

    if (q == null) {
      return true;
    }

    return !q.contains("token=") && isVudrm(uri);
  }

  // Checks for vudrm.tech or drm.technology presence in the license URL
  private static Boolean isVudrm(Uri uri) {
    String dns = uri.getAuthority();
    assert dns != null;
    return dns.contains("vudrm.tech") || dns.contains("drm.technology");
  }
}
