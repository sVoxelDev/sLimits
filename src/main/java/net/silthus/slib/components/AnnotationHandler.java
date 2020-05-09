package net.silthus.slib.components;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author zml2008
 */
public interface AnnotationHandler<T extends Annotation> {
    boolean handle(AbstractComponent component, Field field, T annotation);
}
