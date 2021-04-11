package soham.spring.graphqldgs.error;

public class NoDataFoundError extends RuntimeException {

    private String errorCode;

    public NoDataFoundError(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return this.errorCode + " : " + super.getMessage();
    }

    public String getErrorCode() {
        return this.errorCode;
    }
}
