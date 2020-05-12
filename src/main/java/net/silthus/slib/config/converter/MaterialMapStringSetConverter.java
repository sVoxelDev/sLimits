package net.silthus.slib.config.converter;

import de.exlll.configlib.Converter;
import org.bukkit.Material;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

public final class MaterialMapStringSetConverter
        implements Converter<Map<Material, Set<String>>, Map<String, Set<String>>> {

    @Override
    public Map<String, Set<String>> convertTo(Map<Material, Set<String>> element, ConversionInfo info) {
        return element.entrySet().stream()
                .collect(toMap(o -> o.getKey().toString(), Map.Entry::getValue));
    }

    @Override
    public Map<Material, Set<String>> convertFrom(Map<String, Set<String>> element, ConversionInfo info) {
        return element.entrySet().stream()
                .collect(toMap(o -> Material.matchMaterial(o.getKey()), Map.Entry::getValue));
    }
}
