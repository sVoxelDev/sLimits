package net.silthus.slib.config.converter;

import de.exlll.configlib.Converter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public final class MaterialMapLocationListConverter
        implements Converter<Map<Material, List<Location>>, Map<String, List<Map<String, Object>>>> {

    @Override
    public Map<String, List<Map<String, Object>>> convertTo(Map<Material, List<Location>> element, ConversionInfo info) {
        return element.entrySet().stream()
                .collect(toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue().stream()
                                .map(Location::serialize)
                                .collect(Collectors.toList())
                        )
                );
    }

    @Override
    public Map<Material, List<Location>> convertFrom(Map<String, List<Map<String, Object>>> element, ConversionInfo info) {
        return element.entrySet().stream()
                .collect(toMap(
                        entry -> Material.matchMaterial(entry.getKey()),
                        entry -> entry.getValue().stream().map(Location::deserialize).collect(Collectors.toList())
                        )
                );
    }
}
