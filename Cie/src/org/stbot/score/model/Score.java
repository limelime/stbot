package org.stbot.score.model;

import java.io.Serializable;
import java.sql.Date;

public class Score implements Serializable
{
  private int cie_id; 
  private int rule_id; 
  private Date date;
  private double point;
  private double realpoint;
  
  public int getCie_id()
  {
    return cie_id;
  }
  public void setCie_id(int cie_id)
  {
    this.cie_id = cie_id;
  }
  public int getRule_id()
  {
    return rule_id;
  }
  public void setRule_id(int rule_id)
  {
    this.rule_id = rule_id;
  }
  public Date getDate()
  {
    return date;
  }
  public void setDate(Date date)
  {
    this.date = date;
  }
  public double getPoint()
  {
    return point;
  }
  public void setPoint(double point)
  {
    this.point = point;
  }
  public double getRealpoint()
  {
    return realpoint;
  }
  public void setRealpoint(double realpoint)
  {
    this.realpoint = realpoint;
  }
  
}
