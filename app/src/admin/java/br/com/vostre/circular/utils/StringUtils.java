package br.com.vostre.circular.utils;

import com.imangazaliev.slugify.Slugify;

public class StringUtils {

    public static String toSlug(String input) {
        Slugify slugify = new Slugify();
        return slugify.slugify(input);
    }

}
