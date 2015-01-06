package org.stbot.stock.scheduler;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stbot.common.utils.Chronometer;
import org.stbot.stock.biz.Amex;
import org.stbot.stock.biz.Nasdaq;
import org.stbot.stock.biz.Nyse;
//import org.stbot.stock.biz.Otcbb;
import org.stbot.stock.biz.PriceBiz;

/**
 * Get price of each stock.
 * @author Xuan Ngo
 *
 */
public class Prices
{
  private static final Logger log = LoggerFactory.getLogger(Prices.class);
  
  public static void main(String[] args)
  {
    // Start the Chronometer.
    Chronometer oChronometer = new Chronometer();
    oChronometer.start();
    
    // Main application starts here.
    Prices oPrices = new Prices();
    oPrices.run();
    
    // Stop the Chronometer.
    oChronometer.stop();
    oChronometer.report("Update prices");
    
  }
  
  public void run()
  {
    // Update cie daily prices.
    PriceBiz oPriceBiz = new PriceBiz();
    oPriceBiz.updateAll();
  }
  
  /**
   * Return today's time at the defined hour or 
   *  if current time already passed the defined hour, then return tomorrow's time at the same defined hour.
   * @param iHour Defined hour.
   * @return Time in millis at the defined hour.
   */
  private long getNextTimeAt(final int iHour)
  {
    return this._getNextTimeAt(iHour, Calendar.getInstance());
  }

  /**
   * Return oCurrDateTime's time at the defined hour or 
   *  if oCurrDateTime already passed the defined hour, then return tomorrow's time at the same defined hour.
   * @param iHour Defined hour.
   * @param oCurrDateTime
   * @return Time in millis at the defined hour.
   */
  private long _getNextTimeAt(final int iHour, final Calendar oCurrDateTime)
  {
    // Swing between util.Date & sql.Date to reset time to 00:00:00.
    java.util.Date oDateOnly = oCurrDateTime.getTime();
    java.sql.Date oSqlDate = new java.sql.Date(oDateOnly.getTime());
    oSqlDate = java.sql.Date.valueOf(oSqlDate.toString()); //Java Spec: java.sql.Date.toString() formats a date in the date escape format yyyy-mm-dd.

    // Make today's date time to iHour(19:00:00)
    final long MILLIS_TO_iHour = 60*60*iHour*1000; // Number of milliseconds for iHour(19:00:00).
    long lTodayInMillis = oSqlDate.getTime()+MILLIS_TO_iHour;
    
    final long MILLIS_FOR_1DAY = 60*60*24*1000; // Number of milliseconds for 1 day.
    long lNextTime = 0;
    if(oCurrDateTime.getTimeInMillis()>lTodayInMillis)
    {// Current time is passed iHour(19:00:00). Therefore, return next's day at iHour(19:00:00).
      // Get tomorrow's date and time.
      lNextTime = lTodayInMillis+MILLIS_FOR_1DAY;
    }
    else
    {// Current time is not passed iHour(19:00:00).
      lNextTime = lTodayInMillis;
    }
    
    // If lNextTime is on the weekends, then give date time for monday.
    Calendar oTmpCalendar = Calendar.getInstance();
    oTmpCalendar.setTimeInMillis(lNextTime);
    final int iDayOfWeek = oTmpCalendar.get(Calendar.DAY_OF_WEEK);
    if(iDayOfWeek==Calendar.SATURDAY)
    {
      lNextTime = lNextTime + 2*MILLIS_FOR_1DAY;
    }
    else if(iDayOfWeek==Calendar.SUNDAY)
    {
      lNextTime = lNextTime + MILLIS_FOR_1DAY;
    }
    
    return lNextTime;
  }
  private long getPauseTime(final long lNextTimeInMillis)
  {
    return _getPauseTime(Calendar.getInstance().getTimeInMillis(), lNextTimeInMillis);
  }
  
  private long _getPauseTime(final long lCurrDateTime, final long lNextTimeInMillis)
  {
    final long lPauseTimeInMillis = lNextTimeInMillis - lCurrDateTime; // lNextTimeInMillis is guaranteed to be always bigger than current's date and time.
    
    if(lPauseTimeInMillis<0)
      throw new RuntimeException("_getPauseTime("+lCurrDateTime+", "+lNextTimeInMillis+") can't return negative number. Your input parameters are incorrect.");
    return lPauseTimeInMillis;
  }

}


























/*

drop table Generals;
CREATE TABLE Generals (
  id INT(10) NOT NULL AUTO_INCREMENT,
  cie_id INT NOT NULL, 
  date DATE NOT NULL,
  name VARCHAR(255) NOT NULL,
  status INT NOT NULL, 
  securityType VARCHAR(255),
  exchange VARCHAR(50) NOT NULL,
  outstanding BIGINT,
  marketValue NUMERIC(20,4),
  description TEXT,
  PRIMARY KEY(id),
  FOREIGN KEY (cie_id) REFERENCES Cies(id)
);


drop table Symbols;
CREATE TABLE Symbols (
  id INT(10) NOT NULL AUTO_INCREMENT,
  cie_id INT NOT NULL, 
  date DATE NOT NULL,
  symbol VARCHAR(10) NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY (cie_id) REFERENCES Cies(id)
);  

drop table Prices;
CREATE TABLE Prices (
  cie_id INT NOT NULL, 
  date DATE NOT NULL,
  open NUMERIC(12,4) NOT NULL,
  high NUMERIC(12,4) NOT NULL,
  low NUMERIC(12,4) NOT NULL,
  close NUMERIC(12,4) NOT NULL,
  volume BIGINT NOT NULL,
  adjClose NUMERIC(12,4),
  PRIMARY KEY(cie_id, date),
  FOREIGN KEY (cie_id) REFERENCES Cies(id)
); 


drop table Jobs;
CREATE TABLE Jobs (
  id INT(10) NOT NULL AUTO_INCREMENT,
  cie_id INT NOT NULL, 
  dateTime DATETIME NOT NULL,
  status INT,
  message TEXT,
  PRIMARY KEY(id),
  FOREIGN KEY (cie_id) REFERENCES Cies(id)
); 


drop table Urls;
CREATE TABLE Urls (
  id INT(10) NOT NULL AUTO_INCREMENT,
  cie_id INT NOT NULL, 
  date DATE NOT NULL,
  type INT NOT NULL,
  url VARCHAR(255) NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY (cie_id) REFERENCES Cies(id)
);  


drop table Cies;
CREATE TABLE Cies (
  id INT(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY(id)
);  









drop table Rules;
CREATE TABLE Rules (
  id INT(10) NOT NULL AUTO_INCREMENT,
  date DATE NOT NULL,
  unit NUMERIC(12,4) NOT NULL,
  weight NUMERIC(12,4) NOT NULL,
  description TEXT,
  PRIMARY KEY(id)
); 

drop table Scores;
CREATE TABLE Scores (
  cie_id INT NOT NULL, 
  rule_id INT NOT NULL, 
  date DATE NOT NULL,
  parameter VARCHAR(255),
  point NUMERIC(12,4) NOT NULL,
  PRIMARY KEY(cie_id, rule_id),
  FOREIGN KEY (cie_id) REFERENCES Cies(id),
  FOREIGN KEY (rule_id) REFERENCES Rules(id)
); 




















mysql -uroot -pmypassword

*/