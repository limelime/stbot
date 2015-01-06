package org.stbot.common.utils.test;

import org.stbot.common.utils.Chronometer;
import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;


public class ChronometerTest
{
  @Test
  public void testGetDiffInMillis()
  {
    Chronometer oChronometer = new Chronometer();
    
    long lElapsed = 10000;
    oChronometer.start();
    try
    {
      Thread.sleep(lElapsed);
    }
    catch(Exception ex)
    {
      System.out.println(ex.getMessage());
    }
    oChronometer.stop();
    
    long lTolerance=10;
    if(lElapsed+lTolerance >= oChronometer.getDiffInMillis() && oChronometer.getDiffInMillis()>=lElapsed-lTolerance)
    {
      assertTrue(true);
    }
    else
    {
      assertTrue(false, "Expected="+lElapsed+" +/-"+lTolerance+", Actual="+oChronometer.getDiffInMillis());
    }
  }
  
  @Test
  public void testGetDiffInString()
  {
    Chronometer oChronometer = new Chronometer();
    
    long lElapsed = 1000*65*1; // "00:01:05";
    final String sExpected = "00:01:05";
    
    oChronometer.start();
    try
    {
      Thread.sleep(lElapsed);
    }
    catch(Exception ex)
    {
      System.out.println(ex.getMessage());
    }
    oChronometer.stop();
    
    assertTrue(oChronometer.getDiffInString().indexOf(sExpected)!=-1, "Exepected="+sExpected+", Actual="+oChronometer.getDiffInString());
  }
}
