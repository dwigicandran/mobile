package com.bsms.restobjclient.cardotp;

import java.util.List;

import com.bsms.domain.OtpNumTrx;
import com.bsms.restobj.MbApiContentResp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListOfNumTrx implements MbApiContentResp {

	public List<OtpNumTrx> content;

}
