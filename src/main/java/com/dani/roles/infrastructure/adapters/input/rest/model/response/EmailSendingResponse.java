package com.dani.roles.infrastructure.adapters.input.rest.model.response;

public class EmailSendingResponse {

    private String message;

    public EmailSendingResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
