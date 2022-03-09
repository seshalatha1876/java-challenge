package jp.co.axa.apidemo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Added EmployeeNotFoundAdvice to handle employee not found exception
 */
@ControllerAdvice
public class EmployeeNotFoundAdvice {

	@ResponseBody
	@ExceptionHandler(EmployeeNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="No such employee")
	String employeeNotFoundHandler(EmployeeNotFoundException ex) {
		return ex.getMessage();
	}
}