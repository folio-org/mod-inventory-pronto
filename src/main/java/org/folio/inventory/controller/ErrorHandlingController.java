package org.folio.inventory.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
public class ErrorHandlingController {

  @ResponseBody()
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(EmptyResultDataAccessException.class)
  public String handleException(EmptyResultDataAccessException exception) {
    log.error(exception);
    return "Item not found: " + exception.getMessage();
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public String handleException(Exception exception) {
    log.debug(exception);
    return exception.getMessage();
  }
}
