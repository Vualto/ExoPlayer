package com.google.android.exoplayer2.demo.vudrm;

import android.net.Uri;
import android.os.Build;
import androidx.annotation.Nullable;
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
  public static final String TOKEN = "";
  public static final Boolean USE_SDK = false;
  public static final String DEFAULT_VUDRM_WIDEVINE_LICENSE_URL = "https://widevine-license.vudrm.tech/proxy";
  public static final String VUDRM_TECH = "vudrm.tech";
  public static final String DRM_TECHNOLOGY = "drm.technology";

  // Returns a Widevine VUDRM license URI that includes the token in a query string param
  public static Uri buildVudrmLicenseUri(@Nullable Uri uri) throws Exception {
    if (uri == null) {
      uri = Uri.parse(DEFAULT_VUDRM_WIDEVINE_LICENSE_URL);
    }

    String tokenParam =  "token=" + java.net.URLEncoder.encode(TOKEN, "utf-8");

    // from java uri to android uri
    return Uri.parse(
        new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(),tokenParam, uri.getFragment())
            .toString());
  }

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
  public static Boolean isVudrm(Uri uri) {
    String dns = uri.getAuthority();
    assert dns != null;
    return dns.contains(VUDRM_TECH) || dns.contains(DRM_TECHNOLOGY);
  }
}
