package net.silthus.slib.components;

/**
 * A way for components to register deps on other things
 */
public @interface Depend {
    Class<?>[] components() default {};

    String[] plugins() default {};
}
