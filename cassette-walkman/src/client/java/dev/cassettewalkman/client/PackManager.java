package dev.cassettewalkman.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.cassettewalkman.CassetteWalkman;
import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class PackManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String BUILTIN_ID = "cave_violence_vol_1";
    private static final String ROOT = "/assets/cassettewalkman/music/" + BUILTIN_ID + "/";
    private static final String[] BUILTIN_FILES = {
        "manifest.json",
        "tracks/01_pantera_cowboys_from_hell.mid",
        "tracks/02_pantera_walk.mid",
        "tracks/03_metallica_master_of_puppets.mid",
        "tracks/04_metallica_dont_tread_on_me.mid",
        "tracks/05_led_zeppelin_immigrant_song.mid"
    };

    private final Path packsDirectory = FabricLoader.getInstance().getConfigDir().resolve(CassetteWalkman.MOD_ID).resolve("packs");
    private List<CassettePack> packs = List.of();

    public void initialize() {
        try {
            Files.createDirectories(packsDirectory);
            bootstrapBuiltinPack();
            reload();
        } catch (Exception e) {
            CassetteWalkman.LOGGER.error("Could not initialize cassette packs.", e);
        }
    }

    public void reload() {
        List<CassettePack> loaded = new ArrayList<>();
        try {
            Files.createDirectories(packsDirectory);
            try (Stream<Path> dirs = Files.list(packsDirectory)) {
                dirs.filter(Files::isDirectory).sorted().forEach(dir -> {
                    Path manifestPath = dir.resolve("manifest.json");
                    if (Files.notExists(manifestPath)) return;
                    try (Reader reader = Files.newBufferedReader(manifestPath)) {
                        PackManifest manifest = GSON.fromJson(reader, PackManifest.class);
                        if (manifest == null || manifest.tracks == null) return;
                        List<CassetteTrack> tracks = new ArrayList<>();
                        for (TrackManifest t : manifest.tracks) {
                            if (!t.enabled) continue;
                            Path midi = dir.resolve(t.file).normalize();
                            if (!midi.startsWith(dir.normalize()) || Files.notExists(midi)) continue;
                            tracks.add(new CassetteTrack(t.artist, t.title, midi));
                        }
                        if (!tracks.isEmpty()) {
                            boolean shuffle = manifest.playback != null && "shuffle".equalsIgnoreCase(manifest.playback.mode);
                            loaded.add(new CassettePack(
                                    manifest.pack_id == null ? dir.getFileName().toString() : manifest.pack_id,
                                    manifest.name == null ? dir.getFileName().toString() : manifest.name,
                                    shuffle,
                                    List.copyOf(tracks)
                            ));
                        }
                    } catch (Exception e) {
                        CassetteWalkman.LOGGER.error("Could not load cassette pack {}", dir, e);
                    }
                });
            }
        } catch (Exception e) {
            CassetteWalkman.LOGGER.error("Could not scan cassette packs.", e);
        }
        loaded.sort(Comparator.comparing(CassettePack::name, String.CASE_INSENSITIVE_ORDER));
        packs = List.copyOf(loaded);
    }

    private void bootstrapBuiltinPack() throws Exception {
        Path packDir = packsDirectory.resolve(BUILTIN_ID);
        if (Files.exists(packDir.resolve("manifest.json"))) return;
        for (String relative : BUILTIN_FILES) {
            try (InputStream in = PackManager.class.getResourceAsStream(ROOT + relative)) {
                if (in == null) throw new IllegalStateException("Missing bundled resource " + ROOT + relative);
                Path target = packDir.resolve(relative);
                Files.createDirectories(target.getParent());
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    public List<CassettePack> packs() { return packs; }
    public Path packsDirectory() { return packsDirectory; }
    public record CassettePack(String id, String name, boolean shuffle, List<CassetteTrack> tracks) {}
    public record CassetteTrack(String artist, String title, Path file) {}
    private static final class PackManifest { String pack_id; String name; PlaybackManifest playback; List<TrackManifest> tracks; }
    private static final class PlaybackManifest { String mode; }
    private static final class TrackManifest { String artist; String title; String file; boolean enabled = true; }
}
