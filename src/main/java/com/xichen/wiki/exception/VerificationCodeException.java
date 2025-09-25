package com.xichen.wiki.exception;

/**
 * 验证码相关异常
 * 
 * @author xichen
 * @since 2024-09-25
 */
public class VerificationCodeException extends RuntimeException {
    
    private final String errorCode;
    
    public VerificationCodeException(String message) {
        super(message);
        this.errorCode = "VERIFICATION_CODE_ERROR";
    }
    
    public VerificationCodeException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "VERIFICATION_CODE_ERROR";
    }
    
    public VerificationCodeException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public VerificationCodeException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
