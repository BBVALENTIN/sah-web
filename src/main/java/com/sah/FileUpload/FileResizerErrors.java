package com.sah.FileUpload;

public enum FileResizerErrors {
    DIR_NOT_FOUND,
    FILE_TOO_BIG,
    FILE_NOT_VALID,
    NOT_ALLOWED_DIMENSIONS,
    NO_WRITER_AVAILABLE;

    public static String dimensionsError(int w, int h) {
        return NOT_ALLOWED_DIMENSIONS.toString() + "current width: " + w + ", current height: " + h + ".";
    }

    public static String fileTooBigError(int allowedDimension) {
        return FILE_TOO_BIG.toString() + " allowed dimension is " + allowedDimension;
    }
}
