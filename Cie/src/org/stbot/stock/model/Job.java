package org.stbot.stock.model;

import java.sql.Timestamp;

public class Job
{
  private int id;
  private int cie_id;
  private Timestamp dateTime;
  private int status;
  private String message;
  
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
  public Timestamp getDateTime()
  {
    return dateTime;
  }
  public void setDateTime(Timestamp dateTime)
  {
    this.dateTime = dateTime;
  }
  public int getStatus()
  {
    return status;
  }
  public void setStatus(int status)
  {
    this.status = status;
  }
  public String getMessage()
  {
    return message;
  }
  public void setMessage(String message)
  {
    this.message = message;
  }

  
  
}
