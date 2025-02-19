package dev.name.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.name.config.settings.Setting;
import dev.name.config.settings.impl.StringArraySetting;
import dev.name.transformer.Transformer;
import dev.name.transformer.Transformers;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "unchecked"})
public class Config {
    private static final File CONFIG = new File("config.json");

    public static String create_new() {
        final Map<String, Object> data = new HashMap<>();
        for (final Transformer transformer : Transformers.getTransformers()) data.put(transformer.name().toLowerCase(), transformer.settings.stream().collect(Collectors.toMap(setting -> setting.getName().toLowerCase(), Setting::getValue)));
        return new GsonBuilder().setPrettyPrinting().create().toJson(data);
    }

    public static void read(final String json) {
        final Map<String, Map<String, Object>> data = new Gson().fromJson(json, Map.class);

        for (final Transformer transformer : Transformers.getTransformers()) {
            final Map<String, Object> settings = data.get(transformer.name().toLowerCase());
            if (settings == null) continue;

            for (final Setting setting : transformer.settings) {
                Object value = settings.get(setting.getName().toLowerCase());
                if (value == null) continue;
                if (setting instanceof StringArraySetting arr_setting) arr_setting.setValue(((ArrayList<String>) value).toArray(new String[0]));
                else setting.setValue(value);
            }
        }
    }

    @SneakyThrows
    public static void write(final File file, final String data) {
        final FileWriter writer = new FileWriter(file);
        writer.write(data);
        writer.close();
    }

    @SneakyThrows
    public static String read() {
        if (!exists()) return null;
        return new String(Files.readAllBytes(CONFIG.toPath()));
    }

    public static boolean exists() {
        return CONFIG.exists();
    }

    @SneakyThrows
    public static File create() {
        if (!exists()) if (!CONFIG.createNewFile()) System.err.println("failed to create config ??");
        return CONFIG;
    }
}