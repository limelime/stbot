package org.stbot.stock.biz;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.stock.model.Symbol;
import org.stbot.stock.model.Finance;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

public class FloatShareBiz
{
  private static final Logger log = LoggerFactory.getLogger(FloatShareBiz.class);
  
  private ArrayList<Symbol> m_aSymbols = new ArrayList<Symbol>();
  private int m_iMaxSymbolsPerUrl = 25;
  
  public FloatShareBiz()
  {}
  public void run()
  {
    JobBiz oJobBiz = new JobBiz();
    
    // Get all symbols to update.
    ArrayList<Symbol> lSymbolsWithPrices = oJobBiz.getSymbolsWithPrices();   

/*
TEST
for(int k=0;k<lSymbolsWithPrices.size(); k++)
  System.out.println(String.format("%s, %s", lSymbolsWithPrices.get(k).getCie_id(), lSymbolsWithPrices.get(k).getSymbol()));
System.exit(0);
*/
    for(int i=0; i<lSymbolsWithPrices.size();)
    {
      /**
       * Process 10 symbols at a time.
       */
      for(int j=0; j<this.m_iMaxSymbolsPerUrl && i<lSymbolsWithPrices.size(); j++, i++)
      {
        this.addSymbol(lSymbolsWithPrices.get(i));
      }
      
      final String sUrl = this.getUrl();
      // Log what will be processed.
      if(log.isInfoEnabled())
        log.info("{}: Processing {}", String.format("%06d/%d", i, lSymbolsWithPrices.size()), sUrl);
      
      this.retrieveData(sUrl, this.getSymbolsBatch());
      this.clearSymbols(); // It is important to destroy the batch of symbols.
    }
    System.out.println(lSymbolsWithPrices.size());
    
  }
  
  private void addSymbol(final Symbol oSymbol)
  {
    this.m_aSymbols.add(oSymbol);
  }
  private void clearSymbols()
  {
    this.m_aSymbols = new ArrayList<Symbol>();
  }
  /**
   * f6 = float shares
   * @return
   */
  private String getUrl()
  {
    StringBuilder sSymbols = new StringBuilder(this.m_iMaxSymbolsPerUrl*6);
    if(this.m_aSymbols.size()>0)
    {
      for(int i=0; i<this.m_aSymbols.size()-1; i++)
      {
        sSymbols.append(this.m_aSymbols.get(i).getSymbol()).append("+");
      }
      sSymbols.append(this.m_aSymbols.get(this.m_aSymbols.size()-1).getSymbol());
    }
    
    String sUrl = String.format("http://finance.yahoo.com/d/quotes.csv?s=%s&f=f6", sSymbols.toString());
    return sUrl;
  }
  
  private ArrayList<Symbol>getSymbolsBatch()
  {
    return this.m_aSymbols; 
  }
  
  
  
  
  
  private void retrieveData(final String sUrl, final ArrayList<Symbol> lSymbols)
  {
    try
    {
      // Get csv file.
      URL oUrl = new URL(sUrl);
      BufferedReader oCsvStream = new BufferedReader(new InputStreamReader(oUrl.openStream()));
      
      // Process csv file.
      String sLine ="";
      for(int i=0; i<lSymbols.size() && (sLine = oCsvStream.readLine())!=null; i++)
      {
        sLine = sLine.trim();
        if(sLine.length()!=0)
        {
          this.update(lSymbols.get(i).getCie_id(), sLine);
        }
      }
      oCsvStream.close();
    }
    catch(FileNotFoundException e)
    {
      log.error("CSV file not found: "+sUrl+"\n"+e.getMessage());
    }
    catch(IOException e)
    {
      log.error("Unexpected error: "+sUrl+"\n"+e.getMessage());
    }
  }
  
  private void update(int iCie_id, String sLine)
  {
    final String sFloatShare = sLine;
    
    // Set values of object model.
    Finance oFinance = new Finance();
    oFinance.setCie_id(iCie_id);
    
    if(sFloatShare.compareToIgnoreCase("n/a")!=0)
    {
      try
      {
        // Parse the float share.
        oFinance.setFloatShare(new Long(NumberFormat.getInstance().parse(sFloatShare).longValue()));
        
        // Commit
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(oFinance);
        
        Query oQuery = session.createSQLQuery("UPDATE Finances SET floatShare=:floatShare WHERE cie_id=:cie_id");
        oQuery.setInteger("cie_id", oFinance.getCie_id());
        oQuery.setLong("floatShare", oFinance.getFloatShare());
        int iRowCnt = oQuery.executeUpdate();        
      }
      catch(ParseException ex)
      {
        log.error(String.format("cie_id=%d, Float share=%s", iCie_id, sFloatShare), ex);
      }
    }
  }
}
