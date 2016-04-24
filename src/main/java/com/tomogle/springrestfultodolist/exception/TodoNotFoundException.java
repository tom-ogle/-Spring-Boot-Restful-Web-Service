package com.tomogle.springrestfultodolist.exception;

/**
 * Represents the case where todos cannot be found in the data store.
 */
public class TodoNotFoundException extends RuntimeException {
  public TodoNotFoundException(String message) {
    super(message);
  }
}
