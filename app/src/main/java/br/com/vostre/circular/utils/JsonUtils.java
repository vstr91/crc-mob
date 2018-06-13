package br.com.vostre.circular.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.lang.reflect.Type;
import java.util.List;

import br.com.vostre.circular.model.EntidadeBase;

public class JsonUtils {

    public static JsonSerializer<DateTime> serDateTime = new JsonSerializer<DateTime>() {
        @Override
        public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext
                context) {
            return src == null ? null : new JsonPrimitive(DateTimeFormat.forPattern("dd-MM-yyyy HH:mm").print(src));
        }
    };

    public static JsonDeserializer<DateTime> deserDateTime = new JsonDeserializer<DateTime>() {
        @Override
        public DateTime deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
            return json == null ? null : new DateTime(json.getAsLong());
        }
    };

    public static String toJson(EntidadeBase dado){

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, JsonUtils.serDateTime)
                .registerTypeAdapter(DateTime.class, JsonUtils.deserDateTime)
                .create();
        return gson.toJson(dado);
    }

    public static String toJson(List<EntidadeBase> dados){

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, JsonUtils.serDateTime)
                .registerTypeAdapter(DateTime.class, JsonUtils.deserDateTime)
                .create();
        return gson.toJson(dados);
    }

}
