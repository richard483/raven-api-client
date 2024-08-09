package com.nephren.raven.apiclient.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RavenApiException extends RuntimeException {
  private final String message;
  private final Throwable cause;
  
}
