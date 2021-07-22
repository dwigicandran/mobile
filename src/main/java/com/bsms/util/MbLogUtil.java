package com.bsms.util;

import org.slf4j.Logger;

public class MbLogUtil {

	public static final String LOG_TYPE_WARN = "WARN";
	public static final String LOG_TYPE_DEBUG = "DEBUG";
	public static final String LOG_TYPE_ERROR = "ERROR";
	
	/**
     * Currently use at messaging from Bancslink, and to 3rd party
     * <br>log.info
     * @param log
     * @param typeLog
     * @param msg custom Message
     * @param txId
     */
    public static void writeLogMsg(Logger log, String typeLog, String msg, String txId){
            StringBuilder sbLog = new StringBuilder();
            sbLog.append("\n  ").append(" [txid=").append(txId).append("] [").append(msg).append("]");
            if(typeLog.equalsIgnoreCase("INFO")){
                    log.info(sbLog.toString());
            }
            else if(typeLog.equalsIgnoreCase(LOG_TYPE_WARN)){
                    log.warn(sbLog.toString());
            }
            else if(typeLog.equalsIgnoreCase(LOG_TYPE_DEBUG)){
                    log.debug(sbLog.toString());
            }
            else if(typeLog.equalsIgnoreCase(LOG_TYPE_ERROR)){
                    log.error(sbLog.toString());
            } else {
                    sbLog = new StringBuilder();
                    sbLog.append("\n  ").append(typeLog).append(" [txid=").append(txId).append("] [").append(msg).append("]");
                    log.info(sbLog.toString());
            }
            
    }
    
    /**
     * Digunakan di adaptor, sebelum throw SlSocketException, SlCodexException, BP: createOtherFault
     * Print stack trace
     * @param log
     * @param ex
     * @param txId
     */
    public static void writeLogError(Logger log, Exception ex, String txId){
            //ex.printStackTrace();
            StringBuilder sbLog = new StringBuilder();
            sbLog.append("\n  [txid=").append(txId).append("] : ").append(ex.toString());
            log.error(sbLog.toString() + "\n___printStackTrace = ", ex);
    }
    
    /**
     * Creating log error with custom message
     * @param log
     * @param msg custom message
     * @param txId
     */
    public static void writeLogError(Logger log, String msg, String txId){
            StringBuilder sbLog = new StringBuilder();
            sbLog.append("\n  [txid=").append(txId).append("] : ").append(msg);
            log.error(sbLog.toString());
    }
    
    /**
     * Creating log AxisFault
     * use only at BP BaseBPServiceImpl: createSysFault, createBpFault
     * @param log
     * @param errorNum
     * @param errorMsg
     * @param minimumLevel
     * @param txId
     */
    public static void writeLogInfo(Logger log, String errorNum, String errorMsg, String minimumLevel, String txId){
            StringBuilder sbLog = new StringBuilder();
            sbLog.append("\n  AxisFault [txid=").append(txId).append("] : ");
            sbLog.append("\n    [error_num] = [").append(errorNum).append("]");
            sbLog.append("\n    [error_msg] = [").append(errorMsg).append("]");
            if (minimumLevel!=null && !"".equals(minimumLevel)){
                    sbLog.append("\n    [min_level] = [").append(minimumLevel).append("]");
            }
            
            log.info(sbLog.toString());
    }
	
}
