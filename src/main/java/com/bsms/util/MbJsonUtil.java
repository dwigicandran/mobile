package com.bsms.util;

import java.util.Date;
import java.util.List;

import com.bsms.cons.MbApiConstant;
import com.bsms.cons.MbConstant;
import com.bsms.restobj.MbApiContentResp;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobj.ResponseStatus;
import com.bsms.restobjclient.ActivationResp;
import com.bsms.restobjclient.BalanceInquiryDispResp;
import com.bsms.restobjclient.PINKeyResp;
import com.bsms.restobjclient.VerifyPinResp;

public class MbJsonUtil {

	public static MbApiResp createResponse(MbApiReq request, MbApiContentResp respContent, MbApiStatusResp respStatus) {

		MbApiResp response = new MbApiResp();

		response.setClientId(request.getClientId());
		response.setBranchId(request.getBranchId());
		response.setTellerId(request.getTellerId());
		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
		response.setResponseContent(respContent);
		// response.setRespStatus(respStatus);
		return response;

	}
	
	public static MbApiResp createResponse(MbApiReq request, MbApiContentResp respContent, MbApiStatusResp respStatus, String responseCode, String responseMessage) {

		MbApiResp response = new MbApiResp();

		response.setClientId(request.getClientId());
		response.setResponseCode(responseCode);
		response.setResponseMessage(responseMessage);
		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
		response.setResponseContent(respContent);
		// response.setRespStatus(respStatus);
		return response;

	}

	// activation
	public static MbApiResp createResponse(MbApiReq request, ActivationResp activationResp,
			MbApiStatusResp respStatus) {

		MbApiResp response = new MbApiResp();

		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
		// response.setRespContent(activationResp);
		response.setActivationResp(activationResp);
		response.setRespStatus(respStatus);
		return response;

	}

	// verify PIN
	public static MbApiResp createResponse(MbApiReq request, VerifyPinResp verifyPinResp,
			String response, String transactionId, String responseCode) {

		MbApiResp resp = new MbApiResp();

		resp.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
		resp.setResponse(response);
		resp.setTransactionId(transactionId);
		resp.setResponseCode(responseCode);
		
		return resp;

	}

	// pin key retrieval
	public static MbApiResp createResponse(MbApiReq request, PINKeyResp pinKeyResp, MbApiStatusResp respStatus) {

		MbApiResp response = new MbApiResp();

		response.setClientId(request.getClientId());
		response.setBranchId(request.getBranchId());
		response.setTellerId(request.getTellerId());
		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
		response.setPinKeyResp(pinKeyResp);
		response.setRespStatus(respStatus);
		return response;

	}

	// return balance info
//	public static MbApiResp createResponse(MbApiReq request, BalanceInquiryDispResp balanceInquiryDispResp,
//			MbApiStatusResp respStatus) {
//
//		MbApiResp response = new MbApiResp();
//
//		response.setClientId(request.getClientId());
//		response.setBranchId(request.getBranchId());
//		response.setTellerId(request.getTellerId());
//		response.setRespTime(MbDateFormatUtil.formatTime(new Date()));
//		response.setBalanceInquiryDispResp(balanceInquiryDispResp);
//		response.setRespStatus(respStatus);
//		return response;
//
//	}

	public static MbApiResp createResponseDesc(MbApiReq request, String responseCode, String responseDesc) {

		MbApiResp response = new MbApiResp();

		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
		response.setResponseCode(responseCode);
		response.setResponseMessage(responseDesc);
		return response;

	}
	
	public static MbApiResp createResponse(MbApiReq request, MbApiStatusResp respStatus) {

		MbApiResp response = new MbApiResp();

		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
		response.setRespStatus(respStatus);
		return response;

	}

	public static MbApiResp createResponse(MbApiReq request, String token) {

		MbApiResp response = new MbApiResp();

		response.setToken(token);
		response.setClientId(request.getClientId());
		response.setBranchId(request.getBranchId());
		response.setTellerId(request.getTellerId());
		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
		return response;

	}

	// pin key retrieval
	public static MbApiResp createResponse(MbApiReq request, String clearZpk, String resp, String transactionId,
			String responseCode) {

		MbApiResp response = new MbApiResp();

		response.setClientId(request.getClientId());
		response.setBranchId(request.getBranchId());
		response.setClearZpk(clearZpk);
		response.setResponse(resp);
		response.setTransactionId(transactionId);
		response.setResponseCode(responseCode);
		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
		return response;

	}

	public static MbApiResp createExceptionResponse(MbApiReq request, Exception e) {

		MbApiResp response = new MbApiResp();

		response.setClientId(request.getClientId());
		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
		// resp.setRespContent(null);
		response.setRespStatus(new MbApiStatusResp(MbConstant.ERROR_NUM_UNKNOWN, e.toString()));
		return response;

	}

	public static MbApiResp createExceptionSL(MbApiReq request, Exception e) {

		MbApiResp response = new MbApiResp();

		response.setClientId(request.getClientId());
		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
		// resp.setRespContent(null);
		response.setRespStatus(new MbApiStatusResp(MbConstant.ERROR_NUM_HOST_SL, e.toString()));
		return response;

	}

	public static MbApiResp createJsonParseExceptionResponse(Exception e) {

		MbApiResp response = new MbApiResp();

		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
//		response.setRespContent(null);
		response.setRespStatus(new MbApiStatusResp(MbConstant.ERROR_NUM_UNKNOWN, e.toString()));

		return response;

	}
	
	public static MbApiResp createJsonParseExceptionResponse(Exception e, String respDesc) {

		MbApiResp response = new MbApiResp();

		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
		response.setResponseCode(MbConstant.ERROR_NUM_UNKNOWN);
		response.setResponseMessage(respDesc);
//		response.setResponseDescription(respDesc);
		
//		response.setRespStatus(new MbApiStatusResp(MbConstant.ERROR_NUM_UNKNOWN, e.toString()));

		return response;

	}

	public static MbApiResp createResponse(MbApiReq request, Object respContent) {

		MbApiResp response = new MbApiResp();

		response.setChannelId(request.getChannelId());
		response.setChannelType(request.getChannelType());
		response.setTraceNum(request.getTraceNum());
		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));

		response.setContent(respContent);

		return response;

	}
	
	public static MbApiResp createResponseCustom(MbApiReq request, MbApiStatusResp[] mbApiStatusResps) {

		MbApiResp response = new MbApiResp();

		response.setChannelId(request.getChannelId());
		response.setChannelType(request.getChannelType());
		response.setTraceNum(request.getTraceNum());
		response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
		
		String respCode = null;
		String respDesc = null;
		
		for(MbApiStatusResp statusResp : mbApiStatusResps) {
			respCode = statusResp.getRespCode();
			respDesc = statusResp.getRespDesc();
		}
		
		response.setResponseCode(respCode);
		response.setResponseMessage(respDesc);

		return response;

	}
	
	public static MbApiResp createResponseTrf(String rc,String rm,  MbApiContentResp respContent, String trx_id) {

		MbApiResp response = new MbApiResp();

		response.setResponseCode(rc);
		response.setResponseMessage(rm);
		response.setResponseContent(respContent);
		response.setTransactionId(trx_id);
		return response;

	}
	
	public static MbApiResp createResponseBank(String rc,String rm,  MbApiContentResp respContent) {

		MbApiResp response = new MbApiResp();

		response.setResponseCode(rc);
		response.setResponseMessage(rm);
		response.setResponseContent(respContent);
		return response;

	}
	
	public static MbApiResp createResponseTrfMethod(String rc,String rm,  MbApiContentResp respContent) {

		MbApiResp response = new MbApiResp();

		response.setResponseCode(rc);
		response.setResponseMessage(rm);
		response.setResponseContent(respContent);
		return response;

	}


}
