package com.nephren.raven.apiclient.http;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Internal helpers for deriving stable pool keys from configured URLs.
 *
 * <p>The key shape is intentionally narrow — scheme + host + (resolved) port — so that two
 * clients hitting the same logical backend share a pool regardless of differences in path,
 * query, or trailing slashes. Inputs without an explicit scheme (e.g. the
 * {@code localhost:8080} format used in our setup docs) are normalized as if they were
 * {@code http://...} so they collapse onto the same key as their scheme-prefixed siblings.
 * Anything still unparseable falls back to a {@code raw:} prefix to preserve isolation
 * without throwing.</p>
 */
final class PoolKeys {

  private PoolKeys() {}

  static String fromUrl(String url) {
    if (url == null || url.isBlank()) {
      return "default";
    }
    try {
      URI uri = new URI(url);
      if (uri.getHost() == null && !url.contains("://")) {
        // Inputs like "localhost:8080" or "localhost:8080/api" parse as opaque URIs with no
        // host; re-parse with an http:// prefix so they normalize to the same scheme/host/port
        // shape as fully-qualified URLs.
        uri = new URI("http://" + url);
      }
      String scheme = uri.getScheme() == null ? "http" : uri.getScheme().toLowerCase();
      String host = uri.getHost();
      if (host == null) {
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
