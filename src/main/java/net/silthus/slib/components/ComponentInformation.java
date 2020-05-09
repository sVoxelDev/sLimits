package net.silthus.slib.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides useful information about an {@link AbstractComponent}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentInformation {

    /**
     * The unique name of the component. This name can be used in the components.yml config to disable
     * the component.
     *
     * @return name of the component.
     */
    String value();

    /**
     * A name for this component that users see.
     *
     * @return This component's friendly name.
     */
    String friendlyName() default "";

    /**
     * An array of authors involved in the creation of this component.
     *
     * @return The authors.
     */
    String[] authors() default "";

    /**
     * A short description of this component to be used, for example, on a component help page.
     *
     * @return The description.
     */
    String desc();
}
