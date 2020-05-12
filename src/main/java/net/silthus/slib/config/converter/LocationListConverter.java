package net.silthus.slib.config.converter;

import de.exlll.configlib.Converter;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocationListConverter implements Converter<List<Location>, List<Map<String, Object>>> {
    @Override
    public List<Map<String, Object>> convertTo(List<Location> locations, ConversionInfo conversionInfo) {

        return locations.stream().map(Location::serialize).collect(Collectors.toList());
    }

    @Override
    public List<Location> convertFrom(List<Map<String, Object>> locations, ConversionInfo conversionInfo) {
        return locations.stream().map(Location::deserialize).collect(Collectors.toList());
    }
}
