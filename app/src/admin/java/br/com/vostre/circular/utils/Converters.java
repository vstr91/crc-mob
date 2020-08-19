package br.com.vostre.circular.utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.lang.reflect.Type;
import java.util.List;
import java.util.TimeZone;

public class Converters {

    @TypeConverter
    public static DateTime fromTimestamp(Long value) {

        if(value != null){
            DateTime dateTime = new DateTime(value,
                    DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo")));
            //dateTime.withDate(new LocalDate(value));

            return dateTime;
        } else{
            return null;
        }

    }

    @TypeConverter
    public static Long dateTimeToTimestamp(DateTime dateTime) {
        return dateTime == null ? null : dateTime.getMillis();
    }

    @TypeConverter
    public static List<String> fromString(String value) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(List<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}
