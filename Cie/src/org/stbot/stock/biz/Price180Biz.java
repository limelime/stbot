package org.stbot.stock.biz;

import java.util.ArrayList;
import java.util.Calendar;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.stock.model.Price180;

public class Price180Biz
{
  private static final Logger log = LoggerFactory.getLogger(Price180Biz.class);
  
  public static final int iTradingDays = 180; // Number of trading days.
  /**
   * In this implementation, the date calculation doesn't have to be precise. 
   * The goal is to delete old prices in order to keep Prices180 table light 
   * so that data processing done on this table can be faster.
   * @param iNumOfTradingDays Number of last trading days.
   */
  public void deletePricesOlderThan(int iNumOfTradingDays)
  {
    final int iHolidays = 20; // Assuming that there is roughly 20 holidays per year.
    int iRealNumOfDays = (iNumOfTradingDays/5)*2+iNumOfTradingDays+iHolidays; // This is a rough calculation.
    Calendar oNow = Calendar.getInstance();
    oNow.add(Calendar.DAY_OF_WEEK, -iRealNumOfDays);
    java.sql.Date oAcceptableDate = new java.sql.Date(oNow.getTimeInMillis());
    
    if(log.isInfoEnabled())
      log.info("About to delete all records from Prices180 table that are older than {} trading days. DELETE FROM Prices180 WHERE date <='{}'", iNumOfTradingDays, oAcceptableDate);

    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();    
    String sQuery = "DELETE FROM Prices180 WHERE volume=0 OR date <=:date";
    SQLQuery oQuery = session.createSQLQuery(sQuery);
    oQuery.setParameter("date", oAcceptableDate, Hibernate.DATE);
    final int iRowAffected = oQuery.executeUpdate();
    
    if(log.isInfoEnabled())
      log.info("{} rows have been deleted.", iRowAffected);
  }
  
  public ArrayList<Price180> getPricesOf(final int iCie_id)
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    
    /*
     * Select all unique cie_id that have prices.
     * The focus is on the Jobs table. This query left join with Cies table because I only want the cie_id.
     */
    String sQuery = "SELECT Prices180.* FROM Prices180 "+
                    "WHERE cie_id=:cie_id "+
                    "ORDER BY date DESC "+
                    "LIMIT "+iTradingDays;
    SQLQuery oQuery = session.createSQLQuery(sQuery);
    oQuery.addEntity(Price180.class);
    oQuery.setParameter("cie_id", iCie_id, Hibernate.INTEGER);

    return new ArrayList<Price180>(oQuery.list());    
  }
}
