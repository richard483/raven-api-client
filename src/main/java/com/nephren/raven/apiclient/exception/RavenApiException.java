package com.nephren.raven.apiclient.exception;

/**
 * RavenApiException
 *
 * <p>
 * Exception class for API calls using the RavenApiClient. This class is used to handle exceptions
 * that occur during API calls with the RavenApiClient.
 * </p>
 */

public class RavenApiException extends RuntimeException {

  public RavenApiException(String message) {
    super(message);
  }

  public RavenApiException(String message, Throwable cause) {
    super(message, cause);
  }

}
