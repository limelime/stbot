package org.stbot.score.biz;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.score.model.Score;


public class ScoreBiz
{
  private static final Logger log = LoggerFactory.getLogger(ScoreBiz.class);
  
  /**
   * Should guarantee that there is only 1 score per rule per cie.
   * @param iCie_id
   * @param iRule_id
   * @param oDate
   * @param dPoints
   * @param dRealPoints
   */
  public void add(final int iCie_id, final int iRule_id, final java.sql.Date oDate, final double dPoints, final double dRealPoints)
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    
    // Delete old score.
    String sQuery = "DELETE FROM Scores WHERE cie_id=:cie_id AND rule_id=:rule_id";
    SQLQuery oQuery = session.createSQLQuery(sQuery);
    oQuery.setParameter("cie_id", iCie_id, Hibernate.INTEGER);
    oQuery.setParameter("rule_id", iRule_id, Hibernate.INTEGER);
    final int iRowAffected = oQuery.executeUpdate();    

    // Add newly compute score.
    Score oScore = new Score();
    oScore.setCie_id(iCie_id);
    oScore.setRule_id(iRule_id);
    oScore.setDate(oDate);
    oScore.setPoint(dPoints);
    oScore.setRealpoint(dRealPoints);
    
    session.save(oScore);
    session.getTransaction().commit();
    
  }
}
