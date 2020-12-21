package com.bsms.util;

import java.util.Date;

import com.bsms.cons.MbConstant;
import com.bsms.restobj.MbApiContentResp;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobjclient.authentication.ActivationResp;
import com.bsms.restobjclient.authentication.PINKeyResp;
import com.bsms.restobjclient.base.BaseResponse;
import com.bsms.restobjclient.payment.Content;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MbJsonUtil {

    public static MbApiResp createResponse(BaseResponse baseResponse) {
        MbApiResp response = new MbApiResp();
        response.setResponseCode(baseResponse.getResponseCode());
        response.setResponseMessage(baseResponse.getResponseMessage());
        response.setResponseContent(baseResponse.getResponseContent());
        response.setTransactionId(baseResponse.getTransactionId());
        response.setContent(baseResponse.getContent() != null ? baseResponse.getContent() : null);
        return response;
    }

    public static MbApiResp createResponse(MbApiReq request, MbApiContentResp respContent, MbApiStatusResp respStatus) {

        MbApiResp response = new MbApiResp();

        response.setClientId(request.getClientId());
        response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
        response.setResponseContent(respContent);
        return response;

    }

    public static MbApiResp createResponse(MbApiReq request, MbApiContentResp respContent, MbApiStatusResp respStatus, String responseCode, String responseMessage) {

        MbApiResp response = new MbApiResp();

        response.setClientId(request.getClientId());
        response.setResponseCode(responseCode);
        response.setResponseMessage(responseMessage);
        response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
        response.setResponseContent(respContent);

        return response;

    }

    public static MbApiResp createResponse(MbApiReq request, MbApiContentResp respContent, String responseCode, String responseMessage) {

        MbApiResp response = new MbApiResp();

        response.setResponseCode(responseCode);
        response.setResponseMessage(responseMessage);
        response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
        response.setResponseContent(respContent);

        return response;

    }

    // activation
    public static MbApiResp createResponse(MbApiReq request, ActivationResp activationResp,
                                           MbApiStatusResp respStatus) {

        MbApiResp response = new MbApiResp();

        response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
        response.setActivationResp(activationResp);
        response.setRespStatus(respStatus);
        return response;

    }

    // verify PIN
//	public static MbApiResp createResponse(MbApiReq request, VerifyPinResp verifyPinResp,
//			String response, String transactionId, String responseCode) {
//
//		MbApiResp resp = new MbApiResp();
//
//		resp.setResponseTime(MbDateFormatUtil.formatTime(new Date()));
//		resp.setResponse(response);
//		resp.setTransactionId(transactionId);
//		resp.setResponseCode(responseCode);
//		
//		return resp;
//
//	}

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

        for (MbApiStatusResp statusResp : mbApiStatusResps) {
            respCode = statusResp.getRespCode();
            respDesc = statusResp.getRespDesc();
        }

        response.setResponseCode(respCode);
        response.setResponseMessage(respDesc);

        return response;

    }

    public static MbApiResp createResponseTrf(String rc, String rm, MbApiContentResp respContent, String trx_id) {

        MbApiResp response = new MbApiResp();

        response.setResponseCode(rc);
        response.setResponseMessage(rm);
        response.setResponseContent(respContent);
        response.setTransactionId(trx_id);
        return response;

    }

    public static MbApiResp createResponseBank(String rc, String rm, MbApiContentResp respContent) {

        MbApiResp response = new MbApiResp();

        response.setResponseCode(rc);
        response.setResponseMessage(rm);
        response.setResponseContent(respContent);
        return response;

    }

    public static MbApiResp createResponseTrfMethod(String rc, String rm, MbApiContentResp respContent) {

        MbApiResp response = new MbApiResp();

        response.setResponseCode(rc);
        response.setResponseMessage(rm);
        response.setResponseContent(respContent);
        return response;

    }

    //addition by Dwi S
    public static MbApiResp createSPMerchantResponse(String message, Content content) {
        MbApiResp response = new MbApiResp();
        response.setResponseCode("99");
        response.setResponseMessage(message);
        response.setContent(content);
        return response;
    }

    //addition by Dwi S
    public static MbApiResp createErrResponse(BaseResponse baseResponse) {
        MbApiResp response = new MbApiResp();
        response.setResponseCode(baseResponse.getResponseCode());
        response.setTransactionId(baseResponse.getTransactionId());
        response.setResponseMessage(baseResponse.getResponseMessage());
        response.setContent(baseResponse.getContent());
        return response;
    }

    //addition by Dwi S
    public static MbApiResp createPDAMResponse(BaseResponse baseResponse, String language) {
        MbApiResp response = new MbApiResp();
        response.setResponseCode(baseResponse.getResponseCode());
        response.setResponseMessage(baseResponse.getResponseMessage());
        response.setResponseContent(baseResponse.getResponseContent());
        response.setTransactionId(baseResponse.getTransactionId());

        Object objContent = baseResponse.getContent() != null ? baseResponse.getContent() : null;
        response.setContent(objContent);

        if (objContent != null) {
            System.out.println("Run Mapper");
            ObjectMapper objectMapper = new ObjectMapper();
            com.bsms.restobjclient.pdam.Content content = objectMapper.convertValue(objContent, com.bsms.restobjclient.pdam.Content.class);
            MbApiResp resp = createPDAMErrorResponse(content, language);
            if (!content.getErrorCode().equalsIgnoreCase("00")) {
                response.setResponseCode(resp.getResponseCode());
                response.setResponseMessage(resp.getResponseMessage());
            }
        }

        return response;
    }

    //addition by Dwi S
    public static MbApiResp createPDAMErrorResponse(com.bsms.restobjclient.pdam.Content content, String language) {
        System.out.println("run pdam error ");
        MbApiResp response = new MbApiResp();
        String errorCode = content.getErrorCode();

        if (errorCode.equalsIgnoreCase(MbConstant.PDAM_ERROR_PAID_OFF)) {
            response.setResponseCode("88");
            response.setResponseMessage(language.equalsIgnoreCase("en") ? MbConstant.PDAM_ERROR_PAID_OFF_EN : MbConstant.PDAM_ERROR_PAID_OFF_ID);
        } else if (errorCode.equalsIgnoreCase(MbConstant.PDAM_ERROR_MIN_PAYMENT)) {
            response.setResponseCode(errorCode);
            response.setResponseMessage(language.equalsIgnoreCase("en") ? MbConstant.PDAM_ERROR_MIN_PAYMENT_EN : MbConstant.PDAM_ERROR_MIN_PAYMENT_ID);
        } else if (errorCode.equalsIgnoreCase("89")) {
            response.setResponseCode(errorCode);
            response.setResponseMessage(MbConstant.PDAM_ERROR_TIMEOUT);
        } else if (errorCode.equalsIgnoreCase(MbConstant.PDAM_ERROR_PASSIVE)) {
            response.setResponseCode("99");
            response.setResponseMessage(language.equalsIgnoreCase("en") ? MbConstant.PDAM_ERROR_PASSIVE_EN : MbConstant.PDAM_ERROR_PASSIVE_ID);
        } else {
            response.setResponseCode("99");
            response.setResponseMessage(language.equalsIgnoreCase("en") ? MbConstant.ERROR_REQUEST_EN : MbConstant.ERROR_REQUEST_ID);
        }

        return response;
    }

    //addition By Dwi S
    public static MbApiResp createPdamPaymentErrorResponse(BaseResponse baseResponse, String language) {
        MbApiResp response = new MbApiResp();
        response.setResponseCode(baseResponse.getResponseCode());
        response.setResponseMessage(baseResponse.getResponseMessage());
        response.setResponseContent(baseResponse.getResponseContent());
        response.setTransactionId(baseResponse.getTransactionId());

        Object objContent = baseResponse.getContent() != null ? baseResponse.getContent() : null;
        response.setContent(objContent);

        if (objContent != null) {
            System.out.println("Run Mapper");
            ObjectMapper objectMapper = new ObjectMapper();
            com.bsms.restobjclient.pdam.Content content = objectMapper.convertValue(objContent, com.bsms.restobjclient.pdam.Content.class);
            MbApiResp resp = createPDAMErrorResponse(content, language);
            if (!content.getErrorCode().equalsIgnoreCase("00")) {
                response.setResponseCode(resp.getResponseCode());
                response.setResponseMessage(resp.getResponseMessage());
            }
        }

        return response;
    }


}
