/*
An image utility class for stitching, concatination, matrix manipulation and color scheme conversion
*/
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGCodec;

import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_core.Rect;
import static org.bytedeco.javacpp.opencv_core.MatVector;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_stitching.Stitcher;
/**
 *
 * @author Khant
 */
public class ImageUtility {
    
    // Color conversion function. If you are going to read images from source such as Robot class's screenCapture,
    // ImageIO.read() or Selenium, chances are you will need to convert to RGB compatible colors.
    // Without converting to RGB will end up with incorrect color output.
    public static final BufferedImage convertCMYKToRGB(final byte[] img) {         
        
        BufferedImage originalImg = null, finalImg = null;        
        try {  
            originalImg = ImageIO.read(new ByteArrayInputStream(img));
        } catch (IOException ex) {
            Logger.getLogger(ImageUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        // TYPE_3BYTE_BGR is the correct value.
        finalImg = new BufferedImage(originalImg.getWidth(), originalImg.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        // just filter the color channel and paint on top.
        ColorConvertOp op = new ColorConvertOp(null);
        op.filter(originalImg, finalImg);
        
        return finalImg;
    }
    
    
    
    // exact same function as convertCMYKToRGB func. This is for BufferedImage type param.
    public static final BufferedImage convertBufferedCMYKToRGB(BufferedImage img) {         
        
        BufferedImage finalImg = null; 
        finalImg = new BufferedImage(img.getWidth(), img.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        ColorConvertOp op = new ColorConvertOp(null);
        op.filter(img, finalImg);
        
        return finalImg;
    }
    
    
    
    // Convert BufferedImage to OpenCV's 2D Matrix for inuitive manipulation.
    public static final Mat BufferedImageToMat(BufferedImage img){  
            
        OpenCVFrameConverter.ToIplImage cv = new OpenCVFrameConverter.ToIplImage();         
        return cv.convertToMat(new Java2DFrameConverter().convert(img));
    }
    
        
    
    // Might as well make this a function in case I need it.
    // if 2 images have overllaping portion, Combine the two as one complete panorama.
    // Stitcher is part of OpenCV.
   public static final Mat stitchImages(Mat img1, Mat img2, boolean gpu_enable)  {
       
       Mat panorama = new Mat();
       MatVector mv = new MatVector();             
       mv.put(new Mat[]{img1,img2});
       
       imshow("1",img1);
       imshow("2", img2);
       waitKey();
       
       Stitcher stitcher = Stitcher.createDefault(gpu_enable);
       
        if (stitcher.stitch(mv, panorama) != Stitcher.OK) {            
            System.out.println("Error Occured! Can't stitch images.");
            System.exit(-1);
        }    
        
        System.out.println("Stitching successful!");
        return panorama;
   }
   
   
   // save a list of images of bytes to the disk.
   // I never really had to use this function, partly because images have to be converted to Mat for some modification before saving,
   // rather than directly saving raw bytes.
   public static final void saveIndividualImages( List<byte[]>  files, String path, String filename, String fileformat) {
        
        int i = 1;        
        for(byte[] file : files) {            
            try {                
                 FileOutputStream fout = new FileOutputStream(path + "\\" + filename + i + fileformat);
                 BufferedOutputStream bf = new BufferedOutputStream(fout);
                 bf.write(file);
                 bf.flush();
                 bf.close();
                 i++;
                 
            } catch (Exception e) {
                System.err.println("Error occured while trying to save files!");
            }
        }
    }
}