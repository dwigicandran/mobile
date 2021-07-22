package com.bsms.verification;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceVerification {

	private static String TransactionService;
	private static String MultiReqService;
	private static String FinTrxService;
	private static String PINVerificationService;

	public ServiceVerification(@Value("${transaction.service}") String transactionService,
							   @Value("${multireq.service}") String multiReqService,
							   @Value("${fintrx.service}") String finTrxService,
							   @Value("${pinverification.service}") String pinVerificationService) {
		// TODO Auto-generated constructor stub
		ServiceVerification.TransactionService = transactionService.toLowerCase();
		ServiceVerification.MultiReqService = multiReqService.toLowerCase();
		ServiceVerification.FinTrxService = finTrxService.toLowerCase();
		ServiceVerification.PINVerificationService = pinVerificationService.toLowerCase();
	}

	/*@Value("${activation.service}")
	private String activationService;
	
	@PostConstruct
    public void init() {
        ServiceVerification.ActivationService = activationService;
    }*/
	
	public static Boolean isTransactionReq(String serviceName) throws Exception {
		return TransactionService.contains(serviceName.toLowerCase());
	}
	
	public static Boolean isMultiReqReq(String serviceName) throws Exception {
		return MultiReqService.contains(serviceName.toLowerCase());
	}
	
	public static Boolean isFinTrxReq(String serviceName) throws Exception {
		return FinTrxService.contains(serviceName.toLowerCase());
	}
	
	public static Boolean isPINVerificationReq(String serviceName) throws Exception {
		return PINVerificationService.contains(serviceName.toLowerCase());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
