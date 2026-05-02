package com.nephren.raven.apiclient.http;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Internal helpers for deriving stable pool keys from configured URLs.
 *
 * <p>The key shape is intentionally narrow — scheme + host + (resolved) port — so that two
 * clients hitting the same logical backend share a pool regardless of differences in path,
 * query, or trailing slashes. Anything we cannot parse falls back to the raw input string,
 * which preserves backwards-compatible isolation for malformed URLs without throwing.</p>
 */
final class PoolKeys {

  private PoolKeys() {}

  static String fromUrl(String url) {
    if (url == null || url.isBlank()) {
      return "default";
    }
    try {
      URI uri = new URI(url);
      String scheme = uri.getScheme() == null ? "http" : uri.getScheme().toLowerCase();
      String host = uri.getHost();
      if (host == null) {
        // not an absolute URL (e.g. just "localhost"); use the raw value as the key
        return "raw:" + url;
      }
      int port = uri.getPort();
      if (port == -1) {
        port = "https".equals(scheme) ? 443 : 80;
      }
      return scheme + "://" + host + ":" + port;
    } catch (URISyntaxException e) {
      return "raw:" + url;
    }
  }
}
