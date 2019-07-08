/*
 Program entry point. Snatcher is the main GUI window.
*/
import org.openqa.selenium.WebDriver;
import org.bytedeco.javacpp.opencv_core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.openqa.selenium.JavascriptExecutor;


/**
 *
 * @author Khant
 */
public class Snatcher extends Application {
    
    private  final Button bbrowser = new Button("Start Browser");     
    private  final Button bfull = new Button("Full Capture");
    private  final Button bpartial = new Button("Partial Capture");
    private  final Button binspection = new Button("Activate Inspection");
    private  static WebDriver browser;

    
    
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
        registerInspectionAction();
        setupGUI(stage);
    }
    
    
    // set up layout for the main GUI, buttons, etc..
    private void setupGUI(Stage stage) {        
        
        VBox vb = new VBox(15);
        vb.setAlignment(Pos.CENTER);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.getChildren().addAll(bbrowser, bfull, bpartial, binspection);
        
        bbrowser.setPrefHeight(50);        
        bfull.setMaxWidth(Double.MAX_VALUE);
        bfull.setPrefHeight(30);
        bpartial.setMaxWidth(Double.MAX_VALUE);
        bpartial.setPrefHeight(30);
        binspection.setMaxWidth(Double.MAX_VALUE);
        binspection.setPrefHeight(30);
        
        binspection.setTooltip(new Tooltip("Once the Inspection feature is activated on a webpage, bring page to focus:" +
                                           "\n1) Press 'Q' to resume/suspend the feature." +
                                           "\n2) Press 'E' to delete away a web element"));
        
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
                    new Thread(() -> {
                        SnatcherInterface sncr = new PartialSnatcher(browser, d, true);
                        Mat panorama = sncr.producePanorama();
                        sncr.savePanoramAsImage(panorama);  
                    }).start();
                };
                // start selection window for user to specify the area with rectangular window
                SelectionWindow window = new SelectionWindow(task, stage );
                window.show();
        });                    
    }
    
    
    
    private void registerInspectionAction() {
              
        String inspection_code = WebController.concatJSfiles(new String[]{"Javascripts\\animation.js", "Javascripts\\inspection.js"});
        // create variable name for JavaScript, so that we can check later if a page is already injected with JS code by examining the existence of this variable
        String variable =  "AuthoredByAungKhant";
        
        binspection.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
               JavascriptExecutor injector = WebController.getJavaScriptController(browser);
               if((boolean) injector.executeScript(String.format("return typeof window.%s === 'undefined';", variable))) {
                   System.out.println("Not injected yet!");
                   injector.executeScript(String.format("window.%s = 2019;", variable));
                   injector.executeScript(inspection_code);
               }
               else
                   System.out.println("Already injected !");
        });
    }
    
    
    // prepare directories for outputting files.
    private static void createDirectoryStructure() {        
        
        File output = new File(SnatcherInterface.OUTPUT_FOLDER);
        if (!output.isDirectory())
            output.mkdir();
        
        File parts = new File(SnatcherInterface.OUTPUT_FOLDER + "\\" + SnatcherInterface.PARTS_FOLDER);
        if (!parts.isDirectory())
            parts.mkdir();
    }
}
