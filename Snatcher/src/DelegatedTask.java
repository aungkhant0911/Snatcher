/*
 This is a functional interface. Image capturing is  passed to Selection WIndow via this interface
 Its actual implementation can be found in Snatcher class's registerPartialCapture() method.
 */

/**
 *
 * @author Khant
 */
public interface DelegatedTask {
    
     public void executeTask(int [] dimention);
}
