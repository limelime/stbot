package org.stbot.score.rule;

import java.util.ArrayList;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.score.biz.RuleBiz;
import org.stbot.score.biz.ScoreBiz;
import org.stbot.score.model.Rule;
import org.stbot.stock.biz.GeneralBiz;
import org.stbot.stock.model.Price180;

/**
 * Which stocks have their volume traded near the total number of outstanding shares for the last 90 days.
 * @author Xuan Ngo
 * INSERT INTO Rules(id, date, weight, description) VALUES(4, NOW(), 1.0, 'VolumeActivity');
 * select count(*) from Scores where rule_id=4 and realpoint >= 1;
 * select count(*) from Scores where rule_id=4 and realpoint > 0 and realpoint < 1;
 */
public class VolumeActivity
{
  private static final Logger log = LoggerFactory.getLogger(VolumeActivity.class);
  
  public final int ID=4; // This is the ID of the this rule. Can't be changed.
  
  private Rule m_oRule = null;
  
  private int m_iOptimalNumOfDays = 90;
  private double m_dPercentOfOutstanding = 0.25d;
  
  public VolumeActivity()
  {
    RuleBiz oRuleBiz = new RuleBiz();
    this.m_oRule = oRuleBiz.load(this.ID);    
  }
  
  public void updateRule()
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    
    StringBuilder sDesc = new StringBuilder(500);
    sDesc.append(ListedAge.class.getSimpleName()).append(":").append("\n");
    sDesc.append("Which stocks have their volume traded near ").append(this.m_dPercentOfOutstanding*100).append("% of their outstanding shares for the last ").append(m_iOptimalNumOfDays).append(" days.").append("\n");
    double dWeight = 1.0d;
    
    this.m_oRule.setId(this.ID);
    this.m_oRule.setDate(new java.sql.Date(System.currentTimeMillis()));
    this.m_oRule.setWeight(dWeight);
    this.m_oRule.setDescription(sDesc.toString());
    
    session.save(this.m_oRule);
    session.getTransaction().commit();
  }
  
  public void calculate(int iCie_id, ArrayList<Price180> lPrices)
  {
    double dTotalPoints = 0.0d;
    
    GeneralBiz oGeneralBiz = new GeneralBiz();
    final Long oOutstanding = oGeneralBiz.getOutstanding(iCie_id);
    if(oOutstanding!=null)
    {
      long lTotalVolume = 0;
      for(int i=0; i<lPrices.size() && i<this.m_iOptimalNumOfDays; i++)
      {
        lTotalVolume =+ lPrices.get(i).getVolume();
      }
      
      dTotalPoints = (double)lTotalVolume/(oOutstanding.doubleValue()*this.m_dPercentOfOutstanding);
      // Max value of dTotalPoints can't be greater than 1.
      if(dTotalPoints>=1.0d)
        dTotalPoints = 1.0d;
    }
    /*
     * Write points to Scores table.
     * Date should be the latest trading day. 
     */
    ScoreBiz oScoreBiz = new ScoreBiz();
    oScoreBiz.add(iCie_id, this.ID, new java.sql.Date(System.currentTimeMillis()), dTotalPoints, dTotalPoints*this.m_oRule.getWeight());
  }
}
