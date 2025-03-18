package com.nephren.raven.apiclient.exception;

import lombok.AllArgsConstructor;

/**
 * RavenApiException
 *
 * <p>
 * Exception class for API calls using the RavenApiClient. This class is used to handle exceptions
 * that occur during API calls with the RavenApiClient.
 * </p>
 */

@AllArgsConstructor
public class RavenApiException extends RuntimeException {

  private final String message;
  private final Throwable cause;

}
