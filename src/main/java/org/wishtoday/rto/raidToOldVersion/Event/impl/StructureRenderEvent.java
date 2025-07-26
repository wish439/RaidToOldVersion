package org.wishtoday.rto.raidToOldVersion.Event.impl;

import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StructureRenderEvent extends BukkitRunnable implements Listener {
    public static final int CHUNK_SIZE = 16;

    public void onStructureRender() {
        @NotNull OfflinePlayer[] players = Bukkit.getOfflinePlayers();
        for (OfflinePlayer player1 : players) {
            Player player = player1.getPlayer();
            if (player == null) continue;
            List<Chunk> chunks = getViewDistanceChunks(player);
            for (Chunk chunk : chunks) {
                Collection<GeneratedStructure> structures = chunk.getStructures();
                for (GeneratedStructure structure : structures) {
                    spawnParticlesToStructure(structure,player);
                }
            }
        }
    }
    private static List<Chunk> getViewDistanceChunks(Player player) {
        List<Chunk> viewDistanceChunks = new ArrayList<>();
        int viewDistance = Bukkit.getViewDistance() + 2;
        //viewDistance *= CHUNK_SIZE;
        int playerChunkX = player.getChunk().getX();
        int playerChunkZ = player.getChunk().getZ();
        World world = player.getWorld();
        for (int x = playerChunkX - viewDistance; x <= playerChunkX + viewDistance; x++) {
            for (int z = playerChunkZ - viewDistance; z <= playerChunkZ + viewDistance; z++) {
                Chunk chunk = world.getChunkAt(x, z);
                viewDistanceChunks.add(chunk);
            }
        }
        return viewDistanceChunks;
    }
    private static void spawnParticlesToStructure(GeneratedStructure structure,
                                                  Player player) {
        BoundingBox box = structure.getBoundingBox();
        Vector min = box.getMin();
        Vector max = box.getMax();
        BlockPosition blockMin = new BlockPosition(min);
        BlockPosition blockMax = new BlockPosition(max);
        for (int i = blockMin.getX(); i <= blockMax.getX(); i++) {
            for (int j = blockMin.getY(); j <= blockMax.getY(); j++) {
            }
        }
    }

    @Override
    public void run() {
        onStructureRender();
    }
}
