package io.revx.core.enums;

public enum CrmStatus {
	CREATED(10, true, false),

	POPPED(11, true, false, new Retry(10)),

	SUBMITTED(12, true, false, new Retry(10)),

	SUBMIT_FAILED(13, false, true, new Retry(30)),

	RUNNING(20, true, false),

	PREPARATION(21, true, false),

	RETRY_PENDING(30, true, true),

	RETRIES_OVER(31, false, true),

	CONNECTION_FAILED(50, false, true),

	AUTH_FAILED(51, false, true),

	FILE_NOT_FOUND(52, false, true),

	DOWNLOADING(100, true, false),

	DOWNLOADED(101, true, false),

	DOWNLOAD_FAILED(102, false, true),

	RULE_PROCESSING(110, true, false),

	RULE_PROCESSED(111, true, false),

	UPLOADING(120, true, false),

	UPLOADED(121, false, false),

	UPLOAD_FAILED(130, false, true),

	UPLOAD_COMPLETED_PARTIALLY_FAILED(131, false, true),

	UPLOAD_FAILED_NO_USER_MATCHED(132, false, true),

	UPLOAD_PARTIALLY_COMPLETED(136, true, false),

	UPLOAD_PARTIALLY_FAILED_AND_RUNNING(137, true, true),

	COMPLETED(200, false, false),

	URI_ERROR(404, false, true),

	NETWORK_FAILURE(501, false, true),

	FILE_NOT_MODIFIED_AT_SERVER(1, false, false),

	SAME_MD5SUM(1001, false, false),

	SUCCESS(1002, false, false),

	IOEXCEPTION(1003, false, true),

	INVALIDDATA(1004, false, true),

	UNKNOWN_PROTOCOL(1005, false, true),

	PROCESSING_FAILED(1006, false, true)

	;

	public final Integer id;
	public final boolean isRunning;
	public final boolean isFailed;
	public final Retry retry;

	public static class Retry {
		public final int retryInMinutes;
		public final int retryCount;

		public Retry(int retryInMinutes, int retryCount) {
			this.retryInMinutes = retryInMinutes;
			this.retryCount = retryCount;
		}

		public Retry(int retryInMinutes) {
			this.retryInMinutes = retryInMinutes;
			this.retryCount = -1;
		}
	}

	private CrmStatus(int id, boolean isRunning, boolean isFailed, Retry retry) {
		this.id = id;
		this.isRunning = isRunning;
		this.isFailed = isFailed;
		this.retry = retry;
	}

	private CrmStatus(int id, boolean isRunning, boolean isFailed) {
		this(id, isRunning, isFailed, null);
	}

	public static CrmStatus getById(Integer id) {
		for (CrmStatus type : values()) {
			if (type.id.equals(id))
				return type;
		}
		return null;
	}

	public static boolean isRunning(CrmStatus status) {
		return status.isRunning;
	}

	public static boolean isFailed(CrmStatus status) {
		return status.isFailed;
	}

	public static Integer[] POPPABLE_STATUSES = new Integer[] { CREATED.id,
			POPPED.id, SUBMIT_FAILED.id };

}
