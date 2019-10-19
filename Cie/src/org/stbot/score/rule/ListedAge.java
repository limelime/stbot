package org.stbot.score.rule;

import java.util.Calendar;

import org.hibernate.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.stbot.common.utils.HibernateUtil;
import org.stbot.score.biz.RuleBiz;
import org.stbot.score.biz.ScoreBiz;

import org.stbot.score.model.Rule;
import org.stbot.stock.biz.GeneralBiz;

/**
 * Being listed longer on exchange means the stock is stable as it stood through time.
 * @author Xuan Ngo
 * INSERT INTO Rules(id, date, weight, description) VALUES(3, NOW(), 1.0, 'ListedAge');
 */
public class ListedAge
{
  private static final Logger log = LoggerFactory.getLogger(ListedAge.class);
  
  public final int ID=3; // This is the ID of the this rule. Can't be changed.
  
  private Rule m_oRule = null;
  
  private final int m_iOptimalNumOfYears = 10;
  
  public ListedAge()
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
    sDesc.append("Which stocks are listed longer than "+this.m_iOptimalNumOfYears+" years.").append("\n");
    double dWeight = 0.7d;
    
    this.m_oRule.setId(this.ID);
    this.m_oRule.setDate(new java.sql.Date(System.currentTimeMillis()));
    this.m_oRule.setWeight(dWeight);
    this.m_oRule.setDescription(sDesc.toString());
    
    session.save(this.m_oRule);
    session.getTransaction().commit();
  }
  
  public void calculate(int iCie_id)
  {
    double dTotalPoints = 0.0d;
    
    GeneralBiz oGeneralBiz = new GeneralBiz();
    final Integer oIpoYear = oGeneralBiz.getIpoYear(iCie_id);
    if(oIpoYear!=null)
    {
      Calendar oNow = Calendar.getInstance();
      
      // Current Year - IPO Year.
      final int YearsDiff = oNow.get(Calendar.YEAR) - oIpoYear.intValue();
      dTotalPoints = (double)YearsDiff/(double)this.m_iOptimalNumOfYears;
      
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
