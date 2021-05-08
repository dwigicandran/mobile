package com.bsms.restobjclient.cardotp;

import java.util.List;

import com.bsms.domain.OtpNumTrx;
import com.bsms.restobj.MbApiContentResp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
public class ListOfNumTrx implements MbApiContentResp {

	public List<OtpNumTrx> content;

}
