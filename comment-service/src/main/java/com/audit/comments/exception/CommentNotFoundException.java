package com.audit.comments.exception;

public class CommentNotFoundException extends RuntimeException {
    
    public CommentNotFoundException(String commentId) {
        super("Commentaire non trouvé avec l'ID: " + commentId);
    }
}