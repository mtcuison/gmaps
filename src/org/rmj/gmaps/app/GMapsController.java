package org.rmj.gmaps.app;

import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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

    private void toJSONArray() {
        String lsSQL = System.getProperty("app.sql.source");

        try {
            ResultSet loRS = oApp.executeQuery(lsSQL);
            JSONArray jsonArray = new JSONArray();

            while (loRS.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dTransact", loRS.getString("dTransact"));

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
                            webengine.load("http://192.168.10.69/integsys/gMaps/index.php");
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