/*
    This class controll all aspects of activities on webbrowser, from injecting javacript
    to scroll page, take page wide snapshot of a site, starting browser and manipulating it.
    Selenium WebDriver automation framework is used to control a browser,
    either via chrome or gecko firefox driver.
*/
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;


/**
 *
 * @author Khant
 */
public class WebController {
    // a driver exe file must reside in this folder, "Drivers"
    public static final String DRIVER_FOLDER = "Drivers";
    // firefox driver name
    private static final String FIREFOX_DRIVER = "webdriver.gecko.driver";
    // chrome driver name
    private static final String CHROME_DRIVER = "webdriver.chrome.driver";
    
    
    
    public static void registerWebDriver() {
        try {
            //System.setProperty(FIREFOX_DRIVER , DRIVER_FOLDER + "\\geckodriver.exe");
            // need to register the driver before invoking the respective browser
            System.setProperty(CHROME_DRIVER , DRIVER_FOLDER + "\\chromedriver.exe");            
        } catch(Exception e) {            
            System.out.println("Drivers missing!! Please ensure 'geckodriver.exe' "
                    + "and 'chromedriver.exe' exist in the path " + new File("").getAbsolutePath()
                    + "\\" + DRIVER_FOLDER);
        }
    }        
    
    
    // start chrome browser
    public static final WebDriver getBrowser() {
        //return new FirefoxDriver();
        ChromeOptions options = new ChromeOptions();
        // just disabling the annoying noti bar
        options.addArguments("disable-infobars"); 
        
        return new ChromeDriver(options);
    }    
    
    
    // a utility function to inject java script to the current page
    public static final JavascriptExecutor getJavaScriptController(WebDriver browser) {
        // browser.get() inject javascriptExecutor to the current webpage user is viewing
        // WARNING: browser.getCurrentURL() doesn't work with multiple tab, at least without
        // changing the current code. User must be advised to use ONLY one page.
        browser.get(browser.getCurrentUrl());
        return (JavascriptExecutor) browser;
    }    
    
    
    // scroll webpage vertically by capture_height ammount using javascript.
    public static final void scrollByHeight(JavascriptExecutor js, long capture_height) {
       // js.executeScript("setTimeout(new Date().getHours(), 1000)");
        js.executeScript(String.format("scrollBy(0,%d)", capture_height));
    }   
    
    
    // get the entire height of a webpage.
    public static final long getPageHeight(JavascriptExecutor js) {
        return (long) js.executeScript("return document.body.scrollHeight");
    }    
    
    
    //  get height of the visible view port of a tab.
    public static final long getCaptureWindowHeight(JavascriptExecutor js) {
        return (long) js.executeScript("return window.innerHeight");
    }    
    
    
    // get scrollable height so far.
    public static final long getScrollable(JavascriptExecutor js) {
        //js.executeScript("setTimeout(new Date().getHours(), 500)");
        return (long) js.executeScript("return  document.body.scrollHeight - (window.innerHeight + window.scrollY)");
    }
    
    
    // This is image capturing feature provided by selenium for webpage.
    // it captures the current visible area/ view port of a tab.
    public static final byte[] captureScreenshotOfCurrentView(JavascriptExecutor js) {
       // js.executeScript("setTimeout(new Date().getHours(), 1000)");
        return ((TakesScreenshot) js).getScreenshotAs(OutputType.BYTES);
    }    
}
