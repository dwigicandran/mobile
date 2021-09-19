package com.bsms.controller;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.bsms.util.RestUtil;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.bsms.domain.ErrorMessage;
import com.bsms.domain.MbAppContent;
import com.bsms.except.CustomException;
import com.bsms.except.MbServiceException;
import com.bsms.profile.CardMapping;
import com.bsms.profile.Customer;
import com.bsms.profile.CustomerActivation;
import com.bsms.repository.ErrormsgRepository;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.service.authentication.VerifyServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.trx.TrxLog;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbVerifyPinUtil;
import com.bsms.verification.MultiReqVerification;
import com.bsms.verification.PINVerification;
import com.bsms.verification.ServiceVerification;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;
import org.springframework.web.client.RestTemplate;

@Component
@Path("/api/services")
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

    @Value("${mutation.download}")
    private String mutationUrl;


    @POST
    @Path("/{serviceName}")
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
                System.out.println(new Gson().toJson("cms :" + cmS.obtain(request.getLanguage(), customerId, request.getAccount_number())));

                if (ServiceVerification.isPINVerificationReq(serviceName)) {
                    pvS.execute(request.getLanguage(), customerId);

                    response = verifyServiceImpl.process(header, requestContext, request);

                    if (!"00".equals(response.getResponseCode())) {
                        ErrorMessage errMsg = errormsgRepository.findByCodeAndLanguage(response.getResponseCode(), request.getLanguage());
                        response = MbJsonUtil.createResponseDesc(request, response.getResponseCode(), errMsg.getDescription());
                    } else {
                        MbService service = (MbService) context.getBean(serviceName);
                        response = service.process(header, requestContext, request);
                    }
                } else {
                    MbService service = (MbService) context.getBean(serviceName);
                    response = service.process(header, requestContext, request);
                }

            } else {
                MbService service = (MbService) context.getBean(serviceName);
                response = service.process(header, requestContext, request);
            }

        } catch (MbServiceException e) {
            throw createException(e, request);
        } catch (CustomException customException){
        	log.error(serviceName+" AppException with error code: "+customException.getCode()+" message: "+customException.getDescription());
        	customException.printStackTrace();            
            response = MbJsonUtil.createJsonParseExceptionResponse(customException, customException.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            String lang = request.getLanguage() != null ? request.getLanguage() : "en";
            String msg = lang.equals("id") ? "Permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi." : "Request cannot be process, please try again later.";
            response = MbJsonUtil.createJsonParseExceptionResponse(e, msg);
        }

        return response;
    }

    //Addition By Dwi S & Ferdi Haspi
    @POST
    @Path("/{serviceName}/{paramNo}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MbApiResp serviceWithSecParam(@PathParam("serviceName") String serviceName, @PathParam("paramNo") String paramNo, String jsonRequest) {
        System.out.println("run 2nd router " + serviceName);
        MbApiResp response = null;
        MbApiReq request = null;
        String customerId;

        try {
            request = objectMapper.readValue(jsonRequest, MbApiReq.class);

            if (ServiceVerification.isTransactionReq(serviceName)) {

                log.info("service name : " + serviceName);

                JSONObject ca = caS.obtain(request.getLanguage(), request.getSessionId());
                request.setCustomerId(ca.getString("customerid"));
                request.setZpkLMK(ca.getString("zpk_lmk"));
                customerId = ca.getString("customerid");
                log.info("CA : " + new Gson().toJson(ca));

                JSONObject cust = custS.obtain(request.getLanguage(), customerId);
                request.setCustomerName(cust.getString("name"));
                request.setMsisdn(cust.getString("msisdn"));
                request.setCustomerEmail(cust.getString("email"));
                request.setCustomerLimitType(cust.getInt("type"));
                log.info("Customer : " + new Gson().toJson(cust));

                JSONObject cardMapping = cmS.obtain(request.getLanguage(), customerId, request.getAccount_number());
                request.setPan(cardMapping.getString("cardnumber"));
                request.setPinOffset(cardMapping.getString("pinoffset"));
                log.info(new Gson().toJson("cms :" + cmS.obtain(request.getLanguage(), customerId, request.getAccount_number())));

                if (ServiceVerification.isPINVerificationReq(serviceName)) {
                    pvS.execute(request.getLanguage(), customerId);

                    response = verifyServiceImpl.process(header, requestContext, request);

                    if (!"00".equals(response.getResponseCode())) {
                        ErrorMessage errMsg = errormsgRepository.findByCodeAndLanguage(response.getResponseCode(), "id");
                        response = MbJsonUtil.createResponseDesc(request, response.getResponseCode(), errMsg.getDescription());
                    } else {
                        MbService service = (MbService) context.getBean(serviceName);
                        response = service.process(header, requestContext, request);
                    }
                } else {
                    MbService service = (MbService) context.getBean(serviceName);
                    response = service.process(header, requestContext, request);
                }

            } else {
                MbService service = (MbService) context.getBean(serviceName);
                response = service.process(header, requestContext, request);
            }

        } catch (MbServiceException e) {
            e.printStackTrace();
            throw createException(e, request);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error : " + e.getMessage());
            String msg;
            msg = request.getLanguage().equals("en") ? "Your request could not be processed, please try again later" : "Permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
//            response = MbJsonUtil.createJsonParseExceptionResponse(e, msg);
//            MbLogUtil.writeLogError(log, e, MbApiConstant.NOT_AVAILABLE);
            response = MbJsonUtil.createJsonParseExceptionResponse(e, msg);
        }
        return response;
    }

    //adding by Dwi S
    @POST
    @Path("/getStatement")
    public Response downloadStatement(String jsonRequest) {
        MbApiResp apiResp = new MbApiResp();
        ResponseBuilder responseBuilder;

        MbApiReq request = null;
        Object mbApiResp = null;
        MbApiResp response = null;
        String customerId;

        try {
            request = objectMapper.readValue(jsonRequest, MbApiReq.class);

            //check customer activation and cardmapping
            JSONObject ca = caS.obtain(request.getLanguage(), request.getSessionId());
            request.setCustomerId(ca.getString("customerid"));
            request.setZpkLMK(ca.getString("zpk_lmk"));
            customerId = ca.getString("customerid");
            log.info("CA : " + new Gson().toJson(ca));

            JSONObject cust = custS.obtain(request.getLanguage(), customerId);
            request.setCustomerName(cust.getString("name"));
            request.setMsisdn(cust.getString("msisdn"));
            request.setCustomerEmail(cust.getString("email"));
            request.setCustomerLimitType(cust.getInt("type"));
            log.info("Customer : " + new Gson().toJson(cust));

            JSONObject cardMapping = cmS.obtain(request.getLanguage(), customerId, request.getAccount_number());
            request.setPan(cardMapping.getString("cardnumber"));
            request.setPinOffset(cardMapping.getString("pinoffset"));
            log.info(new Gson().toJson("cms :" + cmS.obtain(request.getLanguage(), customerId, request.getAccount_number())));
            //check customer activation and cardmapping

            //check pin verification
            pvS.execute(request.getLanguage(), customerId);
            response = verifyServiceImpl.process(header, requestContext, request);

            if (!"00".equals(response.getResponseCode())) {
                ErrorMessage errMsg = errormsgRepository.findByCodeAndLanguage(response.getResponseCode(), request.getLanguage());
                mbApiResp = MbJsonUtil.createResponseDesc(request, response.getResponseCode(), errMsg.getDescription());
                responseBuilder = Response.ok(mbApiResp).type("application/json");
            } else {
                HttpEntity<?> req = new HttpEntity(jsonRequest, RestUtil.getHeaders());
                RestTemplate restTemps = new RestTemplate();
                String url = mutationUrl;
                log.info("Download Mutasi Url : " + url);
                ResponseEntity<byte[]> restResponse = restTemps.exchange(url, HttpMethod.POST, req, byte[].class);
                mbApiResp = restResponse.getBody();
                responseBuilder = Response.ok(mbApiResp).type("application/pdf").header(HttpHeaders.CONTENT_LENGTH, restResponse.getBody().length);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorDefault = "Permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
            if (request.getLanguage().equals("en")) {
                errorDefault = "Request can't be process, please try again later.";
            }
            mbApiResp = MbJsonUtil.createJsonParseExceptionResponse(e, errorDefault);
            responseBuilder = Response.ok(mbApiResp).type("application/json");
        }

        return responseBuilder.build();
    }

    @GET
    @Path("/checkServices")
    @Produces(MediaType.APPLICATION_JSON)
    public MbApiResp checkService() {
        return new MbApiResp("OK");
    }

    protected WebApplicationException createException(MbServiceException e, MbApiReq request) {
        MbApiResp response = MbJsonUtil.createResponseCustom(request, e.getErrors());
        return new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(response).build());
    }


}
