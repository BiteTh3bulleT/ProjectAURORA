package dev.cassettewalkman.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;

public final class MusicAura {
    private int ticks;

    public void tick(Minecraft minecraft, WalkmanPlayer player) {
        ticks++;
        WalkmanConfig config = player.config();
        if (!config.auraEnabled || !player.isAudiblyPlaying() || minecraft.player == null || minecraft.level == null) return;
        int every = Math.max(1, config.auraEveryTicks);
        if (ticks % every != 0) return;
        int count = Math.max(1, Math.min(12, config.auraNotesPerBurst));
        double radius = Math.max(0.35, Math.min(3.5, config.auraRadius));
        double baseAngle = ticks * 0.17;
        for (int i = 0; i < count; i++) {
            double angle = baseAngle + Math.PI * 2.0 * i / count;
            double wave = Math.sin(ticks * 0.11 + i) * 0.12;
            double x = minecraft.player.getX() + Math.cos(angle) * (radius + wave);
            double z = minecraft.player.getZ() + Math.sin(angle) * (radius + wave);
            double phase = (ticks * 0.035 + (double)i / count) % 1.0;
            double y = minecraft.player.getY() + 0.25 + phase * 1.9;
            double hue = (ticks * 0.015 + (double)i / count) % 1.0;
            minecraft.level.addParticle(ParticleTypes.NOTE, x, y, z, hue, 0.0, 0.0);
        }
    }
}
