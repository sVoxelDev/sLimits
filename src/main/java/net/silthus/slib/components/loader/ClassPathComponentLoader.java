package net.silthus.slib.components.loader;

import lombok.Getter;
import net.silthus.slib.components.AbstractComponent;

import java.io.File;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Getter
public abstract class ClassPathComponentLoader extends AbstractComponentLoader {

    private final JarFile jarFile;

    protected ClassPathComponentLoader(Logger logger, File configDir, JarFile jarFile) {
        super(logger, configDir);
        this.jarFile = jarFile;
    }

    @Override
    public Collection<AbstractComponent> loadComponents() {
        final List<AbstractComponent> components = new ArrayList<>();

        for (Class<?> componentClass :
                getClasses(jarFile).stream().filter(this::isComponentClass).collect(Collectors.toList())) {
            try {
                components.add(instantiateComponent(componentClass));
            } catch (Throwable t) {
                getLogger()
                        .warning("Error initializing component " + componentClass + ": " + t.getMessage());
                t.printStackTrace();
            }
        }

        return components;
    }

    private Set<Class<?>> getClasses(JarFile jarFile) {
        Set<Class<?>> classes = new HashSet<>();
        try {
            for (Enumeration<JarEntry> entry = jarFile.entries(); entry.hasMoreElements(); ) {
                JarEntry jarEntry = entry.nextElement();
                String name = jarEntry.getName().replace("/", ".");
                if (name.endsWith(".class"))
                    classes.add(Class.forName(name.substring(0, name.length() - 6)));
            }
            jarFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
}
