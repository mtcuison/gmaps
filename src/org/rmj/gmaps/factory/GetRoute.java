package org.rmj.gmaps.factory;

import org.rmj.appdriver.SQLUtil;

public class GetRoute {
    public static final String USER_COORDINATES = "SELECT" +
                                                        "  IFNULL(a.dTransact, '') dTransact" +
                                                        ", a.nLatitude" +
                                                        ", a.nLongitud" +
                                                    " FROM App_User_Coordinates a" +
                                                        ", App_User_Master b" +
                                                    " WHERE a.sUserIDxx = b.sUserIDxx" +
                                                        " AND a.cGPSEnbld = '1'" +
                                                        " AND b.sEmployNo = " + SQLUtil.toSQL(System.getProperty("app.employee.id")) +
                                                        " AND a.dTransact BETWEEN " + SQLUtil.toSQL(System.getProperty("app.date.from") + " 00:00:01") +
                                                            " AND " + SQLUtil.toSQL(System.getProperty("app.date.thru") + " 23:59:30") +
                                                    " HAVING nLatitude != 0.00 AND nLongitud != 0.00" +
                                                    " ORDER BY a.dTransact";
    
    public static final String SELFIE_COORDINATES = "SELECT" +
                                                        "  IFNULL(dLogTimex, '') dTransact" +
                                                        ", nLatitude" +
                                                        ", nLongitud" +
                                                    " FROM Employee_Log_Selfie" + 
                                                    " WHERE sEmployID = " + SQLUtil.toSQL(System.getProperty("app.employee.id")) +
                                                        " AND dLogTimex BETWEEN " + SQLUtil.toSQL(System.getProperty("app.date.from") + " 00:00:01") +
                                                            " AND " + SQLUtil.toSQL(System.getProperty("app.date.thru") + " 23:59:30") +
                                                    " HAVING nLatitude != 0.00 AND nLongitud != 0.00" +
                                                    " ORDER BY dLogTimex";
}
