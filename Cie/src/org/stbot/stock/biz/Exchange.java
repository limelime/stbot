package org.stbot.stock.biz;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProxySelector;
import java.net.URL;
import java.sql.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stbot.common.utils.DateUtil;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.common.utils.Unique;
import org.stbot.stock.model.General;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.btr.proxy.search.ProxySearch;



/*
//http://www.nasdaq.com/screening/company-list.aspx
//http://www.nasdaq.com/asp/comlookup.asp?page=empty
//Exchange name, url, token size
{"NASDAQ", "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nasdaq&render=download", "9"},
{"AMEX"  , "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=amex&render=download", "9"}, 
{"NYSE"  , "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nyse&render=download", "9"},

{"NASDAQ", "http://www.nasdaq.com/asp/symbols.asp?exchange=Q&start=0", "6"},
{"AMEX"  , "http://www.nasdaq.com/asp/symbols.asp?exchange=1&start=0", "4"}, 
{"NYSE"  , "http://www.nasdaq.com/asp/symbols.asp?exchange=N&start=0", "4"},
*/


/**
 * Provide exchanges' related data to other classes:
 * <ul>
 * <li>Exchange constants</li>
 * <li>Exchange strings</li>
 * <li>Exchange urls to csv files</li>
 * <li>Token size of csv files</li>
 * <li>Construct data from csv files as {@link org.stbot.stock.model.General General} object</li>
 * </ul>
 * @author Xuan Ngo
 *
 */
public abstract class Exchange
{
  private static final Logger log = LoggerFactory.getLogger(Exchange.class);
  
  protected String m_sCsvUrl = "";
  protected int m_iCsvTokenSize = 0;
  protected String m_sName = "";
  protected final String m_sExchangeDirPath = "archives/exchanges/"; // It has to end with /
  
  
  public Exchange()
  {}
  
  /**
   * Get the name of the exchange.
   * @return Return of the exchange.
   */
  public final String getName()
  {
    return this.m_sName;
  }
  
  /**
   * The URL of CSV file.
   * @return Return the URL of CSV file 
   */
  public final String getCsvUrl()
  {
    return this.m_sCsvUrl;
  }
  
  /**
   * The number of token in the CSV file.
   * @return The number of token in the CSV file.
   */
  public final int getCsvTokenSize()
  {
    return this.m_iCsvTokenSize;
  }
  
  /**
   * Download CSV files. 
   * If there is no internet connection, it keeps trying 
   * to download the file until it gets it.
   * Note: This function is public because it is generic.
   * @param sOutputFilename
   */
  public void downloadCsv(final String sOutputFilename)
  {
    boolean bGoodInternetConnection = true;
    do
    {
      try
      {
        // Use Proxy if applicable.
        ProxySearch proxySearch = ProxySearch.getDefaultProxySearch();
        ProxySelector myProxySelector = proxySearch.getProxySelector();
        ProxySelector.setDefault(myProxySelector);
        
        // Prepare reading data from the internet.
        URL Url = new URL( this.getCsvUrl() );
        HttpURLConnection HttpUrlConn = (HttpURLConnection)Url.openConnection();
//        HttpUrlConn.setReadTimeout(30*1000);
        BufferedReader InputBuffer = new BufferedReader( new InputStreamReader(HttpUrlConn.getInputStream()) );
        
        // Prepare write data to file.
        FileWriter oFileWriter = new FileWriter(sOutputFilename);
        BufferedWriter oBufferedWriter = new BufferedWriter(oFileWriter);
        
        // Write data to file.
        String sLine;
        while( (sLine = InputBuffer.readLine()) !=null )
        {
          oBufferedWriter.write(sLine);
          oBufferedWriter.write("\n");
        }
        oBufferedWriter.close();
        bGoodInternetConnection = true;
      }
      catch(Exception e) // It is on purpose to use Exception to generalize the exception. Put all exceptions into 1 bucket.
      {
        if( IsInternetConnectionError(e.toString()) )
        {// Error in internet connection. Loop forever.
          bGoodInternetConnection = false;
          try
          {
            final int iSleepTime = 60*1000; // Sleep for 1 minute.
            if(log.isInfoEnabled())
            {
              log.info("Internet connection error: Sleep for {} miliseconds. Was retrieving {}", iSleepTime, this.getCsvUrl());
            }
            Thread.sleep(iSleepTime); 
          }
          catch( InterruptedException ex)
          {
            if(log.isErrorEnabled())
            {
              log.error("Can't Thread.sleep.", e);
            }
          }
        }
        else
        {// Unknown errors.
          if(log.isErrorEnabled())
          {
            log.error("Unknown Errors: Was retrieving " + this.getCsvUrl(), e);
          }
          bGoodInternetConnection = true; // Stop.
        }
      }
    }while(!bGoodInternetConnection);
  }

  private static final boolean IsInternetConnectionError( final String sError )
  {
    // Internet connection error strings.
    final String[] aExceptions = {  "UnknownHostException"  ,  
                                   "SocketTimeoutException",
                                   "SocketException",
                                   "NoRouteToHostException",
                                   "ConnectException"      
                                };
    for( int i=0; i<aExceptions.length; ++i )
    {
      if( sError.indexOf( aExceptions[i], 0 ) != -1 )
      {
        return true;
      }
    }
    return false;
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
    // Order from CSV must be taking into account.          
    int i = 0;
    String sSymbol      = aData.get(i++).trim();
    String sName        = aData.get(i++).trim();
    String sLastSale    = aData.get(i++); // No used: Skipped.
    String sMarketValue = aData.get(i++); // MarketValue = Market Cap.
    String sAdrTso      = aData.get(i++); // No used: Skipped.
    String sIpoYear     = aData.get(i++);
    String sSector      = aData.get(i++);
    String sIndustry    = aData.get(i++);
    
    // Logging
    if(log.isInfoEnabled())
    {
      log.info("{}: Parsing symbol {} from csv file ...", this.getName(), sSymbol);
    }
    
    // Parsing Market Value.
    Double dMarketValue = null;
    if(sMarketValue.compareToIgnoreCase("n/a") == 0 || sMarketValue.length() == 0)
    {
      //Don't assign any value to Market Value so that it will be NULL. 
    }
    else
    {
      dMarketValue = new Double(sMarketValue);
    }                    

    // Parsing IPO Year.
    Integer oIpoYear = null;
    if(sIpoYear.compareToIgnoreCase("n/a") == 0 || sIpoYear.length() == 0)
    {
      //Don't assign any value to IPO Year so that it will be NULL. 
    }
    else
    {
      oIpoYear = new Integer(sIpoYear);
    }
    
    // Assign values to General object.
    General oGeneral = new General();
    oGeneral.setCie_id(iCieId);
    oGeneral.setDate(new Date(new java.util.Date().getTime()));
    oGeneral.setName(sName);
    oGeneral.setStatus(Status.TRADED); // By default, cie is active.
    oGeneral.setExchange(this.getName());
    oGeneral.setMarketValue(dMarketValue);
    oGeneral.setIpoyear(oIpoYear);
    if(sSector.compareToIgnoreCase("n/a")!=0) // If equal to n/a, then NULL.
    {  oGeneral.setSector(sSector); }
    if(sIndustry.compareToIgnoreCase("n/a")!=0) // If equal to n/a, then NULL.
    {  oGeneral.setIndustry(sIndustry); }
    
    return oGeneral;
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
    this.downloadCsv(ExchangeCsvFilePath);
 
    try
    {
      BufferedReader oCsvStream = new BufferedReader(new FileReader(ExchangeCsvFilePath));
      // oCsvStream.readLine();  // Discard the 1st line, Date of file.

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
    //"Symbol","Name",
    int iIdx = 0;
    final String sSymbol    = aCsvData.get(iIdx++).trim(); // Assume that the 1st token is the symbol.
    final String sName      = aCsvData.get(iIdx++).trim();
    
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
    
    /*
    // Run once only to update ipoyear, sector and industry of old stocks.
    else
    {
      Session session = HibernateUtil.getSessionFactory().getCurrentSession();
      session.beginTransaction();
      
      final int ifake = 9568923; // Fake iCie_id
      General oGeneral = this.getGeneral(aCsvData, ifake);
      
      Query oQuery = session.createSQLQuery("UPDATE Generals SET ipoyear=:ipoyear, sector=:sector, industry=:industry WHERE cie_id in(:cie_id_list)");
      oQuery.setParameter("ipoyear", oGeneral.getIpoyear(), Hibernate.INTEGER);
      oQuery.setString("sector", oGeneral.getSector());
      oQuery.setString("industry", oGeneral.getIndustry());
      oQuery.setParameterList("cie_id_list", oSymbolBiz.findIds(sSymbol));
      int iRowCnt = oQuery.executeUpdate();
      log.info("\t{} => Rows affected: {}", sSymbol+": "+oGeneral.getIpoyear(), iRowCnt);
    }
    */
  }  
  
  
}
