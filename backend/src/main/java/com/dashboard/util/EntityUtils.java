package com.dashboard.util;

import java.beans.Transient;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class EntityUtils {
    public static int getNonIdFieldCount(Class<?> clazz) {
        return (int) Arrays.stream(clazz.getDeclaredFields())
            .filter(f -> !f.getName().equals("id"))
            .filter(f -> !Modifier.isStatic(f.getModifiers()))
            .filter(f -> !f.isAnnotationPresent(Transient.class))
            .count();
    }
}