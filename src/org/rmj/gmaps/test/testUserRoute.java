package org.rmj.gmaps.test;

import org.rmj.gmaps.app.UserRoute;

public class testUserRoute {
    public static void main(String [] args){
        String param [] = new String [5];
        
        param[0] = "IntegSys";
        param[1] = "M001111122";
        param[2] = "M00123001103";
        param[3] = "2023-10-11";
        param[4] = "2023-10-11";
        
        UserRoute.main(param);
    }
}
