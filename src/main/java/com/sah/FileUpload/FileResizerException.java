package com.sah.FileUpload;

public class FileResizerException extends RuntimeException {
    public FileResizerException(String message) {
        super(message);
    }

    public FileResizerException(FileResizerErrors err) {
        super(err.name());
    }
}
