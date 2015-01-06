package org.stbot.stock.biz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Amex extends Exchange
{
  private static final Logger log = LoggerFactory.getLogger(Amex.class);
  
  public Amex()
  {
    super.m_sName = "AMEX";
    super.m_sCsvUrl = "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=amex&render=download";
    super.m_iCsvTokenSize = 10;
  }
  
}
