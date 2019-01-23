/*
    FullSnatcher is one mode of Snatcher, which capture the user selected area
    It conforms to SnatcherInterface contract
*/

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.util.List;
import java.util.ArrayList;

import static org.bytedeco.javacpp.opencv_core.Mat;

/**
 *
 * @author Khant
 */
public class PartialSnatcher extends FullSnatcher{
    // get (x,y, width, height) of user selected rectangular frame
    private int [] dimension;
    
    
    
    public PartialSnatcher(WebDriver browser, int[] dimension, boolean save_individual_images) {
        
        super(browser, save_individual_images);       
        this.dimension = dimension;
    }
    
       
    // same as FullSnatcher, but this time we use dimension info passed from SelectionWIndow as
    // capture_height, and use Robot image capturing feature.
    public List<BufferedImage> SnatchImages() {    
        
        int res;
        List<BufferedImage> imgs_list = new ArrayList<>(20);        
        JavascriptExecutor page = WebController.getJavaScriptController(browser);
        
        takeANap(2000);
        
        capture_height = dimension[3];
        fullpage_height = WebController.getPageHeight(page); 
        
        Robot robot = null;
        try{
            robot = new Robot();
        } catch(Exception e){
            System.err.println("Can't capture images!");
            System.exit(1);
        }
        
        BufferedImage img = robot.createScreenCapture(new Rectangle( dimension[0], dimension[1], dimension[2], dimension[3]));
        img = ImageUtility.convertBufferedCMYKToRGB(img);
        imgs_list.add(img);        
        
        while((res = (int) WebController.getScrollable(page)) > 0) {            
            
            residual_height = res;
            WebController.scrollByHeight(page, capture_height);
            takeANap(capture_speed);
            img = robot.createScreenCapture(new Rectangle( dimension[0], dimension[1], dimension[2], dimension[3]));    
            img = ImageUtility.convertBufferedCMYKToRGB(img);
            imgs_list.add(img);
            
        }        
        return imgs_list;
    }
}
