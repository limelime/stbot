package org.stbot.stock.model;

import java.sql.Date;

public class Symbol
{
  private int id;
  private int cie_id;
  private Date date;
  private String symbol;
  private int user_id;
  
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
  public String getSymbol()
  {
    return symbol;
  }
  public void setSymbol(String symbol)
  {
    this.symbol = symbol;
  }
  public int getUser_id()
  {
    return user_id;
  }
  public void setUser_id(int user_id)
  {
    this.user_id = user_id;
  }
  
}
