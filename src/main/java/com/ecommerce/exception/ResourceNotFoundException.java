package com.ecommerce.exception;

public class ResourceNotFoundException extends RuntimeException {

    private String field;
    private String fieldName;
    private String resourceName;
    private Long fieldID;

    public ResourceNotFoundException(String field, String fieldName, String ResourceName) {
        super(String.format("%s not found in %s:%s",ResourceName,field,fieldName));
        this.field = field;
        this.fieldName = fieldName;
        this.resourceName = ResourceName;
    }

    public ResourceNotFoundException(String field, String ResourceName, Long fieldID) {
        super(String.format("%s not found with %s:%s",ResourceName,field,fieldID));
        this.field = field;
        this.resourceName = ResourceName;
        this.fieldID = fieldID;
    }
}
