package com.unosquare.carmigo.exception;

public class ResourceNotFoundException extends RuntimeException
{
    public ResourceNotFoundException(final String message)
    {
        super(message);
    }
}
