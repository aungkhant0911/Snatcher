/*
    FullSnatcher is one mode of Snatcher, which capture full webpage
    It conforms to SnatcherInterface contract
*/
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
/**
 *
 * @author Khant
 */
public class FullSnatcher implements SnatcherInterface{
    
    // store the currently opend browser
    protected WebDriver browser;
    // full height of the entire web document
    protected long fullpage_height;
    // residual_height ==  non-full height/ left-over height as a result of scrolling
    protected int capture_height, residual_height;
    // save individual images or not
    protected boolean save_parts;
    
    
         
    
    public FullSnatcher(WebDriver browser, boolean save_individual_images) {        
        
        this.browser = browser;
        capture_height = 0;
        fullpage_height = 0;
        residual_height = 0;
        save_parts = save_individual_images;
    }
    
    
    // main function which get all images, combine them to produce panorama and save them
    public Mat producePanorama(){
        
        List<BufferedImage> images = SnatchImages();
        Mat[] mats = convertImagesToMats(images);
        // save individual images
        if(save_parts)
            new Thread(() -> saveIndividualMatAsImage(mats)).start();
        
        return createPanorama(mats);
    }
    
    
    
    // scroll and capture images and convert them into a list of BufferedImages
    public List<BufferedImage> SnatchImages() {    
        
        int res;
        List<BufferedImage> imgs_list = new ArrayList<>(20);     // nothing fancy about 20, just a default size 
        // inject javascript engine to the page user is viewing
        JavascriptExecutor page = WebController.getJavaScriptController(browser);
        // give it some time. communication with webbrowser might take a while.
        takeANap(2000);
        
         
        capture_height = (int) WebController.getCaptureWindowHeight(page);
        fullpage_height = WebController.getPageHeight(page); 
        BufferedImage img = ImageUtility.convertCMYKToRGB(WebController.captureScreenshotOfCurrentView(page));
        imgs_list.add(img);
        
        // kee on scrolling and capturing if there is scrollable page left        
        while((res = (int) WebController.getScrollable(page)) > 0) {
            
            takeANap(500);
            residual_height = res;
            //scroll down by the given height
            WebController.scrollByHeight(page, capture_height);
            img = ImageUtility.convertCMYKToRGB(WebController.captureScreenshotOfCurrentView(page));
            imgs_list.add(img);
            takeANap(500);
        }        
        return imgs_list;
    }
    
    
    
    // concatinate into final panorama
    protected Mat createPanorama(Mat[] mats) {
       
        Mat panorama;
        
        // if the final residual_height (of final image) is less than 0 or greter than capture height,
        // it means the final scrollable height we know is not a multiple of capture_height
        // In that case the final image captured will have some overlap with the second last image.
        // So we need to do some math to cut out the overlapping area from the final image .
        // residual_height keeps track of non-overlapping, scrollable height, so use it.
        if(mats.length > 1 && residual_height < capture_height && residual_height > 0) {            
            Mat mat = mats[mats.length-1];
            assert mat.rows() == capture_height;
            // the magic is here, calculate the starting position of non-overlapping part "capture_height - residual_height"
            // then copy the non-overlapping part (from the poin to the end)
            mats[mats.length-1] = mat.rowRange(capture_height - residual_height, capture_height);
        }
         //imshow("Img 1", mats[mats.length-1]);
         //waitKey();
         
        // now we push each image onto the stack, from the first to last in that order, to concatinate vertically
        // has to clone, since we still want original images intact to be save to the disk.
        panorama = mats[0].clone();
        if(mats.length > 1) {
            for(int i = 1; i< mats.length; i++)
                panorama.push_back(mats[i]);
        }
        
        return panorama;
    }
    
    
    
    // save individual images    
    protected void saveIndividualMatAsImage( Mat[]  imgs) {
        
        int i = 1;        
        for(Mat img : imgs) {
            imwrite(OUTPUT_FOLDER + "\\" + PARTS_FOLDER + "\\" + FILANAME + i++ + ".jpg", img );
        }
    }
    
    
    
    
    // thread.sleep()
    protected void takeANap(int speed) {
        
        try {
            Thread.sleep(speed);            
        } catch (InterruptedException ex) {
            Logger.getLogger(FullSnatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    // conver to Mat for manipulation
    protected final Mat[] convertImagesToMats(final List<BufferedImage> imgs){        
        
        Mat[] mats = new Mat[imgs.size()];
        int i = 0;
        for(BufferedImage img : imgs)
            mats[i++] = ImageUtility.BufferedImageToMat(img);
        
        return mats;
    }
}
