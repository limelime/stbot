package org.stbot.stock.model;

import java.sql.Date;

public class General
{
  private int id;
  private int cie_id;
  private Date date;
  private String name;
  private int status;
  private String securityType;
  private String exchange;
  private Long outstanding;
  private Double marketValue;
  private String description;
  private Integer ipoyear;
  private String sector;
  private String industry;
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
  public String getName()
  {
    return name;
  }
  public void setName(String name)
  {
    this.name = name;
  }
  public int getStatus()
  {
    return status;
  }
  public void setStatus(int status)
  {
    this.status = status;
  }
  public String getSecurityType()
  {
    return securityType;
  }
  public void setSecurityType(String securityType)
  {
    this.securityType = securityType;
  }
  public String getExchange()
  {
    return exchange;
  }
  public void setExchange(String exchange)
  {
    this.exchange = exchange;
  }
  public Long getOutstanding()
  {
    return outstanding;
  }
  public void setOutstanding(Long outstanding)
  {
    this.outstanding = outstanding;
  }
  public Double getMarketValue()
  {
    return marketValue;
  }
  public void setMarketValue(Double marketValue)
  {
    this.marketValue = marketValue;
  }
  public String getDescription()
  {
    return description;
  }
  public void setDescription(String description)
  {
    this.description = description;
  }
  public Integer getIpoyear()
  {
    return ipoyear;
  }
  public void setIpoyear(Integer ipoyear)
  {
    this.ipoyear = ipoyear;
  }
  public String getSector()
  {
    return sector;
  }
  public void setSector(String sector)
  {
    this.sector = sector;
  }
  public String getIndustry()
  {
    return industry;
  }
  public void setIndustry(String industry)
  {
    this.industry = industry;
  }
   
}
