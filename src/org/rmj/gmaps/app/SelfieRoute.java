package org.rmj.gmaps.app;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.rmj.appdriver.GRider;
import org.rmj.gmaps.factory.GetRoute;

public class SelfieRoute extends Application{
    public static String pdTransact;
    public static GRider poGRider;
    public static String psEmployeeID;   
    private double xOffset = 0; 
    private double yOffset = 0;
    
    public String getdTransact(){
        return pdTransact;
    }
     public String getdEmployeeId(){
        return psEmployeeID;
    }
    
    @Override
    public void start(Stage stage) throws Exception {       

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("GMaps.fxml"));
        
        GMapsController controller = new GMapsController(poGRider);

        fxmlLoader.setController(controller);
        controller.setRoute(false);
        Parent parent = fxmlLoader.load();
        

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setAlwaysOnTop(true);
        dialogStage.initOwner(stage); 
        
        parent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dialogStage.setX(event.getScreenX() - xOffset);
                dialogStage.setY(event.getScreenY() - yOffset);
            }
        });
        
        Scene scene = new Scene(parent);
        dialogStage.setScene(scene);
        dialogStage.setX(800);
        dialogStage.setY(650);
        dialogStage.setWidth(800);
        dialogStage.setHeight(650);
        dialogStage.centerOnScreen();
        dialogStage.showAndWait();
    }
    
    public static void main(String [] args){
        if (args.length != 5) System.exit(1);
        
        String path;
        
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Java_Systems";
        }
        else{
            path = "/srv/GGC_Java_Systems";
        }
        
        System.setProperty("sys.default.path.config", path);
        
        poGRider = new GRider(args[0]);
        
        System.setProperty("app.employee.id", args[2]);
        System.setProperty("app.date.from", args[3]);
        System.setProperty("app.date.thru", args[4]);
        
        if (!poGRider.loadEnv(args[0])) System.exit(1);
        if (!poGRider.loadUser(args[0], args[1])) System.exit(1);

        System.setProperty("app.sql.source", GetRoute.SELFIE_COORDINATES);
        
        launch(args);
    }
}
