package org.stbot.common.utils.test;

import org.stbot.common.utils.SimpleLinearRegression;

/**
 * Test cases of SimpleLinearRegression class
 * @author Xuan Ngo
 */
import java.util.ArrayList;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
 
public class SimpleLinearRegressionTest
{
  @Test
  public void slrPositiveSlopeTest()
  {
    ArrayList<Double> aX = new ArrayList<Double>();
    ArrayList<Double> aY = new ArrayList<Double>();
    aX.add(new Double(0.0));  aY.add(new Double(4.0));
    aX.add(new Double(3.0));  aY.add(new Double(8.0));
 
    SimpleLinearRegression slr = new SimpleLinearRegression(aX, aY);
 
    assertEquals(slr.getSlope(), (8.0-4.0)/(3.0-0.0));
    assertEquals(slr.getIntercept(), 4.0);
  }
 
  @Test
  public void slrNegativeSlopeTest()
  {
    ArrayList<Double> aX = new ArrayList<Double>();
    ArrayList<Double> aY = new ArrayList<Double>();
    aX.add(new Double(0.0));  aY.add(new Double(8.0));
    aX.add(new Double(3.0));  aY.add(new Double(4.0));
 
    SimpleLinearRegression slr = new SimpleLinearRegression(aX, aY);
 
    assertEquals(slr.getSlope(), (4.0-8.0)/(3.0-0.0));
    assertEquals(slr.getIntercept(), 8.0);
  }  
}