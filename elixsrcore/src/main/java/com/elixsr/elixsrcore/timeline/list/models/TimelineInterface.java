package com.elixsr.elixsrcore.timeline.list.models;

import java.util.Date;

/**
 * Created by Niall McShane on 19/06/2016.
 */
public interface TimelineInterface {

    public String getMonthHeadingText();

    public String getDayHeadingText();

    public String getYearHeadingText();

    public Date getDate();

}
