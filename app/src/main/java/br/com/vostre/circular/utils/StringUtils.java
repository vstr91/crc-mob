package br.com.vostre.circular.utils;

import com.imangazaliev.slugify.Slugify;

import java.text.Normalizer;

public class StringUtils {

    public static String toSlug(String input) {
        Slugify slugify = new Slugify();
        return slugify.slugify(input);
    }

    public static String removeAcentos(String input){
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

}
