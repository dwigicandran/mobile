package com.bsms.util;

import com.bsms.restobj.MbApiStatusResp;

public class MbErrorUtil {

	public static MbApiStatusResp[] createError(String errCode, String errDescr){
        return new MbApiStatusResp[]{new MbApiStatusResp(errCode, errDescr)};
    }

}
