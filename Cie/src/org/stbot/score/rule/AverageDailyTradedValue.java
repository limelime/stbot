package org.stbot.score.rule;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.stbot.common.utils.HibernateUtil;
import org.stbot.score.biz.ScoreBiz;
import org.stbot.score.biz.RuleBiz;
import org.stbot.score.model.Rule;
import org.stbot.stock.biz.JobBiz;
import org.stbot.stock.biz.Status;
import org.stbot.stock.biz.Price180Biz;
import org.stbot.stock.model.Cie;
import org.stbot.stock.model.Price180;


/**
 * Which stocks are being bought or sold a lot of times for the last 180 days.
 * @author Xuan Ngo
 * INSERT INTO Rules(id, date, weight, description) VALUES(1, NOW(), 1.0, 'AverageTradingValue');
 */
public class AverageDailyTradedValue
{
  private static final Logger log = LoggerFactory.getLogger(AverageDailyTradedValue.class);
  
  public final int ID=1; // This is the ID of the this rule. Can't be changed.
  
  private final int m_iMultiplier = 500;
  private final int m_iBuyCost = 1000;
  private final double m_dOptimalValue = this.m_iMultiplier * this.m_iBuyCost;
  
  private Rule m_oRule = null;
  
  public AverageDailyTradedValue()
  {
    RuleBiz oRuleBiz = new RuleBiz();
    this.m_oRule = oRuleBiz.load(this.ID);
  }

  public void updateRule()
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    
    StringBuilder sDesc = new StringBuilder(500);
    sDesc.append(AverageDailyTradedValue.class.getSimpleName()).append(":").append("\n");
    sDesc.append("Which stocks are being bought or sold a lot of times for the last 180 days.").append("\n");
    sDesc.append("The optimal daily value is ").append(this.m_dOptimalValue).append(".").append("\n");
    double dWeight = 1.0d;
    
    this.m_oRule.setId(this.ID);
    this.m_oRule.setDate(new java.sql.Date(System.currentTimeMillis()));
    this.m_oRule.setWeight(dWeight);
    this.m_oRule.setDescription(sDesc.toString());
    
    session.save(this.m_oRule);
    session.getTransaction().commit();
  }
  /**
   * 
   * @param iCie_id
   * @param lPrices Assume that it sorted by date in descending order.
   */
  public void calculate(int iCie_id, ArrayList<Price180> lPrices)
  {
    double dTotalPoints = 0.0d;

    // Compute score starts here.
    for(int i=0; i<lPrices.size(); i++)
    {
      // Calculate points.
      Price180 oPrice180 = lPrices.get(i);
      double dAvDailyPrice = (oPrice180.getOpen()+oPrice180.getHigh()+oPrice180.getLow()+oPrice180.getClose())/4.0;
      double dAvDailyValue = dAvDailyPrice * oPrice180.getVolume();
      if(dAvDailyValue >= this.m_dOptimalValue)
      {
        dTotalPoints +=1.0d;
      }
      else
      {
        dTotalPoints += (dAvDailyValue/this.m_dOptimalValue);
      }
    }
    
    /*
     * Write points to Scores table.
     * Date should be the latest trading day. 
     */
    ScoreBiz oScoreBiz = new ScoreBiz();
    java.sql.Date oScoreDate = new java.sql.Date(System.currentTimeMillis());
    if(lPrices.size()>0)
      oScoreDate = lPrices.get(0).getDate(); // Assume that lPrices are order by descending.
    // Use Price180Biz.iTradingDays instead of lPrices.size() to punish stocks that don't have 180 trading days.
    oScoreBiz.add(iCie_id, this.ID, oScoreDate, dTotalPoints/Price180Biz.iTradingDays, (dTotalPoints*this.m_oRule.getWeight())/Price180Biz.iTradingDays);  

  }
}
