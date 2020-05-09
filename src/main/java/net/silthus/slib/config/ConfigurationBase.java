package net.silthus.slib.config;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import net.silthus.slib.bukkit.BasePlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import static net.silthus.slib.config.ConfigUtil.prepareSerialization;
import static net.silthus.slib.config.ConfigUtil.smartCast;

/**
 * @author Silthus
 */
public abstract class ConfigurationBase<T extends BasePlugin> extends YamlConfiguration
        implements Config {

    /**
     * Refrence to the plugin instance.
     */
    private final T plugin;
    /**
     * The Name of the config file
     */
    private final String name;
    /**
     * The actual physical file object.
     */
    private final File file;

    private DataMap override = null;
    @Getter
    @Setter
    private boolean saveDefaults = false;

    private final HashMap<String, String[]> annos = new HashMap<>();

    public ConfigurationBase(T plugin, File file) {

        this.plugin = plugin;
        this.name = file.getName();
        this.file = file;
    }

    public ConfigurationBase(T plugin, String name) {

        this(plugin, new File(plugin.getDataFolder(), name));
    }

    public void merge(ConfigurationBase config, String path) {

        if (config == null) return;
        getOverrideConfig().merge(config.getOverrideSection(path));
    }

    public void merge(ConfigurationBase config) {

        getOverrideConfig().merge(config);
    }

    public void merge(ConfigurationSection config) {

        getOverrideConfig().merge(config);
    }

    public <V> V getOverride(String key, V def) {

        if (!isSet(key)) {
            set(key, def);
        }
        if (def instanceof Integer) return (V) (Integer) getOverrideInt(key, (Integer) def);
        if (def instanceof Double) return (V) (Double) getOverrideDouble(key, (Double) def);
        return (V) getOverrideConfig().get(key, def);
    }

    public int getOverrideInt(String key, int def) {

        return getOverrideConfig().getInt(key, def);
    }

    public double getOverrideDouble(String key, double def) {

        return getOverrideConfig().getDouble(key, def);
    }

    public String getOverrideString(String key, String def) {

        return getOverrideConfig().getString(key, def);
    }

    public boolean getOverrideBool(String key, boolean def) {

        return getOverrideConfig().getBoolean(key, def);
    }

    public ConfigurationSection getOverrideSection(String path) {

        return getOverrideConfig().getSafeConfigSection(path);
    }

    public DataMap getOverrideConfig() {

        if (override == null) {
            setOverrideConfig(createDataMap());
        }
        return this.override;
    }

    public void setOverrideConfig(DataMap override) {

        this.override = override;
    }

    public ConfigurationSection getSafeConfigSection(String path) {

        ConfigurationSection configurationSection = getConfigurationSection(path);
        if (configurationSection == null) {
            configurationSection = createSection(path);
        }
        return configurationSection;
    }

    public DataMap createDataMap() {

        return new YamlDataMap(this, this);
    }

    public DataMap createDataMap(String path) {

        return new YamlDataMap(getSafeConfigSection(path), this);
    }

    public File getFile() {

        return file;
    }

    public T getPlugin() {

        return plugin;
    }

    public void reload() {

        load();
    }

    public void save() {

        save(file);
    }

    public void load() {

        load(file);
        loadAnnotations();
    }

    @Override
    public final void load(File file) {

        try {
            if (!file.exists() || file.length() == 0) {
                copyFile();
                save();
            }
            // load the config by calling the bukkit super method
            super.load(file);
            //            plugin.getLogger().info("[" + plugin.getName() + "] loaded config file \"" +
            // displayName + "\" successfully.");
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAnnotations() {

        if (loadAnnotations(this)) save(file);
    }

    private boolean loadAnnotations(Object object) {

        boolean foundAnnotations = false;
        for (Field field : getFieldsRecur(object.getClass())) {
            field.setAccessible(true);

            try {
                if (field.isAnnotationPresent(ConfigSubClass.class) && field.get(object) != null) {
                    foundAnnotations = loadAnnotations(field.get(object));
                } else if (field.isAnnotationPresent(Setting.class)) {

                    String key = field.getAnnotation(Setting.class).value();
                    if (Strings.isNullOrEmpty(key)) {
                        key = field.getName();
                    }
                    final Object value = smartCast(field.getGenericType(), get(key));

                    if (value != null) {
                        field.set(object, value);
                    } else {
                        set(key, prepareSerialization(field.get(object)));
                    }
                    foundAnnotations = true;
                }
            } catch (IllegalAccessException e) {
                plugin.getLogger().log(Level.SEVERE, "Error setting configuration value of field: ", e);
                e.printStackTrace();
            }
        }
        return foundAnnotations;
    }

    @Override
    public final void save(File file) {

        try {
            annos.clear();
            saveAnnotations();
            super.save(file);
            commentPostProcess(file);
        } catch (IOException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveAnnotations() {

        saveAnnotations(this);
    }

    private void saveAnnotations(Object object) {

        for (Field field : getFieldsRecur(object.getClass())) {
            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(ConfigSubClass.class) && field.get(object) != null) {
                    saveAnnotations(field.get(object));
                } else if (field.isAnnotationPresent(Setting.class)) {
                    String key = field.getAnnotation(Setting.class).value();
                    if (Strings.isNullOrEmpty(key)) {
                        key = field.getName();
                    }
                    onComment(key, field);
                    set(key, prepareSerialization(field.get(object)));
                }
            } catch (IllegalAccessException e) {
                plugin.getLogger().log(Level.SEVERE, "Error setting configuration value of field: ", e);
                e.printStackTrace();
            }
        }
    }

    private void onComment(String path, Field field) {

        if (field.getAnnotation(Comment.class) != null) {
            annos.put(path, new String[]{field.getAnnotation(Comment.class).value()});
            return;
        }
        if (field.getAnnotation(MultiComment.class) != null) {
            annos.put(path, field.getAnnotation(MultiComment.class).value());
        }
    }

    private int level(String path) {

        int count = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) != ' ') {
                break;
            }
            count++;
        }
        return count / 2;
    }

    private void commentPostProcess(File file) throws IOException {

        ArrayList<String> buffer = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.add(line);
        }
        reader.close();
        ArrayList<String> key = new ArrayList<String>();
        int level;
        String newKey;
        String tmpKey;
        int diff;
        String[] comment;
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (int i = 0; i < buffer.size(); i++) {
            if (buffer.get(i).charAt(0) == '#') {
                continue;
            }
            newKey = buffer.get(i).split(":")[0];
            level = level(newKey);
            newKey = newKey.trim();
            if (level == 0) {
                key.clear();
            } else {
                diff = key.size() - level;
                for (int j = 0; j < diff; j++) {
                    key.remove(key.size() - 1);
                }
                tmpKey = StringUtils.join(key, ".");
                tmpKey += "." + newKey;

                if (annos.containsKey(tmpKey)) {
                    comment = annos.get(tmpKey);
                    for (int j = 0; j < comment.length; j++) {
                        writer.write("# " + comment[j]);
                        writer.newLine();
                    }
                }
            }
            key.add(newKey);
            writer.write(buffer.get(i));
            writer.newLine();
        }
        writer.close();
    }

    private List<Field> getFieldsRecur(Class<?> clazz) {

        return getFieldsRecur(clazz, false);
    }

    private List<Field> getFieldsRecur(Class<?> clazz, boolean includeObject) {

        List<Field> fields = new ArrayList<>();
        while (clazz != null && (includeObject || !Object.class.equals(clazz))) {

            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private void copyFile() {

        try {
            // read the template file from the resources folder
            InputStream stream = plugin.getResource("defaults/" + name);
            if (stream == null) {
                plugin.getLogger().warning("There is no default config for " + name);
                file.createNewFile();
                return;
            }
            Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            plugin.getLogger().log(Level.INFO, "Copied default config: " + name);
        } catch (IOException iex) {
            plugin.getLogger().log(Level.WARNING, "could not create default config: " + name, iex);
        }
    }

    @Override
    public int getInt(String path, int def) {

        if (!isSet(path)) {
            set(path, def);
            if (isSaveDefaults()) save();
        } else {
            return super.getInt(path, def);
        }
        return getInt(path);
    }

    @Override
    public String getString(String path, String def) {

        if (def == null) return super.getString(path, null);
        if (!isSet(path)) {
            set(path, def);
            if (isSaveDefaults()) save();
        } else {
            return super.getString(path, def);
        }
        return getString(path);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {

        if (!isSet(path)) {
            set(path, def);
            if (isSaveDefaults()) save();
        } else {
            return super.getBoolean(path, def);
        }
        return getBoolean(path);
    }

    @Override
    public double getDouble(String path, double def) {

        if (!isSet(path)) {
            set(path, def);
            if (isSaveDefaults()) save();
        } else {
            return super.getDouble(path, def);
        }
        return getDouble(path);
    }

    @Override
    public long getLong(String path, long def) {

        if (!isSet(path)) {
            set(path, def);
            if (isSaveDefaults()) save();
        } else {
            return super.getLong(path, def);
        }
        return getLong(path);
    }
}
