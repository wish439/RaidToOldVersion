package org.wishtoday.rto.raidToOldVersion.Event.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.wishtoday.rto.raidToOldVersion.Util.FoliaUtils;

public class PlaceBlockPacket extends PacketAdapter {

    public PlaceBlockPacket(Plugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ITEM_ON);
    }

    @Override
    public void onPacketSending(PacketEvent event) {

    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();

        // blockHitResult
        InternalStructure read = packet.getStructures().read(0);
        Vector hitVec = read.getVectors().read(0);//点击位置
        BlockPosition blockPos = read.getBlockPositionModifier().read(0);
        double relativeHitX = hitVec.getX() - blockPos.getX();
        if (relativeHitX < 2) return;
        String handString = packet.getStructures().read(1).getHandle().toString();
        Player player = event.getPlayer();
        ItemStack item;
        if ("MAIN_HAND".equals(handString)) {
            item = player.getInventory().getItemInMainHand();
        } else {
            item = player.getInventory().getItemInOffHand();
        }
        if (!item.getType().isBlock()) return;
        Material material = item.getType();
        //material.isSolid()
        BlockData data = Bukkit.createBlockData(material);
        World world = player.getWorld();
        int protocolValue = ((int) relativeHitX - 2) / 2;
        if (data instanceof Directional directional) {
            int facingIndex = protocolValue & 0xF;
            BlockFace facing = directional.getFacing();
            if (facingIndex == 6) {
                facing = directional.getFacing().getOppositeFace();
            }
            if (facingIndex >= 0 && facingIndex < 6) {
                facing = getTrueFace(facingIndex);
            }
            directional.setFacing(facing);
        }
        if (data instanceof Orientable orientable) {
            Axis value = Axis.values()[protocolValue % 3];
            orientable.setAxis(value);
        }
        protocolValue &= 0xFFFFFFF0;
        if (protocolValue >= 16) {
            if (data instanceof Repeater repeater) {
                int delay = protocolValue / 16;
                if (repeater.getMaximumDelay() >= delay
                && repeater.getMinimumDelay() <= delay) {
                    repeater.setDelay(delay);
                }
            }
            if (protocolValue == 16) {
                if (data instanceof Comparator comparator) {
                    comparator.setMode(Comparator.Mode.SUBTRACT);
                }
                if (data instanceof Bisected bisected) {
                    if (bisected.getHalf() == Bisected.Half.BOTTOM) {
                        bisected.setHalf(Bisected.Half.TOP);
                    }
                }
                if (data instanceof Slab slab) {
                    if (slab.getType() == Slab.Type.BOTTOM) {
                        slab.setType(Slab.Type.TOP);
                    }
                }
            }
        }
        Block block = blockPos.toLocation(world).getBlock();
        if (data instanceof Door door) {
            FoliaUtils.tryRunTask(
                    plugin, () -> {
                        if (block.canPlace(data)) {
                            world.setBlockData(blockPos.toLocation(world), door);
                            door.setHalf(Bisected.Half.TOP);
                            world.setBlockData(blockPos.toLocation(world).add(0,1,0), door);
                        }
                    }
            );
            return;
        }
        if (data instanceof Bed bed) {
            FoliaUtils.tryRunTask(
                    plugin, () -> {
                        if (block.canPlace(data)) {
                            world.setBlockData(blockPos.toLocation(world), bed);
                            Location location = getBedLocation(blockPos.toLocation(world), bed);
                            bed.setPart(Bed.Part.HEAD);
                            world.setBlockData(location, bed);
                        }
                    }
            );
            return;
        }
        FoliaUtils.tryRunTask(plugin, () -> {
            if (block.canPlace(data)) {
                world.setBlockData(blockPos.toLocation(world), data);
            }
        });
    }

    private BlockFace getTrueFace(int i) {
        switch (i) {
            case 0 -> {
                return BlockFace.DOWN;
            }
            case 1 -> {
                return BlockFace.UP;
            }
            case 2 -> {
                return BlockFace.NORTH;
            }
            case 3 -> {
                return BlockFace.SOUTH;
            }
            case 4 -> {
                return BlockFace.WEST;
            }
            case 5 -> {
                return BlockFace.EAST;
            }
        }
        return BlockFace.DOWN;
    }
    private Location getBedLocation(Location location, Bed bed) {
        BlockFace facing = bed.getFacing();
        return switch (facing) {
            case WEST -> location.subtract(1,0,0);
            case EAST -> location.add(1,0,0);
            case NORTH -> location.subtract(0,0,1);
            case SOUTH -> location.add(0,0,1);
            default -> location.add(0,0,0);
        };
    }

}
