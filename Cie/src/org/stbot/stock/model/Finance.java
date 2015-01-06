package org.stbot.stock.model;

import java.sql.Date;

public class Finance
{
  private int cie_id;
  private Date date;
  private Double targetPrice;
  private Long floatShare;
  
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
  public Double getTargetPrice()
  {
    return targetPrice;
  }
  public void setTargetPrice(Double targetPrice)
  {
    this.targetPrice = targetPrice;
  }
  public Long getFloatShare()
  {
    return floatShare;
  }
  public void setFloatShare(Long floatShare)
  {
    this.floatShare = floatShare;
  }
    

}
