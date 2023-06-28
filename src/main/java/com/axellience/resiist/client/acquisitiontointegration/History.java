package com.axellience.resiist.client.acquisitiontointegration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.axellience.resiist.client.utils.ResiistConstants;
import com.axellience.resiist.client.utils.gui.BrowserUtil;

public class History
{

    private String projectId;

    public History(String projectId, List<String> indicatorIds)
    {
        this.projectId = projectId;
    }

    public void launchGlobalResilienceHistory()
    {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        df.setTimeZone(tz);
        String beginDate = df.format(new Date());

        String endDate = "";
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(df.parse(beginDate));
            c.add(Calendar.DATE, 1);
            endDate = df.format(c.getTime());
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        BrowserUtil.openUrlInBrowser(ResiistConstants.APIURL
                                     + "/resilience/history/page/"
                                     + projectId
                                     + "/"
                                     + projectId
                                     + "?customproperties=resilience"
                                     + "&beginDate="
                                     + beginDate
                                     + "&endDate="
                                     + endDate);

    }

    public void launchGlobalDecisionEvaluationHistory(String decisionModelReportId,
                                                      String decisionList)
    {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        df.setTimeZone(tz);
        String beginDate = df.format(new Date());

        String endDate = "";
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(df.parse(beginDate));
            c.add(Calendar.DATE, 1);
            endDate = df.format(c.getTime());
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        String cleanDecisionList = decisionList.replace(" ", "");

        BrowserUtil.openUrlInBrowser(ResiistConstants.DECISIONURL
                                     + "/decision/history/"
                                     + decisionModelReportId
                                     + "?decisions="
                                     + cleanDecisionList
                                     + "&beginDate="
                                     + beginDate
                                     + "&endDate="
                                     + endDate);
    }

}