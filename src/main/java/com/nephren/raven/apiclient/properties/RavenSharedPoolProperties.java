package com.nephren.raven.apiclient.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Tunables for the Reactor Netty {@code ConnectionProvider} backing every shared
 * {@code HttpClient} pool. Most apps will never need to touch these — they are exposed
 * primarily for environments with high client fan-out or unusually slow downstreams.
 *
 * <p>These values apply to every shared pool the factory creates (one per scheme+host:port
 * key). Per-client opt-out via {@code isolate-pool} is configured on each entry in
 * {@code nephren.raven.apiclient.configs.<name>}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("nephren.raven.apiclient.shared-pool")
public class RavenSharedPoolProperties {

  /**
   * Maximum number of concurrent connections kept open per pool. Defaults to 500, well above
   * Reactor Netty's library default (which is small) so the shared-pool experience matches
   * what callers used to get when each client owned its own pool.
   */
  private int maxConnections = 500;

  /**
   * Maximum number of acquire attempts allowed to queue while the pool is saturated. {@code -1}
   * means unbounded (Reactor Netty default). Set a positive value to fail-fast under load
   * instead of letting backpressure pile up.
   */
  private int pendingAcquireMaxCount = -1;

}
