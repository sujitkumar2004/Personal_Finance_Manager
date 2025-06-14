package com.sujit.personal_Finance_Manager.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest mockRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        mockRequest = mock(WebRequest.class);
    }

    @Test
    void handleBadRequest_shouldReturnBadRequestResponse() {
        BadRequestException ex = new BadRequestException("Invalid input");
        ResponseEntity<Object> response = exceptionHandler.handleBadRequest(ex, mockRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertResponseBody(response, "Bad Request", "Invalid input");
    }

    @Test
    void handleConflict_shouldReturnConflictResponse() {
        ConflictException ex = new ConflictException("Conflict occurred");
        ResponseEntity<Object> response = exceptionHandler.handleConflict(ex, mockRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertResponseBody(response, "Conflict", "Conflict occurred");
    }

    @Test
    void handleNotFound_shouldReturnNotFoundResponse() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        ResponseEntity<Object> response = exceptionHandler.handleNotFound(ex, mockRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertResponseBody(response, "Not Found", "Resource not found");
    }

    @Test
    void handleAll_shouldReturnInternalServerErrorResponse() {
        Exception ex = new Exception("Internal error");
        ResponseEntity<Object> response = exceptionHandler.handleAll(ex, mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertResponseBody(response, "Internal Server Error", "Internal error");
    }

    @SuppressWarnings("unchecked")
    private void assertResponseBody(ResponseEntity<Object> response, String expectedError, String expectedMessage) {
        assertNotNull(response.getBody());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(expectedError, body.get("error"));
        assertEquals(expectedMessage, body.get("message"));
        assertEquals(response.getStatusCodeValue(), body.get("status"));
    }
}
