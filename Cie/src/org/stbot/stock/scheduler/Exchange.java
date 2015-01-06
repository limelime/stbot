package org.stbot.stock.scheduler;

import org.stbot.common.utils.Chronometer;
import org.stbot.stock.biz.Amex;
import org.stbot.stock.biz.Nasdaq;
import org.stbot.stock.biz.Nyse;
import org.stbot.stock.biz.Otcbb;
import org.stbot.stock.biz.Adr;


/**
 * Get the list of stocks from exchanges.
 * @author Xuan Ngo
 *
 */
public class Exchange
{
  public static void main(String[] args)
  {
    // Start the Chronometer.
    Chronometer oChronometer = new Chronometer();
    oChronometer.start();
    
    // Main application starts here.
    Exchange oExchange = new Exchange();
    oExchange.run();
    
    // Stop the Chronometer.
    oChronometer.stop();
    oChronometer.report("Get the list of stocks from exchanges");
    
  }
  
  public void run()
  {
    // Update cie data from exchanges.
    this.updateCiesFromExchanges();
  }
  
  private void updateCiesFromExchanges()
  {
    Amex oAmex = new Amex();
    oAmex.update();
   
    Nasdaq oNasdaq = new Nasdaq();
    oNasdaq.update();
    
    Nyse oNyse = new Nyse();
    oNyse.update();
    
//    Otcbb oOtcbb = new Otcbb(); // 2012-10-05: List of stocks on OTCBB exchange is not available anymore.
//    oOtcbb.update();

    Adr oAdr = new Adr();
    oAdr.update();    
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