package org.stbot.score.rule;

import java.util.ArrayList;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.common.utils.SimpleLinearRegression;
import org.stbot.common.utils.Trigonometry;
import org.stbot.score.biz.RuleBiz;
import org.stbot.score.biz.ScoreBiz;
import org.stbot.score.model.Rule;
import org.stbot.stock.biz.Price180Biz;
import org.stbot.stock.model.Price180;

/**
 * Which stocks price don't fluctuate too much for the last 180 days.
 * The more the angle of the slope is closer to the plateau, the more stable is the price.
 * Cons: Doesn't take into account the volume. 
 * @author Xuan Ngo
 * INSERT INTO Rules(id, date, weight, description) VALUES(2, NOW(), 1.0, 'AverageTradingValue');
 */
public class LeastVolatilePrice
{
  private static final Logger log = LoggerFactory.getLogger(LeastVolatilePrice.class);
  
  public final int ID=2; // This is the ID of the this rule. Can't be changed.
  
  private Rule m_oRule = null;
  
  public LeastVolatilePrice()
  {
    RuleBiz oRuleBiz = new RuleBiz();
    this.m_oRule = oRuleBiz.load(this.ID);    
  }
  
  public void updateRule()
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    
    StringBuilder sDesc = new StringBuilder(500);
    sDesc.append(LeastVolatilePrice.class.getSimpleName()).append(":").append("\n");
    sDesc.append("Which stocks price don't fluctuate too much for the last 180 days.").append("\n");
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
    final int iNumDayPerSlice = 5;
    
    // Loop with the exact mutliple of iNumDayPerSlice(5)
    // Force to remove remainder if there is. Don't care about the remainder because they should be penalize if lPrices.size() < Price180Biz.iTradingDays.
    final int iMultiple = lPrices.size()/iNumDayPerSlice; 
    final int iMaxInLoop = iMultiple * iNumDayPerSlice;
    
    for(int i=0; i<iMaxInLoop;)
    {
      
      // Prepare an array of of 5 trading days.        
      ArrayList<Double> aX = new ArrayList<Double>();
      ArrayList<Double> aY = new ArrayList<Double>();
      for(int j=0; j<iNumDayPerSlice; j++)
      {
        aX.add(new Double(iNumDayPerSlice-j)); // Assume dates are separate by 1 day. Bad: Not very accurate.
        
        Price180 oPrice180 = lPrices.get(i);
        double dAvDailyPrice = (oPrice180.getOpen()+oPrice180.getHigh()+oPrice180.getLow()+oPrice180.getClose())/4.0;
        aY.add(dAvDailyPrice);
        
        i++;
        
//log.info("x={}, y={}, "+oPrice180.getDate(), new Double(5-j), new Double(dAvDailyPrice));
      }
      SimpleLinearRegression slr = new SimpleLinearRegression(aX, aY);
      Trigonometry oTri = new Trigonometry();
      dTotalPoints += oTri.getDegreeFromYAxisToHypotenuse(slr.getSlope())/90.0;
//log.info("slope={}, degree={}, intercept="+slr.getIntercept(), slr.getSlope(), oTri.getDegreeFromYAxisToHypotenuse(slr.getSlope()));        
//System.exit(0);        
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
    oScoreBiz.add(iCie_id, this.ID, oScoreDate, dTotalPoints/(Price180Biz.iTradingDays/iNumDayPerSlice), (dTotalPoints*this.m_oRule.getWeight())/(Price180Biz.iTradingDays/iNumDayPerSlice));
  }
  
}
