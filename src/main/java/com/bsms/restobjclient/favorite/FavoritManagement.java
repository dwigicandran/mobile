package com.bsms.restobjclient.favorite;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoritManagement {
	private String id_favorit;
	private String billkey1;
	private String value;
	private String submodul_id;

	public FavoritManagement(String id_fav,String billkey1,String value,String submodul_id) {

				this.id_favorit=id_fav;
				this.billkey1=billkey1;
				this.value=value;
				this.submodul_id=submodul_id;

	}
}
