package com.bsms.except;

public abstract class BaseError {

	private String code;
	private String description;

	public BaseError(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return code;
	}

}
