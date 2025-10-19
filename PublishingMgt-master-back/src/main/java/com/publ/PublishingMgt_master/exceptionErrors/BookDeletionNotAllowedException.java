package com.publ.PublishingMgt_master.exceptionErrors;

public class BookDeletionNotAllowedException extends RuntimeException {
    public BookDeletionNotAllowedException(String message) {
        super(message);
    }
}

