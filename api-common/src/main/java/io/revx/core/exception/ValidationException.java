package io.revx.core.exception;

public class ValidationException extends ApiException {

	private static final long serialVersionUID = 2182343982282655443L;

	public ValidationException(String message) {
		super(ErrorCode.BAD_REQUEST, message);
	}

	public ValidationException(ErrorCode errorCode, Object[] objects) {
		super(errorCode, objects);
	}

	public ValidationException(ErrorCode errorCode, String msg) {
		super(errorCode, new Object[] { msg });
	}

	public ValidationException(ErrorCode errorCode) {
		super(errorCode, errorCode.getErrorMessage());
	}

	public ValidationException(Integer errorCode) {
	}

}
