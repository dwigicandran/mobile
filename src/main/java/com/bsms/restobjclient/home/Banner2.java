package com.bsms.restobjclient.home;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Banner2 {
	private String img;
	private String title;
	private String tgl;
	private String detail;
	private String submodul_id;
	private String category;
	
	public Banner2(String img,String title,String tgl,String detail,String submodul_id,String category) {
				
				this.img=img;
				this.title=title;
				this.tgl=tgl;
				this.detail=detail;
				this.submodul_id=submodul_id;
				this.category=category;
			
	}
}
