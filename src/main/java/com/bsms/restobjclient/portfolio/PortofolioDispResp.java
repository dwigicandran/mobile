package com.bsms.restobjclient.portfolio;

import java.util.List;

import com.dto.accountlist.ListOfAccount;
import com.dto.portofolio.ListOfPortofolio;

public class PortofolioDispResp {

	private String responseCode;
	private List<ListOfPortofolio> listOfPortofolio;
	
	
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public List<ListOfPortofolio> getListOfPortofolio() {
		return listOfPortofolio;
	}
	public void setListOfPortofolio(List<ListOfPortofolio> listOfPortofolio) {
		this.listOfPortofolio = listOfPortofolio;
	}
	
}
