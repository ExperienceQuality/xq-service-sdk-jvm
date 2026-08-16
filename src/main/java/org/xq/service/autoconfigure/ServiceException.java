package org.xq.service.autoconfigure;

import org.springframework.http.HttpStatusCode;

public final class ServiceException extends RuntimeException {
    private final HttpStatusCode status;
    private final String errorCode;

    public ServiceException(HttpStatusCode status, String errorCode, String detail) {
        super(detail);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
