package com.bsms.controller;

import java.io.IOException;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.bsms.cons.MbApiConstant;
import com.bsms.except.AppException;
import com.bsms.except.MbServiceException;
import com.bsms.profile.CardMapping;
import com.bsms.profile.Customer;
import com.bsms.profile.CustomerActivation;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.service.base.MbService;
import com.bsms.trx.TrxLog;
import com.bsms.util.MbDateFormatUtil;
import com.bsms.util.MbErrorUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.MessageUtil;
import com.bsms.verification.MultiReqVerification;
import com.bsms.verification.PINVerification;
import com.bsms.verification.ServiceVerification;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;

public class BckController {

	private static Logger log = LoggerFactory.getLogger(ApiBaseController.class);
	
	private static final String INTERNAL_SERVER_ERROR = "500";
	
	@Autowired
	private ApplicationContext context;
	@Context
    private ContainerRequestContext requestContext;
	@Context
    private HttpHeaders header;
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	CustomerActivation caS;
	@Autowired
	Customer custS;
	@Autowired
	CardMapping cmS;
	@Autowired
	MultiReqVerification mrvS;
	@Autowired
	TrxLog trxLogS;
	@Autowired
	PINVerification pvS;
	
	@POST @Path("/services/{serviceName}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public MbApiResp service(@PathParam("serviceName") String serviceName, String jsonRequest) {
		
		MbApiResp response = null;
		MbApiReq request = null;
		
		try {
			request = objectMapper.readValue(jsonRequest, MbApiReq.class);
			/*request.getClientId();
			request.getReqTime();*/
			
			if (ServiceVerification.isTransactionReq(serviceName)) {
				// Get the Customer Activation
				JSONObject ca = caS.obtain(request.getLanguage(), request.getCaId());
				request.setCustomerId(ca.getString("customerid"));
				request.setZpkLMK(ca.getString("zpk_lmk"));
				
				// Get the Customer
				//JSONObject cust = custS.obtain(request.getLanguage(), ca.getString("customerid"));
				JSONObject cust = custS.obtain(request.getLanguage(), request.getCustomerId());
				request.setCustomerName(cust.getString("name"));
				request.setMsisdn(cust.getString("msisdn"));
				request.setCustomerEmail(cust.getString("email"));
				request.setCustomerLimitType(cust.getInt("type"));
				
				// Get the CardMapping
				//JSONObject cardMapping = cmS.obtain(request.getLanguage(), ca.getString("customerid"), request.getIdAccount());
				JSONObject cardMapping = cmS.obtain(request.getLanguage(), request.getCustomerId(), request.getIdAccount());
				request.setPan(cardMapping.getString("cardnumber"));
				request.setPinOffset(cardMapping.getString("pinoffset"));
				
				//if (ServiceVerification.isMultiReqReq(serviceName)) {
					// Verify whether the same Trx exists
					//mrvS.execute(request.getLanguage(), ca.getString("customerid"), request.getDateLocal());
				//}
				if (ServiceVerification.isMultiReqReq(serviceName)) {
					// Verify whether the same Trx exists
					//mrvS.execute(request.getLanguage(), ca.getString("customerid"), request.getDateLocal());
					mrvS.execute(request.getLanguage(), request.getCustomerId(), request.getDateLocal());
				}
				
				//if (ServiceVerification.isPINVerificationReq(serviceName)) {
					// Get the CardMapping
					//JSONObject cardMapping = cmS.obtain(request.getLanguage(), ca.getString("customerid"), request.getIdAccount()); 
					// Verify the PIN
					
				//}
				if (ServiceVerification.isPINVerificationReq(serviceName)) {
					// Get the CardMapping
					//JSONObject cardMapping = cmS.obtain(request.getLanguage(), ca.getString("customerid"), request.getIdAccount()); 
					// Verify the PIN
					pvS.execute(request.getLanguage(), request.getCustomerId());
				}
				
				//if (ServiceVerification.isFinTrxReq(serviceName)) {
					// Get the existing Trx
				//}
				if (ServiceVerification.isFinTrxReq(serviceName)) {
					// Get the existing Trx
					JSONObject trxLog = trxLogS.obtain(request.getLanguage(), request.getTransactionId());
					JSONObject requestLog = trxLog.getJSONObject("request");
					
				}			
			}

			/*if (ServiceVerification.isFinTrxReq(serviceName)) {
				// Get the existing Trx
				JSONObject trxLog = trxLogS.obtain(request.getLanguage(), request.getTransactionId());
				JSONObject requestLog = trxLog.getJSONObject("request");
				
			}*/

			/*if (ServiceVerification.isMultiReqReq(serviceName)) {
				// Verify whether the same Trx exists
				//mrvS.execute(request.getLanguage(), ca.getString("customerid"), request.getDateLocal());
				mrvS.execute(request.getLanguage(), request.getCustomerId(), request.getDateLocal());
			}*/

			/*if (ServiceVerification.isPINVerificationReq(serviceName)) {
				// Get the CardMapping
				//JSONObject cardMapping = cmS.obtain(request.getLanguage(), ca.getString("customerid"), request.getIdAccount()); 
				// Verify the PIN
				
			}*/

			// Service Dispatcher
			MbService service = (MbService) context.getBean(serviceName);
			response = service.process(header, requestContext, request);
			
		} catch (JsonParseException e) {
			response = MbJsonUtil.createJsonParseExceptionResponse(e);
			MbLogUtil.writeLogError(log, e, MbApiConstant.NOT_AVAILABLE);
		} catch (JsonMappingException e) {
			response = MbJsonUtil.createJsonParseExceptionResponse(e);
			MbLogUtil.writeLogError(log, e, MbApiConstant.NOT_AVAILABLE);
		} catch (IOException e) {
			response = MbJsonUtil.createJsonParseExceptionResponse(e);
			MbLogUtil.writeLogError(log, e, MbApiConstant.NOT_AVAILABLE);
		} catch (MbServiceException e) {
            throw createException(e, request);
        } catch (Exception e) {
            throw createException(e, request);
        }
		
		return response;
		
	}
	
	/*@RequestMapping(value="/sayHello")
	public String sayHello() {
		return "Hello Spring Boot";
	}*/
	
	protected WebApplicationException createException(Exception e, MbApiReq request) {
        MbLogUtil.writeLogError(log, e, request==null ? MbApiConstant.NOT_AVAILABLE : request.getTraceNum());
        MbApiResp response = new MbApiResp();
        if (request == null) {
            response.setChannelId(MbApiConstant.NOT_AVAILABLE);
            response.setChannelType(MbApiConstant.NOT_AVAILABLE);
            response.setTraceNum(MbApiConstant.NOT_AVAILABLE);
            response.setResponseTime(MbDateFormatUtil.formatTime(new Date()));

            response.setContent(MbErrorUtil.createError(INTERNAL_SERVER_ERROR, e.getMessage()));
        } else {
        	if (e instanceof AppException)
        		response = MbJsonUtil.createResponse(request, MbErrorUtil.createError(INTERNAL_SERVER_ERROR, e.getMessage()));
        	else
        		try { 
        			response = MbJsonUtil.createResponse(request, MbErrorUtil.createError(INTERNAL_SERVER_ERROR, MessageUtil.obtain("600002", request.getLanguage())));
        		}
	        	catch (Exception ex) {
	        		
	        	}
        }
        return new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(response).build());
    }
	
	protected WebApplicationException createException(MbServiceException e, MbApiReq request){
		MbApiResp response = MbJsonUtil.createResponse(request, e.getErrors());
        return new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(response).build());
    }
	
}
