package com.bsms.service;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.CardmappingRepository;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;

@Service("createPin")
public class MbCreatePinServiceImpl extends MbBaseServiceImpl implements MbService {

	@Autowired
	private MbAppContentRepository mbAppContentRepository;
	
    @Autowired
    private MbTxLogRepository txLogRepository;
    
    @Autowired
    private CardmappingRepository cardMappingRepository;
    
    MbApiResp response;
	
	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		
		MbApiTxLog txLog = new MbApiTxLog();
		txLogRepository.save(txLog);
		
		String pan = "";
		String customerId = request.getCustomer_id();
		
		try {
			
			Integer count = cardMappingRepository.getCountByCustomerId(customerId);
			System.out.println(count + " ::: count ");
			if(count > 0) {
				
			} else {
				
				pan = cardMappingRepository.getCardnumberByCustomerId(customerId);
				System.out.println(pan + " ::: pan");
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		return response;
	}

}
