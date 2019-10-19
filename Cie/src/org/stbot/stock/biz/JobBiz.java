package org.stbot.stock.biz;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.stock.model.Cie;
import org.stbot.stock.model.Job;
import org.stbot.stock.model.Symbol;



/**
 * Get a list of cie to update.
 *  If there are corrections to be made(e.g Symbol name change), then do it at the source, not here.
 *  Its responsibility is to keep track of errors.
 * @author Xuan Ngo
 *
 */
public class JobBiz
{
  // Need to synchronize the database if the values are changed.
  //  Database: status field.
  public static int SUCCESS             = 0; // Prices update is successful.
  public static int WARNING             = 1; // Not used yet: Warning???
  public static int ERROR               = 2; // Unexpected error.
  public static int CSV_FILE_NOT_FOUND  = 3; // Url to CSV file is not found.
  public static int CSV_FILE_BAD_FORMAT = 4; // Unexpected CSV file format.
  
  private static final Logger log = LoggerFactory.getLogger(JobBiz.class);
  
  /**
   * Get a list of active cies and don't include those that you have already ran.
   * @param oToDoDateTime See {@link org.stbot.stock.biz.JobBiz#getToDoDateTime()}
   * @return Return a list of {@link org.stbot.stock.model.Symbol}
   */
  public ArrayList<Symbol> getTodoCies(java.sql.Timestamp oToDoDateTime)
  {
    if(log.isInfoEnabled())
    {
      log.info("ToDoDateTime = {}", oToDoDateTime);
    }
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    
    // Include active cies with the latest symbol name and don't include those that you have already ran.
    //  Active cies = General.status = Status.TRADED
    //  Exclude cies that are already ran = Job.dateTime >= :todoDateTime
    String sQuery = "SELECT Symbols.* "+
                      "FROM (Symbols INNER JOIN Generals ON Symbols.cie_id = Generals.cie_id) LEFT JOIN Symbols AS Symbols_1 ON (Symbols.date < Symbols_1.date) AND (Symbols.cie_id = Symbols_1.cie_id) "+
                      "WHERE (Generals.status=:activeStatus AND Symbols_1.date IS NULL AND Symbols.cie_id NOT IN (SELECT cie_id from Jobs where Jobs.dateTime>= :todoDateTime) );";
    SQLQuery oQuery = session.createSQLQuery(sQuery);
    oQuery.addEntity(Symbol.class);
    oQuery.setParameter("todoDateTime", oToDoDateTime, Hibernate.TIMESTAMP);
    oQuery.setParameter("activeStatus", Status.TRADED, Hibernate.INTEGER);

    return new ArrayList<Symbol>(oQuery.list());
  }  
  
  /**
   * @return Return today's date if it is now past 19:00:00. Otherwise, return yesterday's date. 
   */
  public java.sql.Timestamp getToDoDateTime()
  {
    return this._getToDoDateTime(Calendar.getInstance());
  }
  /**
   * Return oCurrDateTime's date if it is now past 19:00:00. Otherwise, return oCurrDateTime's date - 1 day.
   * @param oCurrDateTime
   * @return java.sql.Timestamp Return oCurrDateTime's date if it is now past 19:00:00. Otherwise, return oCurrDateTime's date - 1 day.
   */
  private java.sql.Timestamp _getToDoDateTime(final Calendar oCurrDateTime)
  {
    final int THRESHOLD_TIME = 19; // 0-24h
    final long MILLIS_AT_THRESHOLD_TIME = 1000 * 60 * 60 * THRESHOLD_TIME;
    
    // Get the current date time. Swing between util.Date & sql.Date to reset time to 00:00:00.
    java.util.Date oDateOnly = oCurrDateTime.getTime();
    java.sql.Date oSqlDate = new java.sql.Date(oDateOnly.getTime());
    oSqlDate = java.sql.Date.valueOf(oSqlDate.toString()); //Java Spec: java.sql.Date.toString() formats a date in the date escape format yyyy-mm-dd.

    if(oCurrDateTime.getTimeInMillis()>oSqlDate.getTime()+MILLIS_AT_THRESHOLD_TIME) // 19:00:00
    {// Return today's date
      return new java.sql.Timestamp(oSqlDate.getTime());
    }
    else
    {// Return yesterday's date: Tricky: need to handle end of month and year
      int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
      oSqlDate.setTime(oSqlDate.getTime()-MILLIS_IN_DAY);
      return new java.sql.Timestamp(oSqlDate.getTime());
    }
  }
  
  public void updateStatus(int cid, int iStatus, String sMessage)
  {
    // Create Job object.
    Job oJob = new Job();
    oJob.setCie_id(cid);
    oJob.setDateTime( new Timestamp(System.currentTimeMillis()) );
    oJob.setStatus(iStatus);
    oJob.setMessage(sMessage);
    
    // Commit
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    session.save(oJob);
    session.getTransaction().commit();       
  }
  
  /**
   * A list of cie_id that have prices.
   * @return Return a list of cie_id that have prices.
   */  
  public ArrayList<Cie> getCiesWithPrices()
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    
    /*
     * The focus is on the Jobs table. The query left join with Cies table because I only want the cie_id.
     * Select all unique cie_id that have prices(GROUP BY cie_id make cie_id unique).
     */
    String sQuery = " SELECT Cies.* FROM Cies " +
                        "INNER JOIN Jobs ON Jobs.cie_id=Cies.id "+
                      "WHERE status=:status group by cie_id;";
    SQLQuery oQuery = session.createSQLQuery(sQuery);
    oQuery.addEntity(Cie.class);
    oQuery.setParameter("status", JobBiz.SUCCESS, Hibernate.INTEGER);

    return new ArrayList<Cie>(oQuery.list());
  }
  
  public ArrayList<Symbol> getSymbolsWithPrices()
  {

    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    
    // Get the latest symbols of each active cie_id
    //  Active cies = General.status = Status.TRADED
    String sQuery = "SELECT Symbols.* "+
                      "FROM (Symbols INNER JOIN Generals ON Symbols.cie_id = Generals.cie_id) LEFT JOIN Symbols AS Symbols_1 ON (Symbols.date < Symbols_1.date) AND (Symbols.cie_id = Symbols_1.cie_id) "+
                      "WHERE Generals.status=:activeStatus AND Symbols_1.date IS NULL;";
    SQLQuery oQuery = session.createSQLQuery(sQuery);
    oQuery.addEntity(Symbol.class);
    oQuery.setParameter("activeStatus", Status.TRADED, Hibernate.INTEGER);

    return new ArrayList<Symbol>(oQuery.list());
  }    
  
}
