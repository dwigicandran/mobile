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
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.ErrorMessage;
import com.bsms.domain.MbAppContent;
import com.bsms.except.AppException;
import com.bsms.except.MbServiceException;
import com.bsms.profile.CardMapping;
import com.bsms.profile.Customer;
import com.bsms.profile.CustomerActivation;
import com.bsms.repository.ErrormsgRepository;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.service.MbService;
import com.bsms.service.VerifyServiceImpl;
import com.bsms.trx.TrxLog;
import com.bsms.util.MbDateFormatUtil;
import com.bsms.util.MbErrorUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.MbVerifyPinUtil;
import com.bsms.util.MessageUtil;
import com.bsms.verification.MultiReqVerification;
import com.bsms.verification.PINVerification;
import com.bsms.verification.ServiceVerification;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;

@Component
@Path("/api")
public class ApiBaseController {

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
	MbAppContentRepository mbAppContentRepository;
	
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
	
	@Autowired
	MbVerifyPinUtil mbVerifyPinUtil; 
	
	@Autowired
	VerifyServiceImpl verifyServiceImpl;
	
	@Autowired
	private ErrormsgRepository errormsgRepository;
	
	@POST @Path("/services/{serviceName}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public MbApiResp service(@PathParam("serviceName") String serviceName, String jsonRequest) {
		
		MbApiResp response = null;
		MbApiReq request = null;
		
		String customerId;
		
		MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", "id");
		
		try {
			request = objectMapper.readValue(jsonRequest, MbApiReq.class);
			
//			mbVerifyPinUtil.getRC(request.getSessionId(), request.getAccount_number());
			
			if (ServiceVerification.isTransactionReq(serviceName)) {
				
				JSONObject ca = caS.obtain(request.getLanguage(), request.getSessionId());
				request.setCustomerId(ca.getString("customerid"));
				request.setZpkLMK(ca.getString("zpk_lmk"));
				customerId = ca.getString("customerid");
				
				JSONObject cust = custS.obtain(request.getLanguage(), customerId);
				request.setCustomerName(cust.getString("name"));
				request.setMsisdn(cust.getString("msisdn"));
				request.setCustomerEmail(cust.getString("email"));
				request.setCustomerLimitType(cust.getInt("type"));
				
				JSONObject cardMapping = cmS.obtain(request.getLanguage(), customerId, request.getAccount_number());
				request.setPan(cardMapping.getString("cardnumber"));
				request.setPinOffset(cardMapping.getString("pinoffset"));
				
				if (ServiceVerification.isPINVerificationReq(serviceName)) {
					pvS.execute(request.getLanguage(), customerId);
					
					response = verifyServiceImpl.process(header, requestContext, request);
					
//					System.out.println(response.getResponseCode() + "*** RESPONSE CODE DARI VERIFY SERVICE ***");
					if(!"00".equals(response.getResponseCode())) {
						
						ErrorMessage errMsg = errormsgRepository.findByCodeAndLanguage(response.getResponseCode(), "id");
						response = MbJsonUtil.createResponseDesc(request, response.getResponseCode(), errMsg.getDescription());
					} else {
						MbService service = (MbService) context.getBean(serviceName);
						response = service.process(header, requestContext, request);
					}
				} else {
					// Service Dispatcher
					MbService service = (MbService) context.getBean(serviceName);
					response = service.process(header, requestContext, request);
				}
						
			} else {
				// Service Dispatcher
				MbService service = (MbService) context.getBean(serviceName);
				response = service.process(header, requestContext, request);
			}
			
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
        	e.printStackTrace();
        	//throw createException(e, request);
        	
        	response = MbJsonUtil.createJsonParseExceptionResponse(e, "Permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.");
			MbLogUtil.writeLogError(log, e, MbApiConstant.NOT_AVAILABLE);
        }
		
		return response;
		
	}
	
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
