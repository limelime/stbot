package org.stbot.stock.biz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nyse extends Exchange
{
  private static final Logger log = LoggerFactory.getLogger(Nyse.class);
  
  public Nyse()
  {
    super.m_sName = "NYSE";
    super.m_sCsvUrl = "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nyse&render=download";
    super.m_iCsvTokenSize = 10;
  }
  
}
