package com.bsms.restobjclient.base;

import com.bsms.restobjclient.info.GetInfoListSetting;
import com.bsms.service.infocenter.GetListSetting;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class BaseResponse implements Serializable {
	private String transactionId;
    private String correlationId;
    private String responseCode;
    private String responseMessage;
    private String stan;
    private Object responseContent;
    private Object content;
    private String rc ;
    private List<GetInfoListSetting> list;
}
