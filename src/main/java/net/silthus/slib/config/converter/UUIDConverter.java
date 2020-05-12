package net.silthus.slib.config.converter;

import de.exlll.configlib.Converter;
import org.bukkit.Material;

import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

public final class UUIDConverter
        implements Converter<UUID, String> {

    @Override
    public String convertTo(UUID uuid, ConversionInfo conversionInfo) {
        return uuid.toString();
    }

    @Override
    public UUID convertFrom(String s, ConversionInfo conversionInfo) {
        return UUID.fromString(s);
    }
}
