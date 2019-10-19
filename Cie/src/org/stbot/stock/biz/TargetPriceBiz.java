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

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.stock.model.Symbol;
import org.stbot.stock.model.Finance;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

public class TargetPriceBiz
{
  private static final Logger log = LoggerFactory.getLogger(TargetPriceBiz.class);
  
  private ArrayList<Symbol> m_aSymbols = new ArrayList<Symbol>();
  
  private int m_iMaxSymbolsPerUrl = 25;
  
  public TargetPriceBiz()
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
   * t8 = 1 year target price
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
    
    String sUrl = String.format("http://finance.yahoo.com/d/quotes.csv?s=%s&f=t8", sSymbols.toString());
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

      CsvListReader inFile = new CsvListReader(oCsvStream, CsvPreference.EXCEL_PREFERENCE);

      // Process csv file.
      List<String> aCsvLineData = null;
      for(int i=0; i<lSymbols.size(); i++)
      {
        aCsvLineData = inFile.read();
        if(aCsvLineData!=null)
        {
          this.update(lSymbols.get(i).getCie_id(), aCsvLineData);
        }
        else
        {// csv data is blank.
          if(log.isInfoEnabled())
            log.info("\tNo Data: cie_id={}, data=>{}", lSymbols.get(i).getCie_id(), aCsvLineData);
        }
      }
      inFile.close();
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
  
  private void update(int iCie_id, List<String> aCsvData)
  {
    int iIdx = 0;
    final String sTargetPrice = aCsvData.get(iIdx++).trim();
    
    // Set values of object model.
    Finance oFinance = new Finance();
    oFinance.setCie_id(iCie_id);
    oFinance.setDate(new java.sql.Date(System.currentTimeMillis()));
    
    if(sTargetPrice.compareToIgnoreCase("n/a")!=0)
      oFinance.setTargetPrice(Double.parseDouble(sTargetPrice));

    // Commit
    // Note: Use saveOrUpdate so that entry is created if it doesn't exist.
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    session.saveOrUpdate(oFinance);
    session.getTransaction().commit();
  }
}
