import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.scene.Cursor;
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
     * Calculates the width and height of the rectangle.
     *
     * @param w the w
     * @param h the h
     */
    private final void calculateWidthAndHeight(int w, int h) {
        width = w;
        height = h;
    }

    

    /**
     * Return an array witch contains the (UPPER_LEFT) Point2D of the rectangle
     * and the width and height of the rectangle.
     *
     * @return the int[]
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
    
    
    public int [] getDimensionWithoutFrame() {
        
        int[] d = getDimension();
        return new int[] { d[0] + 2, d[1] + 2, d[2] - 5, d[3] - 5 };
    }
    
    
    private void registerKeyBindings() {
        
        getScene().setOnKeyReleased(key -> {   
            
            if (key.getCode() == KeyCode.C) {
                
                pane.getChildren().remove(1);
                resizeToWorkingArea();
                
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> {
                       task.executeTask(getDimensionWithoutFrame());                
                       close();
                });
                pause.play();
                
                
            } else if(key.getCode() == KeyCode.M) {
                parentStage.setIconified(!parentStage.isIconified());
                this.toFront();
                
            } else if(key.getCode() == KeyCode.ESCAPE)
                close();
        });
    }
    
    
    private void registerDrawing() {        
        
        canvas.setOnMousePressed(m -> {
            if(pane.getChildren().size() > 1)
                pane.getChildren().remove(1);
            
            x1 = (int) m.getScreenX();
            y1 = (int) m.getScreenY();
        });
        
        
        canvas.setOnMouseDragged(m -> {
            x2 = (int) m.getScreenX();
            y2 = (int) m.getScreenY();
            repaintCanvas();
        });
               
        
         canvas.setOnMouseReleased(m -> {
            displayTips();
        });
    }
    
    
    private void resizeToWorkingArea() {
        
        int[] dimension = getDimension();
        
        pane.relocate(-dimension[0], -dimension[1]);
        this.setX(dimension[0]);
        this.setY(dimension[1]);
        this.setWidth(dimension[2]);
        this.setHeight(dimension[3]);
    }
    
    
    
    
    private void displayTips() { 
        
        if(Math.abs(x1 - x2) < tip.getWidth() || Math.abs(y1 - y2) < tip.getHeight())
            return;
        
        if(pane.getChildren().size() <= 1)            
            pane.getChildren().add(tip);
               
        pane.applyCss();
        pane.layout();
       
        Label press_c = (Label) pane.getChildren().get(1); 
        int[] dimension = getDimension();
        double x_pos = dimension[0] + (dimension[2] / 2) - (press_c.getWidth() / 2);        
        double y_pos = dimension[1] + (dimension[3] / 2) - (press_c.getHeight() / 2);
        
        //make it appear in the middle of SelectionWIndow
        press_c.relocate( x_pos  , y_pos);
        //make it appear on top of of SelectionWIndow
        //press_c.relocate( x_pos  , dimension[1] + 5); 
    }
    
    
    
    
    private void buildTips() {
        
        tip = new Label("Press C to capture\nPress M to minimize the GUI");
        tip.setStyle("-fx-background-color: black");
        tip.setFont(Font.font("times new roman", 18));
        tip.setTextFill(Color.WHITE);
        tip.setTextAlignment(TextAlignment.CENTER);
    }
}