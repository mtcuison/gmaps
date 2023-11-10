package org.rmj.gmaps.test;

import org.rmj.gmaps.app.SelfieRoute;

public class testSelfieRoute {
    public static void main(String [] args){
        String param [] = new String [5];
        
        param[0] = "PetMgr";
        param[1] = "M001111122";
        param[2] = "M00110017110";
        param[3] = "2023-09-01";
        param[4] = "2023-10-19";
        
        SelfieRoute.main(param);
    }
}
