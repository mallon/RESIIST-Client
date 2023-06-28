package com.axellience.resiist.client.acquisitiontointegration.dtos;

import java.util.Date;

public class NamedCustomPropertyDto extends CustomPropertyDto
{
    private String name;

    public NamedCustomPropertyDto()
    {
    }

    public NamedCustomPropertyDto(String name, String value, Date date)
    {
        super(value, date);
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
