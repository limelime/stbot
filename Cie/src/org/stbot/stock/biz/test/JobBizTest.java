package org.stbot.stock.biz.test;

import org.stbot.common.utils.Constant;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.stock.biz.JobBiz;
import org.stbot.stock.biz.Status;
import org.stbot.stock.model.Symbol;
import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class JobBizTest
{

  @Test
  public void testGetToDoDateTime_BeforeThreshold()
  {
    /**
     *  Test case: Before 19:00:00.
     */
    String sExpectedDate  = "2010-02-04";
    String sInputDate     = "2010-02-05";
    String sThresholdTime = "18:59:59";
    java.sql.Timestamp oActual = null;
    try
    {
      oActual = this._testGetToDoDateTime(sInputDate, sThresholdTime);
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
    assertTrue(oActual.toString().indexOf(sExpectedDate)!=-1, "Expected="+sExpectedDate+", Actual="+oActual.toString());
  }  
  
  
  @Test
  public void testGetToDoDateTime_OnThreshold()
  {
    /**
     *  Test case: On 19:00:00.
     */
    String sExpectedDate  = "2010-02-04";
    String sInputDate     = "2010-02-05";
    String sThresholdTime = "19:00:00";
    java.sql.Timestamp oActual = null;
    try
    {
      oActual = this._testGetToDoDateTime(sInputDate, sThresholdTime);
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
    assertTrue(oActual.toString().indexOf(sExpectedDate)!=-1, "Expected="+sExpectedDate+", Actual="+oActual.toString());
  }
  
  @Test
  public void testGetToDoDateTime_AfterThreshold()
  {
    /**
     *  Test case: After 19:00:00.
     */
    String sExpectedDate  = "2010-02-05";
    String sInputDate     = "2010-02-05";
    String sThresholdTime = "19:00:01";
    java.sql.Timestamp oActual = null;
    try
    {
      oActual = this._testGetToDoDateTime(sInputDate, sThresholdTime);
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
    assertTrue(oActual.toString().indexOf(sExpectedDate)!=-1, "Expected="+sExpectedDate+", Actual="+oActual.toString());
  }
  
 /* 
  @Test
  public void testGetTodoCies()
  {
    JobBiz oJobBiz = new JobBiz();
    int iInitialSize = oJobBiz.getTodoCies(oJobBiz.getToDoDateTime()).size();
    
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();    

    String sQuery = "update prices set date=:date where date <> :date"; 
    SQLQuery oQuery = session.createSQLQuery(sQuery);
        oQuery.setParameter("todoDateTime", oToDoDateTime);
    //session.getTransaction().commit();
  }
  */
  
  
  
  private java.sql.Timestamp _testGetToDoDateTime(String sInputDate, String sThresholdTime)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    // Construct controlled Calendar object.
    Date oDate = null;
    SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat(Constant.DATE_TIME_FORMAT);
    try
    {
      oDate=oSimpleDateFormat.parse(sInputDate+" "+sThresholdTime);
    }
    catch(ParseException ex)
    {
      System.out.println(ex.getMessage());
    }
    Calendar oCalendar = Calendar.getInstance();
    oCalendar.setTime(oDate);
    
    // Get the class of the private method.
    JobBiz oJobBiz = new JobBiz();
    Class<?> oNewJobBiz = oJobBiz.getClass();
 
    // Change the property of the private method to be accessible.
    Method newPrivateFoo = oNewJobBiz.getDeclaredMethod("_getToDoDateTime", Calendar.class);
    newPrivateFoo.setAccessible(true);
 
    // Run the private method.
    return (java.sql.Timestamp)newPrivateFoo.invoke(oJobBiz, oCalendar);
  }
}
