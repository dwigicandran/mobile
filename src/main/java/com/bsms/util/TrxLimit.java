package com.bsms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

public class TrxLimit {

    public static Integer INSERT_LT = 0;
    public static Integer UPDATE_LT = 1;

    public static Integer TRANSFER = 0;
    public static Integer TRANSFER_ONLINE = 3;
    public static Integer TRANSFER_SKN = 4;
    public static Integer TRANSFER_RTGS = 5;
    public static Integer TRANSFER_CASH = 6;
    public static Integer CW = 7;
    public static Integer EMONEY = 8;
    public static Integer PURCHASE = 1;
    public static Integer PAYMENT = 2;
    public static Integer QRIS = 10;

    public String checkLimit(String msisdn, Integer customerType, Integer trxType, Long trxAmount, JSONObject value, String connectionUrl) throws Exception {
        String result = "99";

        ResultSet rs, rs2;
        Statement stmt, stmt2;
        String SQL, SQL2;

        Long trxAmtLimit = (long) 0;
        Long dailyAmtLimit = (long) 0;

        try (Connection con = DriverManager.getConnection(connectionUrl);) {
            stmt = con.createStatement();
            SQL = "select * from mb_limit where customer_type=" + customerType + " and trx_type=" + trxType + " and enabled=1";
            rs = stmt.executeQuery(SQL);

            if (rs.next()) {
                trxAmtLimit = rs.getLong("trx_amount_limit");
                dailyAmtLimit = rs.getLong("daily_amount_limit");
                result = "00";
            } else {
                result = "01";
            }
            rs.close();
            stmt.close();

            if ("00".equals(result)) {
                // Get the trx tracking
                Calendar calTrxDate = Calendar.getInstance();
                Date trxDate = calTrxDate.getTime();
                Long lastAmount = (long) 0;
                Integer action;

                stmt2 = con.createStatement();
                SQL2 = "select * from mb_limit_tracking where msisdn='" + msisdn + "' and trx_type=" + trxType + "";
                rs2 = stmt2.executeQuery(SQL2);

                if (rs2.next()) {
                    Calendar calLastTrxDate = Calendar.getInstance();
                    calLastTrxDate.setTime((Date) rs2.getObject("last_trx_date"));
                    if (calLastTrxDate.get(Calendar.DATE) == calTrxDate.get(Calendar.DATE) &&
                            calLastTrxDate.get(Calendar.MONTH) == calTrxDate.get(Calendar.MONTH) &&
                            calLastTrxDate.get(Calendar.YEAR) == calTrxDate.get(Calendar.YEAR)) {
                        lastAmount = rs2.getLong("total_amount");
                    } else {
                        lastAmount = (long) 0;
                    }
                    //action = UPDATE_LT;
                } else {
                    lastAmount = (long) 0;
                    //action = INSERT_LT;
                }

                if (trxAmtLimit > 0) {
                    if (trxAmount <= trxAmtLimit) {
                        result = "00";
                    } else {
                        result = "02";
                    }
                } else {
                    result = "00";
                }

                if ("00".equals(result)) {
                    if (dailyAmtLimit > 0) {
                        if ((trxAmount + lastAmount) <= dailyAmtLimit) {
                            result = "00";
                        } else {
                            result = "02";
                        }
                    } else {
                        result = "00";
                    }
                }
//                System.out.println("Transaction Amount : " + trxAmount);
//                System.out.println("Last Amount : " + lastAmount);
//                System.out.println("Daily Limit : " + dailyAmtLimit);
//                System.out.println("Transaction Amount Limit : " + trxAmtLimit);
                rs2.close();
                stmt2.close();

            }

            con.close();


        } catch (SQLException e) {
            System.out.println(e.toString());
            result = "99";

        }


        return result;
    }

    public String LimitUpdate(String msisdn, Integer customerType, Integer trxType, Long trxAmount, JSONObject value, String connectionUrl) throws Exception {
        String result = "99";

        ResultSet rs2;
        Statement stmt2;
        String SQL2;

        try (Connection con = DriverManager.getConnection(connectionUrl);) {

            // Get the trx tracking
            Calendar calTrxDate = Calendar.getInstance();
            Date trxDate = calTrxDate.getTime();
            Long lastAmount;
            Integer action;

            stmt2 = con.createStatement();
            SQL2 = "select * from mb_limit_tracking where msisdn='" + msisdn + "' and trx_type=" + trxType + "";
            rs2 = stmt2.executeQuery(SQL2);

            if (rs2.next()) {
                Calendar calLastTrxDate = Calendar.getInstance();
                calLastTrxDate.setTime((Date) rs2.getObject("last_trx_date"));
                if (calLastTrxDate.get(Calendar.DATE) == calTrxDate.get(Calendar.DATE) &&
                        calLastTrxDate.get(Calendar.MONTH) == calTrxDate.get(Calendar.MONTH) &&
                        calLastTrxDate.get(Calendar.YEAR) == calTrxDate.get(Calendar.YEAR)) {
                    lastAmount = rs2.getLong("total_amount");
                } else {
                    lastAmount = (long) 0;
                }
                action = UPDATE_LT;
                updateLimitTracking(msisdn, trxType, trxAmount, lastAmount, trxDate, action, connectionUrl);
            } else {
                lastAmount = (long) 0;
                action = INSERT_LT;
                updateLimitTracking(msisdn, trxType, trxAmount, lastAmount, trxDate, action, connectionUrl);
            }

        } catch (SQLException e) {
            System.out.println(e.toString());
            result = "99";

        }


        return result;
    }

    public void updateLimitTracking(String msisdn, Integer trxType, Long trxAmount, Long lastAmount, Date trxDate, Integer action, String connectionUrl) throws Exception {

        Long total_amount = trxAmount + lastAmount;

        try {
            if (action == INSERT_LT) {
                try (Connection con = DriverManager.getConnection(connectionUrl);) {
                    Statement stmt;
                    String SQL;

                    stmt = con.createStatement();
                    SQL = "insert into mb_limit_tracking(msisdn, trx_type, last_trx_date, total_amount) values('" + msisdn + "', " + trxType + ", "
                            + "'" + new Timestamp(trxDate.getTime()) + "', " + total_amount + ")";
                    int result = stmt.executeUpdate(SQL);

                    if (result == 1) {
                        System.out.println("::: Insert Limit Tracking Success :::");
                    } else {
                        System.out.println("::: Insert Limit Tracking Failed :::");
                    }

                    stmt.close();
                    con.close();

                } catch (SQLException e) {

                    System.out.println(e.toString());

                }

            } else if (action == UPDATE_LT) {
                try (Connection con = DriverManager.getConnection(connectionUrl);) {
                    Statement stmt;
                    String SQL;

                    stmt = con.createStatement();
                    SQL = "update mb_limit_tracking set last_trx_date='" + new Timestamp(trxDate.getTime()) + "', "
                            + "total_amount=" + total_amount + " where msisdn='" + msisdn + "' and trx_type=" + trxType + "";
                    int result = stmt.executeUpdate(SQL);

                    if (result == 1) {
                        System.out.println("::: Update Limit Tracking Success :::");
                    } else {
                        System.out.println("::: Update Limit Tracking Failed :::");
                    }

                    stmt.close();
                    con.close();

                } catch (SQLException e) {

                    System.out.println(e.toString());

                }

            }

        } catch (Exception e) {

            System.out.println(e.toString());

        }

    }


    //addition By Dwi Sutrisno
    //December 2020
    public static String checkTransLimit(String amount, int customerLimitType, String msisdn, int trxType, String connectionUrl) {
        String limitResponseCode = "99";
        TrxLimit trxLimit = new TrxLimit();
        JSONObject value = new JSONObject();

        try {
            double pdamAmount = Double.parseDouble(amount); //transaction amount
            long amount_convert = (new Double(pdamAmount)).longValue(); //129
            limitResponseCode = trxLimit.checkLimit(msisdn, customerLimitType, trxType, amount_convert, value, connectionUrl);
        } catch (Exception e) {
            System.out.println("Limit Check Error :" + e.getMessage());
        }

        return limitResponseCode;
    }

}
