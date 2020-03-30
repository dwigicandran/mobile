package com.bsms.except;

public class AppException extends Exception {
	
	public static String DEFAULT_RC = "99";

	private String rc;
	
	public AppException(String rc, String msg) {
		this(msg);
		this.rc = rc;
	}
	
	public AppException(String msg) {
		super(msg);
	}
	
	public String getRC() {
		if (rc == null)
			rc = DEFAULT_RC;
		return rc;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
