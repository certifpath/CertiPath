package com.audit.comments.exception;

public class UnauthorizedCommentAccessException extends RuntimeException {
    
    public UnauthorizedCommentAccessException() {
        super("Vous n'êtes pas autorisé à modifier ce commentaire");
    }
}