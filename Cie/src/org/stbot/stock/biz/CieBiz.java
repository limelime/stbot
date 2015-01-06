package org.stbot.stock.biz;

import org.hibernate.Session;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.stock.model.Cie;
import org.stbot.stock.model.Symbol;
/**
 * Synchronized: Add new {@link org.stbot.stock.model.Cie Cie}.
 * @author Xuan Ngo
 *
 */
public class CieBiz
{
  public CieBiz()
  {
  }
  
  public synchronized final int add()
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    
    session.beginTransaction();
    
    Cie oCie = new Cie();
    session.save(oCie);

    session.getTransaction().commit();
//    HibernateUtil.getSessionFactory().close();
    
    return oCie.getId();
  }
}
