package com.axellience.resiist.client.acquisitiontointegration.dtos;

import java.util.Date;

public class CustomPropertyDto
{
    private String value;
    private Date   date;

    public CustomPropertyDto()
    {
    }

    public CustomPropertyDto(String value, Date date)
    {
        this.value = value;
        this.date = date;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

}
