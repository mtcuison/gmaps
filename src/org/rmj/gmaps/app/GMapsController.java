package org.rmj.gmaps.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.SQLUtil;

public class GMapsController {
    private GRider oApp;
    
    public GMapsController(GRider oApp) {
        this.oApp = oApp;
    }
    public void setRoute(boolean isUserRoute){
        pbRoute = isUserRoute;
    }
    @FXML
    private Button btnExit;

    @FXML
    private WebView sceneWeb;
    private WebEngine webengine;
    private boolean pbRoute = false;
    private boolean pbreverseGeoCode = false;
    
    private void toJSONArray() {
        String lsSQL = System.getProperty("app.sql.source");
        String lsGeoCoded = "";
        try {
            ResultSet loRS = oApp.executeQuery(lsSQL);
            JSONArray jsonArray = new JSONArray();

            while (loRS.next()) {                
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dTransact", loRS.getString("dTransact"));                
                jsonObject.put("sAddressx",reverseGeoCode(loRS.getDouble("nLatitude"),loRS.getDouble("nLongitud")) );
                //coordinates parsing
                JSONArray nCoordinateArray = new JSONArray();
                JSONArray coordinates = new JSONArray();
                coordinates.add(loRS.getDouble("nLatitude"));
                coordinates.add(loRS.getDouble("nLongitud"));
                nCoordinateArray.add(coordinates);

                jsonObject.put("nCoordinate", nCoordinateArray);
                jsonArray.add(jsonObject);
            }

            lsSQL = "SELECT sValuexxx FROM `xxxOtherConfig`" +
                    " WHERE sConfigID = 'websvr'" +
                        " AND sProdctID = " + SQLUtil.toSQL(oApp.getProductID());

            loRS = oApp.executeQuery(lsSQL);

            if (!loRS.next()){
                System.err.println("Web server is not set.");
                System.exit(1);
            }

            System.setProperty("system.websvr", loRS.getString("sValuexxx"));


            String jsonString = jsonArray.toJSONString();

            // You should pass the JSON string to the loadmap function
            loadmap(jsonString);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadmap(String jsonObject) {
        try {
            if (jsonObject.equals("[]")) System.exit(1);
                webengine = sceneWeb.getEngine();
                webengine.setJavaScriptEnabled(true);
//                webengine.load("http://192.168.10.69/integsys/gMaps/index.php");
                webengine.load(System.getProperty("system.websvr") + "integsys/gMaps/index.php");
                webengine.getLoadWorker().stateProperty().addListener((obs, oldValue, newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED) {
                        webengine.executeScript("receiveJsonFromJava(" + jsonObject + ", "+ pbRoute +");");
                        System.out.println("Success");
                    }
                });
        } catch (Exception ex) {
            System.err.println("error " + ex.getMessage());
            ex.printStackTrace();
        }
    }

  public static String reverseGeoCode(double lat, double lon){        
        //API NI MAYNARD
        String apiKey = "dFFTFYtJc0GyzeQBIh3VEjr4IamMhPos";
        String apiUrl = "https://www.mapquestapi.com/geocoding/v1/reverse?key=" + apiKey +
                "&location=" + lat + "%2C" + lon + "&outFormat=json&thumbMaps=false";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String inputLine;

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }

                            in.close();

                    
                     JSONParser parser = new JSONParser();
                     JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());


                // Extract street and city from the JSON response
                JSONArray resultsArray = (JSONArray) jsonResponse.get("results");
                if (resultsArray.size() > 0) {

                // Extract street and city from the JSON response
                JSONObject jsonResult = (JSONObject) resultsArray.get(0);
                JSONArray locationsArray = (JSONArray) jsonResult.get("locations");
                if (locationsArray.size() > 0) {
                    JSONObject jsonLocation = (JSONObject) locationsArray.get(0);
                    String street = (String) jsonLocation.get("street");
                    String city = (String) jsonLocation.get("adminArea5"); 
                    String locationInfo = street + ", " + city;
                    
//                    System.out.println ("Location :" +  locationInfo);
                    return locationInfo;
                    
                }
            }
        }

        } catch (IOException e) {
            throw new RuntimeException("Error fetching location information for (" + lat + ", " + lon + ")", e);
        } catch (ParseException ex) {
            Logger.getLogger(GMapsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null; 
    }
  
    @FXML
    private void cmdCancel_Click(ActionEvent event) {
        unloadScene();
    }

    private void unloadScene() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    public void initialize() {
        toJSONArray();
    }
}