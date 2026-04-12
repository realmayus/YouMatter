package org.realverse.youmatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class YMConfig {
    private static final Path PATH = Path.of(String.valueOf(FMLPaths.CONFIGDIR.get()), "youmatter-config.json");
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static Data config;

    public static class Data {
        public boolean filterMode = true;
        public List<String> filterItems = List.of("youmatter:black_hole", "youmatter:umatter_bucket", "youmatter:stabilizer_bucket");
        public List<String> overrides = List.of("minecraft:diamond=2500", "minecraft:nether_star=5000");
        public int thumbDriveSlots = 8;
        public int defaultAmount = 1000;
        public int energyReplicator = 2048;
        public int energyEncoder = 512;
        public int energyScanner = 512;
        public int productionPerTick = 1;

        public Object[] getOverride(String registryName) {
            for(String s : overrides) {
                String foundName = s.substring(0, s.indexOf('='));
                String foundValue = s.substring(s.indexOf('=')).substring(1);
                if (foundName.equalsIgnoreCase(registryName)) {
                    return new Object[]{foundName, foundValue};
                }
            }
            return null;
        }
    }

    public static void loadConfig() {
        try {
            if (!Files.exists(PATH)) {
                Files.createDirectories(PATH.getParent());
                config = new Data();
                saveConfig();
            } else {
                try (Reader reader = Files.newBufferedReader(PATH)) {
                    config = GSON.fromJson(reader, Data.class);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load YouMatter config", e);
        }
    }

    public static void saveConfig() {
        try (Writer writer = Files.newBufferedWriter(PATH)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save YouMatter config", e);
        }
    }

    public static Data get() {
        if (config == null) loadConfig();
        return config;
    }
}
