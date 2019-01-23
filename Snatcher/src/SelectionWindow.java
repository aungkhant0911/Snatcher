import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;

import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Is used to capture an area of the screen.
 *
 * @author Khant
 */
public class SelectionWindow extends Stage {
    
    private DelegatedTask task;
    private Stage parentStage;
    private Label tip;
   
    
    Pane pane = new Pane();
    Canvas canvas = new Canvas();
    GraphicsContext gc = canvas.getGraphicsContext2D();
    Stage stage;

    int width;
    int height;

    int x1 = 0;
    int y1 = 0;
    int x2 = 0;
    int y2 = 0;

    Color foreground = Color.rgb(255, 167, 0);
    Color background = Color.rgb(0, 0, 0, 0.3);

   
    
    public SelectionWindow(DelegatedTask task, Stage parentStage) {
        
        this.task = task;
        this.parentStage = parentStage;
        //stage = parentStage;  
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        
        buildTips();

        setX(0);
        setY(0);
        setWidth(screenWidth);
        setHeight(screenHeight);
       //mm initOwner(parentStage);
        initStyle(StageStyle.TRANSPARENT);
        setAlwaysOnTop(true);
               
        pane.setStyle("-fx-background-color:rgb(0,0,0,0.05);");
        pane.getChildren().add(canvas); 
        
        setScene(new Scene(pane, Color.TRANSPARENT));        
        getScene().setCursor(Cursor.CROSSHAIR);
       
        canvas.setWidth(screenWidth);
        canvas.setHeight(screenHeight);
        
        gc.setLineDashes(5);
        gc.setFont(Font.font("null", FontWeight.BOLD, 14));
      
        registerDrawing();
        registerKeyBindings();
    }

    /**
     * Repaints the canvas *.
     */
    protected void repaintCanvas() {

        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.setStroke(foreground);
        gc.setFill(Color.TRANSPARENT);
        gc.setLineWidth(3);

        if (x2 > x1 && y2 > y1) { // Right and Down

            calculateWidthAndHeight(x2 - x1, y2 - y1);
            gc.strokeRect(x1, y1, width, height);
            gc.fillRect(x1, y1, width, height);

        } else if (x2 < x1 && y2 < y1) { // Left and Up

            calculateWidthAndHeight(x1 - x2, y1 - y2);
            gc.strokeRect(x2, y2, width, height);
            gc.fillRect(x2, y2, width, height);

        } else if (x2 > x1 && y2 < y1) { // Right and Up

            calculateWidthAndHeight(x2 - x1, y1 - y2);
            gc.strokeRect(x1, y2, width, height);
            gc.fillRect(x1, y2, width, height);

        } else if (x2 < x1 && y2 > y1) { // Left and Down

            calculateWidthAndHeight(x1 - x2, y2 - y1);
            gc.strokeRect(x2, y1, width, height);
            gc.fillRect(x2, y1, width, height);
        }

    }

    

    /**
     * Assign width and height of the selection
     
     */
    private final void calculateWidthAndHeight(int w, int h) {
        width = w;
        height = h;
    }

    

    /**
     Return array in this format (x,y,width,height) of the section window.
     */
    public int[] getDimension() {

        int starting_X_pos = Math.min(x1, x2);
        int starting_Y_pos = Math.min(y1, y2);
        
        int width_from_X_pos = Math.abs(Math.abs(x1) - Math.abs(x2));
        int width_from_Y_pos = Math.abs(Math.abs(y1) - Math.abs(y2));
        
        // +2/-2 and +5/-5 is so that we don't capture dotted boundary when using this SelectionWindow.
        // so we padd them.
        return new int[] { starting_X_pos, starting_Y_pos, width_from_X_pos, width_from_Y_pos};
    }
    
    
    /*
     Return array in this format (x,y,width,height) of the section window (excluding the frame/boundary).
     */
    public int [] getDimensionWithoutFrame() {
        
        int[] d = getDimension();
        return new int[] { d[0] + 2, d[1] + 2, d[2] - 5, d[3] - 5 };
    }
    
    /*
     Press C to capture, M to collapse the main GUI and ESCAPE to cancel the selection window
    */
    private void registerKeyBindings() {
        
        getScene().setOnKeyReleased(key -> {   
            
            if (key.getCode() == KeyCode.C) {                
                pane.getChildren().remove(1);
                resizeToWorkingArea();
                // give it sometimes to shrink the stage to selection window size to not
                // take up the whole screen.
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> {                       
                        task.executeTask(getDimensionWithoutFrame());                
                        close();
                });
                pause.play();
                
            } else if(key.getCode() == KeyCode.M) {
                // if the main GUI is hidden, brought it back. Otherwise hide it.
                parentStage.setIconified(!parentStage.isIconified());
                this.toFront();
                
            } else if(key.getCode() == KeyCode.ESCAPE)
                close();
        });
    }
    
    // register mouse events
    private void registerDrawing() {        
        
        canvas.setOnMousePressed(m -> {
            if(pane.getChildren().size() > 1)
                pane.getChildren().remove(1);
            // get the starting x,y position of selection window
            x1 = (int) m.getScreenX();
            y1 = (int) m.getScreenY();
        });
        
        
        canvas.setOnMouseDragged(m -> {
            x2 = (int) m.getScreenX();
            y2 = (int) m.getScreenY();
            // while dragging keep redrawing the selection window on the canvas
            repaintCanvas();
        });
               
        
        // Once done drawing, show the text in the middle of selection window.
         canvas.setOnMouseReleased(m -> {
            displayTips();
        });
    }
    
    
    // resize the transparent that is blocking the whole screnn to selection window size
    private void resizeToWorkingArea() {
        
        int[] dimension = getDimension();
        // move Pane (which hold canvas) to make it appears as if canvas and stage window are just fit
        // very smart move
        pane.relocate(-dimension[0], -dimension[1]);
        // now shrink down stage to fit selection window.
        this.setX(dimension[0]);
        this.setY(dimension[1]);
        this.setWidth(dimension[2]);
        this.setHeight(dimension[3]);
    }
    
    
    
    // show the press M...press C text in the middle of selection window.
    private void displayTips() { 
        // don't show the text if the label box is smaller than the selection window itself
        if(Math.abs(x1 - x2) < tip.getWidth() || Math.abs(y1 - y2) < tip.getHeight())
            return;
        
        if(pane.getChildren().size() <= 1)            
            pane.getChildren().add(tip);
        // this is kinda weird. You can't know the size of text/label box's size, untill they are renendred
        // In order to know its actual size before rendering, you need to put these two lines of code.
        // otherwise the size will always be 0. Something weired about JavaFX
        pane.applyCss();
        pane.layout();
       
        Label press_c = (Label) pane.getChildren().get(1); 
        int[] dimension = getDimension();
        // x_pos and y_pos determines the x and y of label box.
        double x_pos = dimension[0] + (dimension[2] / 2) - (press_c.getWidth() / 2);        
        double y_pos = dimension[1] + (dimension[3] / 2) - (press_c.getHeight() / 2);
        
        //make it appear in the middle of SelectionWIndow
        press_c.relocate( x_pos  , y_pos);
        //make it appear at the top of of SelectionWIndow
        //press_c.relocate( x_pos  , dimension[1] + 5); 
    }
    
    
    
    
    private void buildTips() {
        
        tip = new Label("Press C to capture\nPress M to minimize the GUI\nPress Esc to cancel");
        tip.setStyle("-fx-background-color: black");
        tip.setFont(Font.font("times new roman", 18));
        tip.setTextFill(Color.WHITE);
        tip.setTextAlignment(TextAlignment.CENTER);
    }
}