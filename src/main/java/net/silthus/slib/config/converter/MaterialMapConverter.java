package net.silthus.slib.config.converter;

import de.exlll.configlib.Converter;
import org.bukkit.Material;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public final class MaterialMapConverter
        implements Converter<Map<Material, Double>, Map<String, Double>> {

    @Override
    public Map<String, Double> convertTo(Map<Material, Double> element, ConversionInfo info) {
        return element.entrySet().stream()
                .collect(toMap(o -> o.getKey().toString(), Map.Entry::getValue));
    }

    @Override
    public Map<Material, Double> convertFrom(Map<String, Double> element, ConversionInfo info) {
        return element.entrySet().stream()
                .collect(toMap(o -> Material.matchMaterial(o.getKey()), Map.Entry::getValue));
    }
}
