package org.stbot.score.rule;

import java.sql.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.stock.model.Cie;


/**
 * <pre>
 * Conditions:
 *  1)
 *    Constant: Average trading value per day must be greater or equal to 100K.
 *  2)
 *    Variable: Starting from today and going backward, for each week where 1) 
 *    is satisfied, add {0.1} point. A maximum of 1 point can be allocated. It 
 *    means that we are only checking the condition(1) for a maximum of 2 and a 
 *    half months(1/0.1/4).
 * </pre>
 * @author Xuan Ngo
 *
 */
public class AverageTradingValue2
{
  private String m_sPriceTblName = "tmpPrices";
  private double m_dDailyTradingValue = 100000.00d;
  private double m_dUnitPoint = 0.0d; // get from db.
  
  /**
   * Make a copy of Price table.
   * Preserve prices of cie starting from oFromDate. In order words, remove 
   *  prices older than oFromDate.
   * @param oFromDate
   */
  public void createPriceTblWithDateFrom(Date oFromDate)
  {
    
  }
  
  /**
   * Add unit point for every week where their daily average is greater than
   *  m_dDailyTradingValue. Each cie can only have a maximum of 1 point.
   */
  public void addPoints()
  {
    
  }
  
  /**
   * Is the the average trading value bigger than dBiggerThan since oSinceDate?
   * @param cid
   * @param dBiggerThan
   * @param oSinceDate
   * @return True if conditions are satisfied.
   */
  public boolean isAverageTradingValue(int cid, double dBiggerThan, Date oSinceDate)
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    
    session.beginTransaction();    
    /**
     * (open+high+low+close)/4 => to spread the average. Therefore, getting a better average.
     */
    String sHQuery = "SELECT AVG((open+high+low+close)/4*volume) FROM Price WHERE cie_id=:cie_id and date >= :date";
    Query oQuery = session.createQuery(sHQuery);
    oQuery.setParameter("cie_id", cid);
    oQuery.setParameter("date", oSinceDate);

    double dAverage = Double.parseDouble(oQuery.uniqueResult().toString());

    if( dAverage >= m_dDailyTradingValue )
    {
      return true;
    }
    else
    {
      return false;
    }    
  }
}
