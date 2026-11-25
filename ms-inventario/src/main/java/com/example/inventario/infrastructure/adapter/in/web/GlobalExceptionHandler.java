package com.example.inventario.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.inventario.domain.exception.InsufficientStockException;
import com.example.inventario.domain.exception.ProductNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ProductNotFoundException.class)
	public ProblemDetail handleNotFound(ProductNotFoundException ex) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(InsufficientStockException.class)
	public ProblemDetail handleInsufficientStock(InsufficientStockException ex) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	public ProblemDetail handleOptimisticLocking(ObjectOptimisticLockingFailureException ex) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
				"El stock fue modificado concurrentemente por otra operación. Por favor, reintenta.");
	}
}
