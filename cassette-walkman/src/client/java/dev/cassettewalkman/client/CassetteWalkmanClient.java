package dev.cassettewalkman.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.cassettewalkman.CassetteWalkman;
import dev.cassettewalkman.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class CassetteWalkmanClient implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(CassetteWalkman.MOD_ID, "controls")
    );
    private static final KeyMapping PLAY_PAUSE = key("play_pause", InputConstants.KEY_P);
    private static final KeyMapping NEXT_TRACK = key("next_track", InputConstants.KEY_N);
    private static final KeyMapping STOP = key("stop", InputConstants.KEY_O);
    private static final KeyMapping NEXT_PACK = key("next_pack", InputConstants.KEY_M);
    private static final KeyMapping RELOAD = key("reload", InputConstants.KEY_R);

    private WalkmanPlayer walkmanPlayer;
    private final MusicAura musicAura = new MusicAura();

    private static KeyMapping key(String name, int code) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.cassettewalkman." + name,
                InputConstants.Type.KEYSYM,
                code,
                CATEGORY
        ));
    }

    @Override
    public void onInitializeClient() {
        Minecraft minecraft = Minecraft.getInstance();
        walkmanPlayer = new WalkmanPlayer(minecraft);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (PLAY_PAUSE.consumeClick()) if (readyToPlay(client)) walkmanPlayer.togglePlayPause();
            while (NEXT_TRACK.consumeClick()) if (readyToPlay(client)) walkmanPlayer.nextTrack();
            while (STOP.consumeClick()) walkmanPlayer.stop();
            while (NEXT_PACK.consumeClick()) walkmanPlayer.cyclePack();
            while (RELOAD.consumeClick()) walkmanPlayer.reload();
            if (walkmanPlayer.isAudiblyPlaying() && (!hasWalkmanInOffHand(client) || !hasCassette(client))) walkmanPlayer.stop();
            musicAura.tick(client, walkmanPlayer);
        });
    }

    private boolean readyToPlay(Minecraft client) {
        if (client.player == null) return false;
        if (!hasWalkmanInOffHand(client)) {
            client.player.displayClientMessage(Component.literal("Equip the Cassette Walkman in your off-hand."), true);
            return false;
        }
        if (!hasCassette(client)) {
            client.player.displayClientMessage(Component.literal("You need a Cave Violence Vol. 1 cassette."), true);
            return false;
        }
        return true;
    }

    private boolean hasWalkmanInOffHand(Minecraft client) {
        return client.player != null && client.player.getOffhandItem().is(ModItems.WALKMAN);
    }

    private boolean hasCassette(Minecraft client) {
        if (client.player == null) return false;
        for (int slot = 0; slot < client.player.getInventory().getContainerSize(); slot++) {
            if (client.player.getInventory().getItem(slot).is(ModItems.CAVE_VIOLENCE_CASSETTE)) return true;
        }
        return false;
    }
}
