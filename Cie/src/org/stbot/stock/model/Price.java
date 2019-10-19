package org.stbot.stock.model;

import java.sql.Date;
import java.io.Serializable;

public class Price implements Serializable
{
  private int id;
  private int cie_id;
  private Date date;
  private double open;
  private double high;
  private double low;
  private double close;
  private long volume;
  private double adjClose;
  public int getId()
  {
    return id;
  }
  public void setId(int id)
  {
    this.id = id;
  }
  public int getCie_id()
  {
    return cie_id;
  }
  public void setCie_id(int cie_id)
  {
    this.cie_id = cie_id;
  }
  public Date getDate()
  {
    return date;
  }
  public void setDate(Date date)
  {
    this.date = date;
  }
  public double getOpen()
  {
    return open;
  }
  public void setOpen(double open)
  {
    this.open = open;
  }
  public double getHigh()
  {
    return high;
  }
  public void setHigh(double high)
  {
    this.high = high;
  }
  public double getLow()
  {
    return low;
  }
  public void setLow(double low)
  {
    this.low = low;
  }
  public double getClose()
  {
    return close;
  }
  public void setClose(double close)
  {
    this.close = close;
  }
  public long getVolume()
  {
    return volume;
  }
  public void setVolume(long volume)
  {
    this.volume = volume;
  }
  public double getAdjClose()
  {
    return adjClose;
  }
  public void setAdjClose(double adjClose)
  {
    this.adjClose = adjClose;
  }
  
  
}
