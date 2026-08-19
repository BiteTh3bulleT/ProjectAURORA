package dev.cassettewalkman.client;

import dev.cassettewalkman.CassetteWalkman;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Random;

public final class WalkmanPlayer {
    private final Minecraft minecraft;
    private final PackManager packManager = new PackManager();
    private final MidiEngine midiEngine = new MidiEngine();
    private final Random random = new Random();
    private WalkmanConfig config;
    private int packIndex;
    private int trackIndex = -1;

    public WalkmanPlayer(Minecraft minecraft) {
        this.minecraft = minecraft;
        config = WalkmanConfig.load();
        packManager.initialize();
    }

    public void togglePlayPause() {
        if (midiEngine.isPlaying()) { midiEngine.pause(); actionBar("Cassette paused"); return; }
        if (midiEngine.isPaused()) { midiEngine.resume(); actionBar("Cassette resumed"); return; }
        startTrack(pickFirstTrack());
    }

    public void nextTrack() {
        List<PackManager.CassetteTrack> tracks = currentTracks();
        if (tracks.isEmpty()) { actionBar("No MIDI tracks found"); return; }
        int next;
        if (config.shuffle || currentPack().shuffle()) {
            if (tracks.size() == 1) next = 0;
            else do { next = random.nextInt(tracks.size()); } while (next == trackIndex);
        } else next = (trackIndex + 1 + tracks.size()) % tracks.size();
        startTrack(next);
    }

    public void stop() {
        if (midiEngine.isPlaying() || midiEngine.isPaused()) { midiEngine.stop(); actionBar("Cassette stopped"); }
    }

    public void cyclePack() {
        List<PackManager.CassettePack> packs = packManager.packs();
        if (packs.isEmpty()) { actionBar("No cassette packs found"); return; }
        midiEngine.stop();
        packIndex = (packIndex + 1) % packs.size();
        trackIndex = -1;
        actionBar("Cassette: " + currentPack().name());
    }

    public void reload() {
        midiEngine.stop();
        config = WalkmanConfig.load();
        packManager.reload();
        if (packManager.packs().isEmpty()) { packIndex = 0; trackIndex = -1; actionBar("No cassette packs found"); return; }
        packIndex = Math.floorMod(packIndex, packManager.packs().size());
        trackIndex = -1;
        actionBar("Reloaded " + packManager.packs().size() + " cassette pack(s)");
    }

    public boolean isAudiblyPlaying() { return midiEngine.isPlaying(); }
    public WalkmanConfig config() { return config; }

    private int pickFirstTrack() {
        List<PackManager.CassetteTrack> tracks = currentTracks();
        if (tracks.isEmpty()) return -1;
        return (config.shuffle || currentPack().shuffle()) ? random.nextInt(tracks.size()) : 0;
    }

    private void startTrack(int index) {
        List<PackManager.CassetteTrack> tracks = currentTracks();
        if (index < 0 || index >= tracks.size()) { actionBar("No playable MIDI tracks found"); return; }
        PackManager.CassetteTrack track = tracks.get(index);
        trackIndex = index;
        try {
            midiEngine.play(track.file(), config.midiVolume, () -> minecraft.execute(this::nextTrack));
            actionBar("♪ " + track.artist() + " — " + track.title());
        } catch (Exception e) {
            CassetteWalkman.LOGGER.error("Could not play MIDI {}", track.file(), e);
            actionBar("MIDI playback failed — check latest.log");
            midiEngine.stop();
        }
    }

    private PackManager.CassettePack currentPack() {
        List<PackManager.CassettePack> packs = packManager.packs();
        if (packs.isEmpty()) return new PackManager.CassettePack("none", "No Cassette", false, List.of());
        packIndex = Math.floorMod(packIndex, packs.size());
        return packs.get(packIndex);
    }

    private List<PackManager.CassetteTrack> currentTracks() { return currentPack().tracks(); }
    private void actionBar(String message) { if (minecraft.player != null) minecraft.player.displayClientMessage(Component.literal(message), true); }
}
