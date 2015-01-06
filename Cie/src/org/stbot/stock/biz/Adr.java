package org.stbot.stock.biz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Adr extends Exchange
{
  private static final Logger log = LoggerFactory.getLogger(Adr.class);
  
  public Adr()
  {
    super.m_sName = "ADR";
    super.m_sCsvUrl = "http://www.nasdaq.com/screening/companies-by-industry.aspx?exchange=NASDAQ&market=ADR&render=download";
    super.m_iCsvTokenSize = 10;
  }
  
}
