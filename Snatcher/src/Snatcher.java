/*
 Program entry point. Snatcher is the main GUI window.
*/
import org.openqa.selenium.WebDriver;
import org.bytedeco.javacpp.opencv_core.Mat;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;


/**
 *
 * @author Khant
 */
public class Snatcher extends Application {
    
    private  final Button bbrowser = new Button("Start Browser");     
    private  final Button bfull = new Button("Full Capture");
    private  final Button bpartial = new Button("Partial Capture");
    private  WebDriver browser;

    
    
    public static void main(String[] args)  {
        
        //create directories before launching GUI
        createDirectoryStructure();
        //make sure chrome driver is registered
        WebController.registerWebDriver();
        launch(args);
    }
    
    
    
    @Override
    public void start(Stage stage) {
        
        registerBrowserStartAction();
        registerFullSnatcherAction();
        registerPartialSnatcherAction(stage);        
        setupGUI(stage);
    }
    
    
    // set up layout for the main GUI, buttons, etc..
    private void setupGUI(Stage stage) {        
        
        VBox vb = new VBox(15);
        vb.setAlignment(Pos.CENTER);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.getChildren().addAll(bbrowser, bfull, bpartial);
        
        bbrowser.setPrefHeight(50);        
        bfull.setMaxWidth(Double.MAX_VALUE);
        bfull.setPrefHeight(30);
        bpartial.setMaxWidth(Double.MAX_VALUE);
        bpartial.setPrefHeight(30);
        
        bfull.setDisable(true);
        bpartial.setDisable(true);
        
        Scene sc = new  Scene(vb,250,150);
        stage.setScene(sc);
        stage.show();
        stage.setResizable(false);
    }
    
    
    
    // Star Browser button
    private void registerBrowserStartAction() {   
        
        bbrowser.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {             
            bbrowser.setDisable(true);
            bfull.setDisable(false);
            bpartial.setDisable(false); 
            // open browser
            browser = WebController.getBrowser();
        });
    }
    
    
    
    // Full Capture button. Run FullSnatcher
    private void registerFullSnatcherAction() {
        
        bfull.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> { 
                            SnatcherInterface sncr = new FullSnatcher(browser, true);
                            Mat panorama = sncr.producePanorama();
                            new Thread(() -> sncr.savePanoramAsImage(panorama)).start();        
        });                    
    }
    
    
    // Partial Capture button. Run PartialSnatcher
    private void registerPartialSnatcherAction(Stage stage) {
        
        bpartial.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> { 
                    // assign to functional interface the task we want  SelectionWindow to execute
                    DelegatedTask task = (int[] d) -> {   
                        SnatcherInterface sncr = new PartialSnatcher(browser, d, true);
                        Mat panorama = sncr.producePanorama();
                        new Thread(() -> sncr.savePanoramAsImage(panorama)).start();
                    };
                    // start selection window for user to specify the area with rectangular window
                    SelectionWindow window = new SelectionWindow(task, stage );
                    window.show();
        });                    
    }
    
    
    // prepare directories for outputting files.
    private static void createDirectoryStructure() {        
        
        File output = new File(SnatcherInterface.OUTPUT_FOLDER);
        if (!output.isDirectory())
            output.mkdir();
        
        System.out.println(output.isDirectory());
        File parts = new File(SnatcherInterface.OUTPUT_FOLDER + "\\" + SnatcherInterface.PARTS_FOLDER);
        if (!parts.isDirectory())
            parts.mkdir();
    }
}
