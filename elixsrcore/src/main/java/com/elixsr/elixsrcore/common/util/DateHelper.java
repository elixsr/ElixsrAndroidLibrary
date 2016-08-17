package com.elixsr.elixsrcore.common.util;

import java.util.Date;

/**
 * Created by Niall McShane on 28/06/2016.
 *
 * Unix timestamp is the number of seconds
 *
 * Java util.date requires the number of milliseconds
 */
public class DateHelper {

    private final static int MILLISECONDS_IN_SECOND = 1000;

    public static Date unixTimestampToDate(long unixTimestamp){
        long numberMilliseconds = unixTimestamp * MILLISECONDS_IN_SECOND;

        return new Date(numberMilliseconds);
    }

    public static long dateToUnixTimestamp(Date date){
        long numberOfMilliseconds = date.getTime();

        return numberOfMilliseconds / MILLISECONDS_IN_SECOND;
    }

}
