package com.sujit.personal_Finance_Manager.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Category not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testExceptionIsRuntimeException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Anything");

        assertTrue(exception instanceof RuntimeException);
    }
}
