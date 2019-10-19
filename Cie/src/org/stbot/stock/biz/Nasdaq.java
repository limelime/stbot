package org.stbot.stock.biz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nasdaq extends Exchange
{
  private static final Logger log = LoggerFactory.getLogger(Nasdaq.class);
  
  public Nasdaq()
  {
    super.m_sName = "NASDAQ";
    super.m_sCsvUrl = "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nasdaq&render=download";
    super.m_iCsvTokenSize = 10;
  }
  
}
