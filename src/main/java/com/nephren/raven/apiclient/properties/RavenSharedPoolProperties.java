package com.nephren.raven.apiclient.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Tunables for the Reactor Netty {@code ConnectionProvider} backing every {@code HttpClient}
 * the default factory creates. Most apps will never need to touch these — they are exposed
 * primarily for environments with high client fan-out or unusually slow downstreams.
 *
 * <p>These values apply to <em>every</em> provider the default factory builds, both shared
 * pools (one per scheme+host:port key plus read/write timeouts) and isolated pools opted
 * into via {@code configs.<name>.isolate-pool: true}. Per-client opt-out controls only
 * which key a client lands on — it does not change the per-pool sizing.</p>
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
   * Maximum number of acquire attempts allowed to queue while the pool is saturated. The
   * default of {@code -1} is a sentinel meaning "use Reactor Netty's library default", which
   * is {@code 2 * maxConnections}. Set a positive value to override (e.g. fail-fast under
   * load with a small queue, or raise the ceiling for very bursty workloads).
   *
   * <p>The library default is intentionally bounded — never set this to a value that would
   * let a single wedged downstream accumulate unbounded queued acquires and amplify a
   * single-backend outage into application-wide heap pressure.</p>
   */
  private int pendingAcquireMaxCount = -1;

}
