package net.silthus.slib.config.builder;

import org.bukkit.entity.Player;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mdoering
 */
public interface ConfigGenerator {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Information {

        String value();

        String desc();

        Class<?> type() default Player.class;

        String[] conf() default {};

        String[] aliases() default {};
    }
}
