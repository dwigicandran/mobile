package com.bsms.except;

public class CustomException extends RuntimeException {

	public CustomException() {
		super();
	}

	public CustomException(String message) {
		super(message);
	}

	public CustomException(String code, String description) {
		super(description);
		setCodeDescription(code, description, "");
	}

	public CustomException(String code, String description, String message) {
		super(message);
		setCodeDescription(code, description, "");
	}

	public CustomException(BaseError baseError) {
		super(baseError.getDescription());
		setCodeDescription(baseError.getCode(), baseError.getDescription(), "");
	}

	public CustomException(BaseError baseError, String message) {
		super(message);
		setCodeDescription(baseError.getCode(), baseError.getDescription(), "");
	}

	private String trxId;
	private String code;
	private String description;

	public String getTrxId() {
		return trxId;
	}

	public void setTrxId(String trxId) {
		this.trxId = trxId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCodeDescription(String code, String description, String trxId) {
		this.code = code;
		this.description = description;
		this.trxId = trxId;
	}

	public void setCodeDescription(String code, String description) {
		setCodeDescription(code, description, "");
	}

	public void setCodeDescription(String code) {
		setCodeDescription(code, "", "");
	}

}
