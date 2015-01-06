package org.stbot.stock.biz;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProxySelector;
import java.net.URL;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Query;
import org.hibernate.Session;

import org.stbot.common.utils.HibernateUtil;
import org.stbot.stock.biz.JobBiz;
import org.stbot.stock.biz.UrlBiz;
import org.stbot.stock.model.Price;
import org.stbot.stock.model.Price180;
import org.stbot.stock.model.Symbol;


import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.btr.proxy.search.ProxySearch;


/**
 * Use to update daily price or history prices of all cies.
 * @author Xuan Ngo
 * 
 */
public class PriceBiz
{
  private static int CSV_PRICE_TOKEN  = 7; // Yahoo's csv price table.
  
  private JobBiz m_oJobBiz = new JobBiz();
  
  private static final Logger log = LoggerFactory.getLogger(PriceBiz.class);
 
  /**
   * Update all cies.
   */
  public void updateAll()
  {
    // Get the ToDoDateTime(The date to update to).
    Timestamp oToDoDateTime = this.m_oJobBiz.getToDoDateTime();
    
    // Get all symbols to update.
    ArrayList<Symbol> aSymbols = this.m_oJobBiz.getTodoCies(oToDoDateTime);
    
    // Logging
    if(log.isInfoEnabled())
    {
      log.info("Start updating prices of {} stocks:", aSymbols.size());
    }
    int i=0; // Use in logging only
    
    // Update prices for each cie.
    for(Symbol s : aSymbols)
    {
      // Logging: Make sure to report the stock first before updating so that we know on which stock the errors occur.
      if(log.isInfoEnabled())
      {
        Object[] oParameters = new Object[]{
                                              String.format("%06d", i+1), // e.g. 0000X
                                              aSymbols.size(),
                                              s.getSymbol(),
                                              s.getCie_id(),
                                            };
        log.info("{}/{}: Updating prices of {} [cie_id={}] ...", oParameters);
        i++;
      }
      
      // From Date = LatestDbDate + 1 day.
      int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
      Date oFromDate = new Date(this.getLatestDbDate(s.getCie_id()).getTime()+MILLIS_IN_DAY);
      
      this.update(s.getCie_id(), s.getSymbol(), oFromDate, new Date(oToDoDateTime.getTime()));

        
// Test to remove.
/*
if(i>25)
  return;
else
  i++;
*/
    }    
  }
  
  /**
   * Update prices of 1 cie starting from the last date from db to 'today'.
   * Either the update prices failed or succeed, log in {@link org.stbot.stock.model.Job} table.}
   * @param cid
   * @param sSymbol
   * @param oFromDate
   * @param oToDate
   */
  private void update(final int cid, final String sSymbol, final Date oFromDate, final Date oToDate)
  {
    // Get the URL
    UrlBiz oUrlBiz = new UrlBiz();
    final String sUrl = oUrlBiz.getUrl(sSymbol, oFromDate, oToDate);

    try
    {
      // Use Proxy if applicable.
      ProxySearch proxySearch = ProxySearch.getDefaultProxySearch();
      ProxySelector myProxySelector = proxySearch.getProxySelector();
      ProxySelector.setDefault(myProxySelector);
      
      // Get csv file.
      URL oUrl = new URL(sUrl);
      BufferedReader oCsvStream = new BufferedReader(new InputStreamReader(oUrl.openStream()));

      CsvListReader inFile = new CsvListReader(oCsvStream, CsvPreference.EXCEL_PREFERENCE);

      final String[] header = inFile.getCSVHeader(true); // Discard the header line.

      if(header[0].compareToIgnoreCase("Date")==0)
      {// The downloaded file is the expected csv file format.
        // Process csv file.
        List<String> aCsvLineData = null;
        while ((aCsvLineData = inFile.read()) != null)
        {
          if(aCsvLineData.size()==CSV_PRICE_TOKEN)
          {
            try
            {
              // Add each day's price.
              this.add(aCsvLineData, cid);
            }
            catch(Exception ex)
            {
              /**
               * Log as much info as possible if saving data in database goes wrong.
               */
              if(log.isErrorEnabled())
              {
                log.error("Unable to save prices: was retrieving {}.", sUrl);
                log.error("Unable to save prices: Data was: {}", aCsvLineData);
              }
              this.m_oJobBiz.updateStatus(cid, JobBiz.ERROR, "Data was: "+aCsvLineData+". Retrieving from "+sUrl);
              throw new RuntimeException(ex);
            }
          }
          // Else, discard line not matching the expected number of columns.
        }
        
        // Log the update prices for this cie is successful.
        this.m_oJobBiz.updateStatus(cid, JobBiz.SUCCESS, "Success");
        
      }
      else
      {// The downloaded file is NOT the expected CSV file format.
        
        String sErrorMsg = "Unexpected CSV file format: "+sUrl;
        this.m_oJobBiz.updateStatus(cid, JobBiz.CSV_FILE_BAD_FORMAT, sErrorMsg);
        
        if(log.isErrorEnabled())
        {
          log.error("cid={}: {}", cid, sErrorMsg);
        }
        
      }

      inFile.close();
      
    }
    catch(FileNotFoundException e)
    {
      String sErrorMsg = "CSV file not found: "+sUrl;
      this.m_oJobBiz.updateStatus(cid, JobBiz.CSV_FILE_NOT_FOUND, sErrorMsg);
      
      if(log.isErrorEnabled())
      {
        log.error("cid={}: {}", cid, sErrorMsg);
      }      
    }
    catch(IOException e)
    {
      String sErrorMsg = "Unexpected error: "+sUrl;
      this.m_oJobBiz.updateStatus(cid, JobBiz.ERROR, sErrorMsg);
      
      if(log.isErrorEnabled())
      {
        log.error("cid={}: {}", cid, sErrorMsg);
      }         
    }
    
  }
  
  /**
   * Get the date of the latest price entry entered in database.
   * If it is nulled, then return default date = 1990-01-01
   * @param cid
   * @return
   */
  private java.sql.Date getLatestDbDate(final int cid)
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();    
    
    String sHQuery = "SELECT MAX(date) FROM Price WHERE cie_id=:cie_id";
    Query oQuery = session.createQuery(sHQuery);
    oQuery.setParameter("cie_id", cid);

    // Set default date
    Object oResult = oQuery.uniqueResult(); 
    if( oResult == null )
    {
      return java.sql.Date.valueOf("1990-01-01");
    }
    else
    {
      return java.sql.Date.valueOf(oResult.toString());
    }
    
  }  
  
  private void add(final List<String> aCsvData, final int cid)
  {
    // Date  Open  High  Low Close Volume  AdjClose
    int iIdx = 0;
    final Date oDate      = java.sql.Date.valueOf(aCsvData.get(iIdx++));
    final double dOpen    = Double.parseDouble( aCsvData.get(iIdx++) );
    final double dHigh    = Double.parseDouble( aCsvData.get(iIdx++) );
    final double dLow     = Double.parseDouble( aCsvData.get(iIdx++) );
    final double dClose   = Double.parseDouble( aCsvData.get(iIdx++) );
    final long lVolume    = Long.parseLong( aCsvData.get(iIdx++) );
    final double dAdjClose= Double.parseDouble( aCsvData.get(iIdx++) );
    
    // Transfer data to Price model.
    Price oPrice = new Price();
    oPrice.setCie_id(cid);
    oPrice.setDate(oDate);
    oPrice.setOpen(dOpen);
    oPrice.setHigh(dHigh);
    oPrice.setLow(dLow);
    oPrice.setClose(dClose);
    oPrice.setVolume(lVolume);
    oPrice.setAdjClose(dAdjClose);
    
    // Duplicate the same price info to the lightweight table Prices180
    Price180 oPrice180 = new Price180();
    oPrice180.setCie_id(cid);
    oPrice180.setDate(oDate);
    oPrice180.setOpen(dOpen);
    oPrice180.setHigh(dHigh);
    oPrice180.setLow(dLow);
    oPrice180.setClose(dClose);
    oPrice180.setVolume(lVolume);
    oPrice180.setAdjClose(dAdjClose);
    

    // Commit
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    session.save(oPrice);
    session.save(oPrice180);
    session.getTransaction().commit();
  }
  
}
