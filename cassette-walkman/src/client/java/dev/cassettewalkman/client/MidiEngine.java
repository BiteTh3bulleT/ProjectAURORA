package dev.cassettewalkman.client;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MidiEngine implements AutoCloseable {
    private Sequencer sequencer;
    private Synthesizer synthesizer;
    private Transmitter transmitter;
    private Receiver receiver;
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private volatile Runnable finishedCallback = () -> {};
    private volatile boolean closing;

    public synchronized void play(Path midiFile, double volume, Runnable onFinished) throws Exception {
        stopAndCloseDevices();
        Sequence sequence = MidiSystem.getSequence(midiFile.toFile());
        finishedCallback = onFinished == null ? () -> {} : onFinished;
        closing = false;
        try {
            sequencer = MidiSystem.getSequencer(false);
            if (sequencer == null) throw new MidiUnavailableException("No MIDI sequencer is available.");
            sequencer.open();
            synthesizer = MidiSystem.getSynthesizer();
            if (synthesizer == null) throw new MidiUnavailableException("No MIDI synthesizer is available.");
            synthesizer.open();
            transmitter = sequencer.getTransmitter();
            receiver = synthesizer.getReceiver();
            transmitter.setReceiver(receiver);
            setVolume(volume);
        } catch (MidiUnavailableException e) {
            stopAndCloseDevices();
            sequencer = MidiSystem.getSequencer();
            if (sequencer == null) throw e;
            sequencer.open();
        }
        sequencer.setSequence(sequence);
        sequencer.addMetaEventListener(this::handleMetaEvent);
        sequencer.setTickPosition(0);
        paused.set(false);
        sequencer.start();
    }

    public synchronized void pause() { if (sequencer != null && sequencer.isOpen() && sequencer.isRunning()) { sequencer.stop(); paused.set(true); } }
    public synchronized void resume() { if (sequencer != null && sequencer.isOpen() && paused.get()) { paused.set(false); sequencer.start(); } }
    public synchronized void stop() { closing = true; paused.set(false); stopAndCloseDevices(); closing = false; }
    public synchronized boolean isPlaying() { return sequencer != null && sequencer.isOpen() && sequencer.isRunning(); }
    public boolean isPaused() { return paused.get(); }

    private void handleMetaEvent(MetaMessage message) {
        if (message.getType() != 47 || closing) return;
        Sequencer current = sequencer;
        if (current == null || !current.isOpen()) return;
        if (current.getTickPosition() + 2 < current.getTickLength()) return;
        paused.set(false);
        finishedCallback.run();
    }

    private void setVolume(double requested) {
        if (synthesizer == null || !synthesizer.isOpen()) return;
        int midiVolume = (int)Math.round(Math.max(0.0, Math.min(1.0, requested)) * 127.0);
        MidiChannel[] channels = synthesizer.getChannels();
        if (channels == null) return;
        for (MidiChannel channel : channels) if (channel != null) channel.controlChange(7, midiVolume);
    }

    private void stopAndCloseDevices() {
        try { if (sequencer != null && sequencer.isRunning()) sequencer.stop(); } catch (Exception ignored) {}
        try { if (transmitter != null) transmitter.close(); } catch (Exception ignored) {}
        try { if (receiver != null) receiver.close(); } catch (Exception ignored) {}
        try { if (sequencer != null) sequencer.close(); } catch (Exception ignored) {}
        try { if (synthesizer != null) synthesizer.close(); } catch (Exception ignored) {}
        transmitter = null; receiver = null; sequencer = null; synthesizer = null;
    }

    @Override public void close() { stop(); }
}
