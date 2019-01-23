/*
    An interface to normalize different modes of Snatcher, full or partial.
*/

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream; 
import java.io.IOException;
import java.rmi.AccessException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import org.bytedeco.javacv.FrameFilter;


import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Khant
 */
public interface SnatcherInterface {
    
    // parent folder to store all outputs, jpg, pdf , etc..
    public static final String OUTPUT_FOLDER = "Output";
    // individiual images go here
    public static final String PARTS_FOLDER = "Parts";
    // name of final panorama image
    public static final String FILANAME = "pano";
    // how fast can we scroll and capture, in milliseconds
    public static final int capture_speed = 1000;
    
    
    // produce final panorama
    public Mat producePanorama();
    // produce a list of individual images
    public List<BufferedImage> SnatchImages();
    
    
    // default method to save final panorama
    public default void savePanoramAsImage(Mat panorama) {
        
        imwrite(OUTPUT_FOLDER + "\\" + FILANAME + ".jpg", panorama);
    }
    
    
    // A planned feature for future version.
    // produce images as a bundle .pdf as well
    public default void savePanoramAsPDF(Mat panorama) {
        
        try {
            throw new UnsupportedOperationException("This feature is not supported yet!");
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(SnatcherInterface.class.getName()).log(Level.INFO, null, ex);
        }
    }
}
