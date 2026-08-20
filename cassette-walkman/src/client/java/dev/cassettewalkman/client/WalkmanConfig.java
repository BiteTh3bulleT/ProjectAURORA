package dev.cassettewalkman.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.cassettewalkman.CassetteWalkman;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class WalkmanConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public boolean auraEnabled = true;
    public double auraRadius = 1.35;
    public int auraNotesPerBurst = 3;
    public int auraEveryTicks = 3;
    public double midiVolume = 0.82;
    public boolean shuffle = true;

    public static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(CassetteWalkman.MOD_ID).resolve("config.json");
    }

    public static WalkmanConfig load() {
        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
            if (Files.notExists(path)) {
                WalkmanConfig defaults = new WalkmanConfig();
                defaults.save();
                return defaults;
            }
            try (Reader reader = Files.newBufferedReader(path)) {
                WalkmanConfig loaded = GSON.fromJson(reader, WalkmanConfig.class);
                return loaded == null ? new WalkmanConfig() : loaded;
            }
        } catch (Exception e) {
            CassetteWalkman.LOGGER.error("Could not load Walkman config; using defaults.", e);
            return new WalkmanConfig();
        }
    }

    public void save() throws Exception {
        Path path = configPath();
        Files.createDirectories(path.getParent());
        try (Writer writer = Files.newBufferedWriter(path)) { GSON.toJson(this, writer); }
    }
}
