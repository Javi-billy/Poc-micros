package com.example.pedidos.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.pedidos.domain.exception.InsufficientStockException;
import com.example.pedidos.infrastructure.adapter.out.feign.ExternalServiceUnavailableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InsufficientStockException.class)
	public ProblemDetail handleInsufficientStock(InsufficientStockException ex) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(ExternalServiceUnavailableException.class)
	public ProblemDetail handleServiceUnavailable(ExternalServiceUnavailableException ex) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
	}
}
