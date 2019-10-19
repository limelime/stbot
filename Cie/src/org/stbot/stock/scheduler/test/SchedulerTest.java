package org.stbot.stock.scheduler.test;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.stbot.common.utils.Constant;
import org.stbot.stock.scheduler.Prices;
import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;


@Test(sequential=true)
public class SchedulerTest 
{
  @Test(description="Test case: before 19:00:00 on weekdays.")
  public void testGetNextTimeAt_BeforeHour() 
  {
    /**
     *  Test case: Before 19:00:00.
     */
    String sExpectedDateTime  = "2010-02-05 19:00:00";
    String sInputDateTime     = "2010-02-05 18:59:59";
    final int iHour = 19;
    String sActual = "";
    try
    {
      sActual = this._testGetNextTimeAt(iHour, sInputDateTime);
    }
    catch(NoSuchMethodException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(IllegalAccessException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(InvocationTargetException ex)
    {
      System.out.println(ex.getMessage());
    }    
    assertTrue(sActual.indexOf(sExpectedDateTime)!=-1, "Expected="+sExpectedDateTime+", Actual="+sActual);    
  }
  
  @Test(description="Test case: on 19:00:00 on weekdays.")
  public void testGetNextTimeAt_OnHour() 
  {
    /**
     *  Test case: On 19:00:00.
     */
    String sExpectedDateTime  = "2010-02-05 19:00:00";
    String sInputDateTime     = "2010-02-05 19:00:00";
    final int iHour = 19;
    String sActual = "";
    try
    {
      sActual = this._testGetNextTimeAt(iHour, sInputDateTime);
    }
    catch(NoSuchMethodException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(IllegalAccessException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(InvocationTargetException ex)
    {
      System.out.println(ex.getMessage());
    }    
    assertTrue(sActual.indexOf(sExpectedDateTime)!=-1, "Expected="+sExpectedDateTime+", Actual="+sActual);    
  }
  
  @Test(description="Test case: after 19:00:00 on weekdays.")
  public void testGetNextTimeAt_AfterHour()
  {
    /**
     *  Test case: After 19:00:00.
     */
    String sExpectedDateTime  = "2010-02-05 19:00:00";
    String sInputDateTime     = "2010-02-04 19:00:01";
    final int iHour = 19;
    String sActual = "";
    try
    {
      sActual = this._testGetNextTimeAt(iHour, sInputDateTime);
    }
    catch(NoSuchMethodException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(IllegalAccessException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(InvocationTargetException ex)
    {
      System.out.println(ex.getMessage());
    }    
    assertTrue(sActual.indexOf(sExpectedDateTime)!=-1, "Expected="+sExpectedDateTime+", Actual="+sActual);    
  }
  
  @Test(description="Test case: After 19:00:00 and next time is on Saturday")
  public void testGetNextTimeAt_AfterOnSaturday()
  {
    String sExpectedDateTime  = "2010-02-08 19:00:00";
    String sInputDateTime     = "2010-02-05 19:00:01";
    final int iHour = 19;
    String sActual = "";
    try
    {
      sActual = this._testGetNextTimeAt(iHour, sInputDateTime);
    }
    catch(NoSuchMethodException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(IllegalAccessException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(InvocationTargetException ex)
    {
      System.out.println(ex.getMessage());
    }    
    assertTrue(sActual.indexOf(sExpectedDateTime)!=-1, "Expected="+sExpectedDateTime+", Actual="+sActual);    
  }
  
  @Test(description="Test case: Before 19:00:00 and next time is on Saturday")
  public void testGetNextTimeAt_BeforeOnSaturday()
  {
    String sExpectedDateTime  = "2010-02-08 19:00:00";
    String sInputDateTime     = "2010-02-06 18:59:59";
    final int iHour = 19;
    String sActual = "";
    try
    {
      sActual = this._testGetNextTimeAt(iHour, sInputDateTime);
    }
    catch(NoSuchMethodException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(IllegalAccessException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(InvocationTargetException ex)
    {
      System.out.println(ex.getMessage());
    }    
    assertTrue(sActual.indexOf(sExpectedDateTime)!=-1, "Expected="+sExpectedDateTime+", Actual="+sActual);    
  }
  
  @Test(description="Test case: On 19:00:00 and next time is on Saturday")
  public void testGetNextTimeAt_OnSaturday()
  {
    String sExpectedDateTime  = "2010-02-08 19:00:00";
    String sInputDateTime     = "2010-02-06 19:00:00";
    final int iHour = 19;
    String sActual = "";
    try
    {
      sActual = this._testGetNextTimeAt(iHour, sInputDateTime);
    }
    catch(NoSuchMethodException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(IllegalAccessException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(InvocationTargetException ex)
    {
      System.out.println(ex.getMessage());
    }    
    assertTrue(sActual.indexOf(sExpectedDateTime)!=-1, "Expected="+sExpectedDateTime+", Actual="+sActual);    
  }  
  @Test(description="Test case: After 19:00:00 Sunday")
  public void testGetNextTimeAt_AfterSunday()
  {
    String sExpectedDateTime  = "2010-02-08 19:00:00";
    String sInputDateTime     = "2010-02-07 19:00:01";
    final int iHour = 19;
    String sActual = "";
    try
    {
      sActual = this._testGetNextTimeAt(iHour, sInputDateTime);
    }
    catch(NoSuchMethodException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(IllegalAccessException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(InvocationTargetException ex)
    {
      System.out.println(ex.getMessage());
    }    
    assertTrue(sActual.indexOf(sExpectedDateTime)!=-1, "Expected="+sExpectedDateTime+", Actual="+sActual);    
  }
  
  @Test(description="Test case: Before 19:00:00 and next time is on Sunday")
  public void testGetNextTimeAt_BeforeSunday()
  {
    String sExpectedDateTime  = "2010-02-08 19:00:00";
    String sInputDateTime     = "2010-02-07 18:00:01";
    final int iHour = 19;
    String sActual = "";
    try
    {
      sActual = this._testGetNextTimeAt(iHour, sInputDateTime);
    }
    catch(NoSuchMethodException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(IllegalAccessException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(InvocationTargetException ex)
    {
      System.out.println(ex.getMessage());
    }    
    assertTrue(sActual.indexOf(sExpectedDateTime)!=-1, "Expected="+sExpectedDateTime+", Actual="+sActual);    
  }
  @Test(description="Test case: On 19:00:00 and next time is on Sunday")
  public void testGetNextTimeAt_OnSunday()
  {
    String sExpectedDateTime  = "2010-02-08 19:00:00";
    String sInputDateTime     = "2010-02-07 19:00:00";
    final int iHour = 19;
    String sActual = "";
    try
    {
      sActual = this._testGetNextTimeAt(iHour, sInputDateTime);
    }
    catch(NoSuchMethodException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(IllegalAccessException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(InvocationTargetException ex)
    {
      System.out.println(ex.getMessage());
    }    
    assertTrue(sActual.indexOf(sExpectedDateTime)!=-1, "Expected="+sExpectedDateTime+", Actual="+sActual);    
  }    
  
  @Test(description="Test case: Positive pause time.")
  public void testGetPauseTime_Positive() 
  {
    long lNextTimeInMillis = 5000;
    long lCurrDateTime = 1000;
    long lExpected = lNextTimeInMillis - lCurrDateTime;
    long lActual = 0;
    try
    {
      lActual = this._testGetPauseTime(lCurrDateTime, lNextTimeInMillis);
    }
    catch(NoSuchMethodException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(IllegalAccessException ex)
    {
      System.out.println(ex.getMessage());
    }
    catch(InvocationTargetException ex)
    {
      System.out.println(ex.getMessage());
    }    
    assertEquals(lActual, lExpected, "Expected="+lExpected+", Actual="+lActual);
  }

  @Test(description="Test case: Negative pause time.")
  public void testGetPauseTime_Negative() 
  {
    long lNextTimeInMillis = 1000;
    long lCurrDateTime = 5000;
    try
    {
      this._testGetPauseTime(lCurrDateTime, lNextTimeInMillis);
    }
    catch(NoSuchMethodException ex)
    {
      System.out.println(ex.getMessage());
      assertTrue(false, "NoSuchMethodException was thrown.");
    }
    catch(IllegalAccessException ex)
    {
      System.out.println(ex.getMessage());
      assertTrue(false, "IllegalAccessException was thrown.");
    }
    catch(InvocationTargetException ex)
    { // Test for exception.
      assertTrue(true, "InvocationTargetException was thrown.");
    }    
  }
  //=========================================================================
  // PRIVATE functions
  //=========================================================================
    
  
  private final String _testGetNextTimeAt(final int iHour, final String sDateTime)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    // Construct controlled Calendar object.
    Date oDate = null;
    SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat(Constant.DATE_TIME_FORMAT);
    try
    {
      oDate=oSimpleDateFormat.parse(sDateTime);
    }
    catch(ParseException ex)
    {
      System.out.println(ex.getMessage());
    }
    Calendar oCalendar = Calendar.getInstance();
    oCalendar.setTime(oDate);

    // Get the class of the private method.
    Prices oScheduler = new Prices();
    Class<?> oNewScheduler = oScheduler.getClass();

    // Change the property of the private method to be accessible.
    Method newPrivateFoo = oNewScheduler.getDeclaredMethod("_getNextTimeAt", int.class, Calendar.class);
    newPrivateFoo.setAccessible(true);
 
    // Run the private method.
    long lResult = Long.parseLong( newPrivateFoo.invoke( oScheduler, new Integer(iHour), oCalendar ).toString() );
    Date oNewDateResult = new Date(lResult);

    // Format the result
    return oSimpleDateFormat.format(oNewDateResult);
  }
  
  private final long _testGetPauseTime(final long lCurrDateTime, final long lNextTimeInMillis)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    // Get the class of the private method.
    Prices oScheduler = new Prices();
    Class<?> oNewScheduler = oScheduler.getClass();
  
    // Change the property of the private method to be accessible.
    Method newPrivateFoo = oNewScheduler.getDeclaredMethod("_getPauseTime", long.class, long.class);
    newPrivateFoo.setAccessible(true);
  
    // Run the private method.
    long lResult = Long.parseLong( newPrivateFoo.invoke( oScheduler, new Long(lCurrDateTime), new Long(lNextTimeInMillis) ).toString() );

    return lResult;
  }
}
