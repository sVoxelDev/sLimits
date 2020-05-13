package net.silthus.slib.config.converter;

import de.exlll.configlib.Converter;

import java.util.UUID;

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
