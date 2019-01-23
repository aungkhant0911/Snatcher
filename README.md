# About

  A multi-purpose image capturing and composition tool written in Java for capturing web pages into one large seamless panorama. There are browser-based plugin tools like Full Page Screen Capture chrome extension and many more available free or commercially, but I personally feel they are limiting in feature set and particularly not suited to my taste since I would like to continuously integrate a set of features available into the toolkit.

  I created this tool during my spare time in the senior year, partly due to my desire to expand my knowledge on Java language itself, and also because seeing my creation with that level of complexity comes into fruition brings such a joy. In addition, in this day and age where a massive data is available on the web, it would be nice to have a tool that allows you to gather that text data in the form of images, since they can be of a good use to image-to-text processing Deep Learning tasks that I have in mind. 



# Technologies Used

As a desktop application, there are a couple of technologies involved in this project:

- **JavaFX**
  - for GUI and area selection on webpage
  
- **JavaScript**
  - it is used as the main driving engine that allows to control many aspects of webpage
  
- **Selenium WebDriver**
  - an opencesource browser automation framework that allows to test and automate browser functionalities
  
- **OpenCV/JavaCV**
  - for stitching and composing images with a rich set of image processing features



# How It Works?

As the saying "A picture says a thousand words" goes, I would have you watch the application in action.

[![Snatcher](http://img.youtube.com/vi/yAPAQbPtAt4/0.jpg)](https://www.youtube.com/watch?v=yAPAQbPtAt4 "Snatcher")




# Requirements

- Java 8
- Make sure "Drivers" folder is intact with "chromedriver.exe" and "geckodriver.exe" inside.
- All the necessary library files for Selenium and OpenCV are supplied with the application. So no futher steps required!



# Known Bugs And A Note To User

- When using the browser initiated by the Snatcher:
  - Use the very first default tab to navigate and do the capturing. Closing the default tab means restarting the browser.
  - Currently more than one tab is not supported. The application will only use the first tab.
  
- Though Partial Snatcher allows user to leave focus off the application during capturing process, it may leave black selection         window, resulting in subsequent captured images as pitch black.



# Future Release

The following are intended in the future releases in no particular order.

- Bug fixes

- Support for PDF version of pano

- Image-to-Text translation for PDF version rather than just converting images to PDF (useful for datamining web for DeepLearning)

- Support for parallel multiple tab capturing for full page capturing

- Queueing multiple work orders for full page capturing

- Support for feature to select and supress/delete away blocking or annoying webelement/div tag/iframes etc.. from a page
  - This feature is similar to Inspect Element of major browsers ( right-click on a page). But it is non-programmer friendly. The version of this feature supported will be as simple as point and click for non tech-savy users.
