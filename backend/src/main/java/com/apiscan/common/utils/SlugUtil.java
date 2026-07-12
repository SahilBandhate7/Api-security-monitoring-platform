package com.apiscan.common.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility for generating URL-safe slugs from organization names.
 */
public final class SlugUtil {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGE_DASHES = Pattern.compile("(^-|-$)");

    private SlugUtil() {
    }

    /**
     * Converts a string to a URL-safe slug.
     * Example: "My Organization Name" → "my-organization-name"
     */
    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Slug input cannot be null or empty");
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = WHITESPACE.matcher(normalized).replaceAll("-");
        slug = NON_LATIN.matcher(slug).replaceAll("");
        slug = EDGE_DASHES.matcher(slug).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}
