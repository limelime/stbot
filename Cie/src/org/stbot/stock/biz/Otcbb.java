package org.stbot.stock.biz;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.common.utils.Unique;
import org.stbot.stock.model.General;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

public class Otcbb extends Exchange
{
  private static final Logger log = LoggerFactory.getLogger(Otcbb.class);
  
  public Otcbb()
  {
    super.m_sName = "OTCBB";
    super.m_sCsvUrl = "http://www.nasdaq.com/asp/symbols.asp?exchange=O&start=0";
    super.m_iCsvTokenSize = 2;
  }

  public void update()
  {
    // Logging
    if(log.isInfoEnabled())
    {
      log.info("Updating stocks info from {} ...", this.getName());
    }
    
    // Download CSV data file from exchange.
    final String ExchangeCsvFilePath = this.m_sExchangeDirPath+this.getName()+"_"+Unique.getCurrDateTime(3000)+".csv";
    super.downloadCsv(ExchangeCsvFilePath);
 
    try
    {
      BufferedReader oCsvStream = new BufferedReader(new FileReader(ExchangeCsvFilePath));
      oCsvStream.readLine();  // Discard the 1st line, Date of file.

      CsvListReader inFile = new CsvListReader(oCsvStream, CsvPreference.EXCEL_PREFERENCE);

      final String[] header = inFile.getCSVHeader(true); // Discard the header line.
      
      // Process csv file.
      List<String> aCsvLineData = null;
      int iLinesRead = 0;
      while ((aCsvLineData = inFile.read()) != null)
      {
        if(aCsvLineData.size()==this.getCsvTokenSize()) // Last line contains 4 columns: Copyright line.
        {
          this.add(aCsvLineData);
          iLinesRead++;
        }
        // Else, discard line not matching the expected number of columns.
      }
      inFile.close();
      
      // Logging.
      if(iLinesRead<1)
      {
        if(log.isErrorEnabled())
        {
          log.error("{}: Unable to process the csv file. Was retrieve csv file from {}.", this.getName(), this.getCsvUrl());
        }
      }
      else
      {
        if(log.isInfoEnabled())
        {
          log.info("{}: Processed {} lines from csv file.", this.getName(), iLinesRead);
        }
      }
      
    }
    catch(FileNotFoundException e)
    {
      throw new RuntimeException(e);
    }
    catch(IOException e)
    {
      throw new RuntimeException(e);
    }        
  }
  
  /**
   * Add a new company with general information from CSV file.
   * Note: Implementation is not atomic. If data structure change in Generals table but
   *  not in Symbols, then there is a commit in Symbols table but not in Generals table.
   * @param aCsvData
   */
  private void add(final List<String> aCsvData)
  {
    //"Name", "Symbol"
    int iIdx = 0;
    final String sName      = aCsvData.get(iIdx++).trim();
    final String sSymbol    = aCsvData.get(iIdx++).trim(); // Assume that the 1st token is the symbol.
    
    /**
     * Create a new company if it does NOT exist.
     * Otherwise, do nothing.
     */
    SymbolBiz oSymbolBiz = new SymbolBiz();
    if(!oSymbolBiz.find(sSymbol))
    {
      // Add new company info in Symbols table.
      int iCieId = oSymbolBiz.add(sSymbol);
      
      // Add new company info in Generals table.
      Session session = HibernateUtil.getSessionFactory().getCurrentSession();
      session.beginTransaction();
      
      General oGeneral = this.getGeneral(aCsvData, iCieId);

      session.save(oGeneral);
      session.getTransaction().commit();
    }
  }
  /**
   * Construct {@link org.stbot.stock.model.General General} object using data from csv files
   * and default all stocks to {@link org.stbot.stock.biz.Status#TRADED}.
   * @param aData List of string data from csv files.
   * @param iCieId
   * @return {@link org.stbot.stock.model.General General} object
   */
  private General getGeneral(List<String> aData, final int iCieId)
  {
    // Order from CSV must be taking into acccount.          
    int i = 0;
    String sName        = aData.get(i++).trim();
    String sSymbol      = aData.get(i++).trim();
    
    // Logging
    if(log.isInfoEnabled())
    {
      log.info("{}: Parsing symbol {} from csv file ...", this.getName(), sSymbol);
    }
    
    // Assign values to General object.
    General oGeneral = new General();
    oGeneral.setCie_id(iCieId);
    oGeneral.setDate(new Date(new java.util.Date().getTime()));
    oGeneral.setName(sName);
    oGeneral.setStatus(Status.TRADED); // By default, cie is active.
    oGeneral.setExchange(this.getName());
    
    return oGeneral;
  }    
}
