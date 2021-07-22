package com.bsms.service.smscenter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbSmsCenter;
import com.bsms.repository.MbSmsCenterRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.smscenter.SmsCenter;
import com.bsms.restobjclient.smscenter.SmsCenterResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.service.transfer.GetListBankService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;

// By Muhammad Hadiansyah - 25 Maret 2021

@Service("listSmsCenter")
public class GetListSmsCenter extends MbBaseServiceImpl implements MbService {
	
	@Value("${sql.conf}")
    private String connectionUrl;
	
	@Autowired
	private MbSmsCenterRepository smsCenterRepo;
		
	MbApiResp mbApiResp;

    private static Logger log = LoggerFactory.getLogger(GetListBankService.class);

	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {

		try {
			
			// Connect to DB
			try(Connection con = DriverManager.getConnection(connectionUrl);) {
				
				// List for Data SMS Center
				List<SmsCenter> listSmsCenters = new ArrayList<>();
				
				// Inquiry All Data SMS Center
                List<MbSmsCenter> smsCenters = smsCenterRepo.findAll();
                
                // Mapping List Data SMS Center
                for (MbSmsCenter mbSmsCenter : smsCenters) {
					listSmsCenters.add(new SmsCenter(mbSmsCenter.getMsisdnPrefix(),
							mbSmsCenter.getProviderName(),mbSmsCenter.getSmsCenter()));
				}
                
                // Set Success Response
                SmsCenterResp smsCenterResp = new SmsCenterResp(listSmsCenters);
                mbApiResp = MbJsonUtil.createResponseBank("00", "Success", smsCenterResp);
				
			} catch (Exception e) {
				// Set DB Connection Failure
				mbApiResp = MbJsonUtil.createResponseBank("99", "List_SmsCenter(), Db Connection Error", null);
	            MbLogUtil.writeLogError(log, "List_SmsCenter(), Db Connection Error", MbApiConstant.NOT_AVAILABLE);
	            MbLogUtil.writeLogError(log, e, e.toString());
			}
			
		} catch (Exception e) {
			// Set System Error
			mbApiResp = MbJsonUtil.createResponseBank("99", "List_SmsCenter(), System Error", null);
            MbLogUtil.writeLogError(log, "List_SmsCenter(), System Error", MbApiConstant.NOT_AVAILABLE);
            MbLogUtil.writeLogError(log, e, e.toString());
		}
		
		// Return the Response
		return mbApiResp;
	}

}
