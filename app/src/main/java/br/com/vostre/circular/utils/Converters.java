package br.com.vostre.circular.utils;

import android.arch.persistence.room.TypeConverter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class Converters {

    @TypeConverter
    public static DateTime fromTimestamp(Long value) {

        if(value != null){
            DateTime dateTime = new DateTime();
            dateTime.withDate(new LocalDate(value));

            return dateTime;
        } else{
            return null;
        }

    }

    @TypeConverter
    public static Long dateTimeToTimestamp(DateTime dateTime) {
        return dateTime == null ? null : dateTime.getMillis();
    }

}
