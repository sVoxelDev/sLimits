package net.silthus.slib.config.builder;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mdoering
 */
@Data
public class ConfigBuilder {

    public static Optional<ConfigGenerator.Information> getInformation(ConfigGenerator generator) {

        for (Method method : generator.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ConfigGenerator.Information.class)) {
                return Optional.of(method.getAnnotation(ConfigGenerator.Information.class));
            }
        }
        return Optional.empty();
    }

    public static List<ConfigGenerator.Information> getInformations(ConfigGenerator generator) {

        List<ConfigGenerator.Information> list = new ArrayList<>();
        for (Method method : generator.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ConfigGenerator.Information.class)) {
                list.add(method.getAnnotation(ConfigGenerator.Information.class));
            }
        }
        return list;
    }
}
