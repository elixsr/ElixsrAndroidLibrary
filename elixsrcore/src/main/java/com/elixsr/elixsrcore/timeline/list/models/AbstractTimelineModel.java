package com.elixsr.elixsrcore.timeline.list.models;

import java.util.Date;

/**
 * Created by Niall McShane on 12/06/2016.
 */
public abstract class AbstractTimelineModel {

    public abstract String getMonthHeadingText();

    public abstract String getDayHeadingText();

    public abstract String getYearHeadingText();

    public abstract Date getDate();
}
