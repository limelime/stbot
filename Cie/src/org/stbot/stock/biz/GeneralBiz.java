package org.stbot.stock.biz;

import org.hibernate.Session;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stbot.common.utils.HibernateUtil;

public class GeneralBiz
{
  private static final Logger log = LoggerFactory.getLogger(GeneralBiz.class);
  
  public final Integer getIpoYear(final int iCie_id)
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    
    Query oQuery = session.createSQLQuery("SELECT ipoyear FROM Generals WHERE cie_id=:cie_id");
    oQuery.setInteger("cie_id", iCie_id);
    
    Object oResult = oQuery.uniqueResult();
    if(oResult==null)
      return null;
    else
      return new Integer(oResult.toString());
  }
  
  public final Long getOutstanding(final int iCie_id)
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    
    Query oQuery = session.createSQLQuery("SELECT outstanding FROM Generals WHERE cie_id=:cie_id");
    oQuery.setInteger("cie_id", iCie_id);
    
    Object oResult = oQuery.uniqueResult();
    if(oResult==null)
      return null;
    else
      return new Long(oResult.toString());
  }  
}
