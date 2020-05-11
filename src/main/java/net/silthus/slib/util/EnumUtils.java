package net.silthus.slib.util;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public final class EnumUtils {

    // util class
    private EnumUtils() {

    }

    /**
     * Get the enum value of a string, null if it doesn't exist.
     */
    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {

        if (c != null && string != null) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    /**
     * Get the enum value of a string, null if it doesn't exist.
     */
    public static <T extends Enum<T>> T getEnumFromStringCaseSensitive(Class<T> c, String string) {

        if (c != null && string != null) {
            try {
                return Enum.valueOf(c, string);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    /**
     * Parses the string into an enum set.
     * The string will be split at | characters.
     *
     * @param enumClass to parse enums into.
     * @param string    to split and parse
     * @param <T>       type of the enum
     * @return set of enum flags
     */
    public static <T extends Enum<T>> EnumSet<T> getEnumSetFromString(Class<T> enumClass, String string) {

        if (string == null || string.isEmpty()) {
            return EnumSet.noneOf(enumClass);
        }

        return Sets.newEnumSet(Arrays.stream(string.trim().split("\\|"))
                .map(part -> getEnumFromString(enumClass, part))
                .collect(Collectors.toList()), enumClass);
    }
}
