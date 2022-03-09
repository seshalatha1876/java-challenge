package jp.co.axa.apidemo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Added EmployeeNotFoundAdvice to handle employee not found exception
 */

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EmployeeNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}