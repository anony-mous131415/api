package io.revx.core.exception;

public class QueryBuilderException extends ApiException {

	private static final long serialVersionUID = -6073608298877291015L;

	public QueryBuilderException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public QueryBuilderException(ErrorCode errorCode) {
		super(errorCode, errorCode.getErrorMessage());
	}

}
