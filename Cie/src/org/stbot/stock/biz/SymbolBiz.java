package org.stbot.stock.biz;

import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.Query;
import org.hibernate.Session;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.stock.model.Symbol;
/**
 * Add or find a {@link org.stbot.stock.model.Symbol Symbol}.
 * @author Xuan Ngo
 *
 */
public class SymbolBiz
{
  /**
   * Create a Cie id and then create the symbol.
   *  Note: It is assumed that the symbol doesn't exist.
   * @param sSymbol
   * @return Return Cie id.
   */
  public final int add(final String sSymbol)
  {
    // Add an entry in Cies table.
    CieBiz oCieBiz = new CieBiz();
    final int cie_id = oCieBiz.add();

    // Add an entry in Symbols table.
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    
    Symbol oSymbol = new Symbol();
    oSymbol.setCie_id(cie_id);
    oSymbol.setDate( new Date(new java.util.Date().getTime()) );
    oSymbol.setSymbol(sSymbol);
    oSymbol.setUser_id(1); // UserID = 1 = System user.
    
    session.save(oSymbol);
    session.getTransaction().commit();
    
    return cie_id;
  }
  
  /**
   * Find a symbol
   * @param symbol
   * @return True if symbol is found. Otherwise, false.
   */
  public boolean find(final String symbol)
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    
    session.beginTransaction();

    // Using count(*) to guarantee that it return 1 result. Hence, oQuery.uniqueResult() can be used.
    Query oQuery = session.createQuery("SELECT count(*) FROM Symbol where symbol=:symbol");
    oQuery.setParameter("symbol", symbol);
    
    final long lNumOfResultsFound = Long.parseLong(oQuery.uniqueResult().toString());
    
    if(lNumOfResultsFound > 0) 
      return true;
    else
      return false;
  }

  /**
   * Return cie_ids of the symbol found.
   * @param symbol
   * @return Return cie_ids of the symbol found.
   */
  public ArrayList<Integer> findIds(final String symbol)
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    
    session.beginTransaction();

    // Using count(*) to guarantee that it return 1 result. Hence, oQuery.uniqueResult() can be used.
    Query oQuery = session.createQuery("SELECT cie_id FROM Symbol WHERE symbol=:symbol GROUP BY cie_id");
    oQuery.setParameter("symbol", symbol);
    
    List oList = oQuery.list();
    ArrayList<Integer> aCieIdList = new ArrayList<Integer>();
    for(int i=0; i<oList.size(); i++)
    {
      aCieIdList.add(new Integer(oList.get(i).toString()));
    }
    
    return aCieIdList;
  }  
}
