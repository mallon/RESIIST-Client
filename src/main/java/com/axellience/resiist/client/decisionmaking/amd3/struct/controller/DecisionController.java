package com.axellience.resiist.client.decisionmaking.amd3.struct.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.axellience.resiist.client.acquisitiontointegration.dtos.CustomPropertyDto;
import com.axellience.resiist.client.acquisitiontointegration.dtos.NamedCustomPropertyDto;
import com.axellience.resiist.client.acquisitiontointegration.integration.IntegrationPlatform;

@Controller
public class DecisionController
{
    private Map<String, List<NamedCustomPropertyDto>> previousDateAndCustomProperties =
            new LinkedHashMap<>();

    @GetMapping("decision/history/{decisionModelReportId}")
    public ModelAndView getCustompropertyHistoryPage(@PathVariable String decisionModelReportId,
                                                     @RequestParam List<String> decisions,
                                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date beginDate,
                                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
    {
        ModelAndView modelAndView = new ModelAndView("barchartpage.html");

        Map<String, List<NamedCustomPropertyDto>> dateAndCustomProperties = new LinkedHashMap<>();

        IntegrationPlatform integrationPlatform = IntegrationPlatform.getInstance();
        for (String decision : decisions) {
            List<CustomPropertyDto> customPropertyDtos =
                    integrationPlatform.getCustomPropertyDto(decisionModelReportId,
                                                             decision,
                                                             beginDate,
                                                             endDate);

            for (CustomPropertyDto customPropertyDto : customPropertyDtos) {
                String cpDate = customPropertyDto.getDate().toString();
                if (dateAndCustomProperties.get(cpDate) == null) {
                    dateAndCustomProperties.put(cpDate, new ArrayList<>());
                }
                NamedCustomPropertyDto namedCustomPropertyDto = new NamedCustomPropertyDto();
                namedCustomPropertyDto.setValue(customPropertyDto.getValue());
                namedCustomPropertyDto.setName(decision);
                dateAndCustomProperties.get(cpDate).add(namedCustomPropertyDto);
            }

        }

        // TODO - Correct later (due to GMM server not recording same value at
        // tn and tn+1)
        correctEmptyValues(dateAndCustomProperties, decisions);

        Map<String, List<NamedCustomPropertyDto>> orderedDateAndCustomProperties =
                orderLinkedMap(dateAndCustomProperties);

        previousDateAndCustomProperties.clear();
        previousDateAndCustomProperties.putAll(orderedDateAndCustomProperties);

        modelAndView.addObject("dateAndCustomProperties", orderedDateAndCustomProperties);
        return modelAndView;
    }

    private void correctEmptyValues(Map<String, List<NamedCustomPropertyDto>> dateAndCustomProperties,
                                    List<String> decisions)
    {
        for (String dateKey : dateAndCustomProperties.keySet()) {
            List<NamedCustomPropertyDto> customPropertyDtos = dateAndCustomProperties.get(dateKey);
            if (customPropertyDtos.size() < decisions.size()) {

                List<String> decisionsWithoutValue = new ArrayList<>(decisions);
                // List<String> decisionsInCPD = new ArrayList<>();
                // for (NamedCustomPropertyDto customPropertyDto :
                // customPropertyDtos) {
                // decisionsInCPD.add(customPropertyDto.getName());
                // }

                for (String decision : decisions) {
                    for (NamedCustomPropertyDto customPropertyDto : customPropertyDtos) {
                        if (decision.equals(customPropertyDto.getName())) {
                            decisionsWithoutValue.remove(decision);
                        }
                    }
                }

                for (String decisionWithoutValue : decisionsWithoutValue) {
                    NamedCustomPropertyDto namedCustomPropertyDto = new NamedCustomPropertyDto();
                    String previousValue = getPreviousValue(dateAndCustomProperties,
                                                            decisionWithoutValue,
                                                            dateKey);
                    System.out.println("decision Without Value : " + decisionWithoutValue);
                    System.out.println("Previous value : " + previousValue);
                    namedCustomPropertyDto.setValue(previousValue);
                    namedCustomPropertyDto.setName(decisionWithoutValue);
                    dateAndCustomProperties.get(dateKey).add(namedCustomPropertyDto);
                }
            }
        }
    }

    private Map<String, List<NamedCustomPropertyDto>> orderLinkedMap(Map<String, List<NamedCustomPropertyDto>> dateAndCustomProperties)
    {
        List<Date> orderedDates = new ArrayList<>();
        Set<String> allDateSet = dateAndCustomProperties.keySet();
        for (String dateAsString : allDateSet) {
            Date date = parseToDate(dateAsString);
            orderedDates.add(date);
        }
        Collections.sort(orderedDates);

        Map<String, List<NamedCustomPropertyDto>> orderedDateAndCustomProperties =
                new LinkedHashMap<>();
        for (Date dateKey : orderedDates) {
            String dateToString = dateKey.toString();
            List<NamedCustomPropertyDto> customPropertyDto =
                    dateAndCustomProperties.get(dateToString);
            orderedDateAndCustomProperties.put(dateToString, customPropertyDto);
        }

        return orderedDateAndCustomProperties;

    }

    private String getPreviousValue(Map<String, List<NamedCustomPropertyDto>> dateAndCustomProperties,
                                    String decisionWithoutValue, String dateLimit)
    {
        List<NamedCustomPropertyDto> previousNCPList =
                getPreviousNCPList(dateAndCustomProperties, dateLimit);

        if (previousNCPList == null)
            return "0.0";

        NamedCustomPropertyDto previousDecisionWithValue = null;
        for (NamedCustomPropertyDto ncp : previousNCPList) {
            if (decisionWithoutValue.equals(ncp.getName())) {
                previousDecisionWithValue = ncp;
            }
        }

        if (previousDecisionWithValue == null)
            return "0.0";

        return previousDecisionWithValue.getValue();
    }

    private List<NamedCustomPropertyDto> getPreviousNCPList(Map<String, List<NamedCustomPropertyDto>> dateAndCustomProperties,
                                                            String dateLimit)
    {
        String previousDate = "";
        ListIterator<String> keyListIterator =
                new ArrayList<String>(previousDateAndCustomProperties.keySet()).listIterator();
        while (keyListIterator.hasNext()) {
            String currentDate = keyListIterator.next();
            if (currentDate.equals(dateLimit) && keyListIterator.hasPrevious()) {
                previousDate = keyListIterator.previous();
                break;
            }
        }

        if (previousDate != null) {
            return previousDateAndCustomProperties.get(previousDate);
        }
        // List<NamedCustomPropertyDto> previousNCPList =
        // Collections.emptyList();
        // Date parsedDateLimit = parseToDate(dateLimit);
        // Date previousStockedDate = null;
        // for (String currentDate : dateAndCustomProperties.keySet()) {
        // Date parsedCurrentDate = parseToDate(currentDate);
        // if (parsedCurrentDate.compareTo(parsedDateLimit) < 0
        // && ((previousStockedDate != null
        // && parsedCurrentDate.compareTo(previousStockedDate) > 0)
        // || previousStockedDate == null))
        // {
        //
        // previousNCPList = dateAndCustomProperties.get(currentDate);
        // }
        //
        // previousStockedDate = parsedCurrentDate;
        // }

        return Collections.emptyList();
    }

    private Date parseToDate(String dateToParse)
    {
        // Be careful to consider the correct date format returned by
        // the GMM server
        TimeZone tz = TimeZone.getTimeZone("CEST");
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        dateFormat.setTimeZone(tz);

        try {
            return dateFormat.parse(dateToParse);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean isPresent(String decision, List<NamedCustomPropertyDto> customPropertyDtos)
    {
        return customPropertyDtos.stream().anyMatch(dto -> dto.getName().equals(decision));
    }
}
