package org.wishtoday.rto.raidToOldVersion.Event.impl;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.wishtoday.rto.raidToOldVersion.Util.FoliaUtils;

public class EasyPlace implements PacketListener {
    private final Plugin plugin;

    public EasyPlace(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        Player player = event.getPlayer();
        if (event.getPacketType() != PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            return;
        }

        WrapperPlayClientPlayerBlockPlacement place = new WrapperPlayClientPlayerBlockPlacement(event);
        Vector3f cursor = place.getCursorPosition();
        Vector3i blockPos = place.getBlockPosition();
        InteractionHand hand = place.getHand();

        double relativeHitX = cursor.getX();
        if (relativeHitX < 2) return;

        ItemStack stack = (hand == InteractionHand.OFF_HAND)
                ? player.getInventory().getItemInOffHand()
                : player.getInventory().getItemInMainHand();

        if (!stack.getType().isBlock()) return;
        event.setCancelled(true);

        BlockData data = Bukkit.createBlockData(stack.getType());

        int protocolValue = ((int) relativeHitX - 2) / 2;

        if (data instanceof Directional directional) {
            int facingIndex = protocolValue & 0xF;
            BlockFace facing = getTrueFace(facingIndex);
            directional.setFacing(facing);
        }

        if (data instanceof Orientable orientable) {
            Axis axis = Axis.values()[protocolValue % 3];
            orientable.setAxis(axis);
        }

        protocolValue &= 0xFFFFFFF0;
        if (protocolValue >= 16) {
            if (data instanceof Repeater repeater) {
                int delay = protocolValue / 16;
                repeater.setDelay(Math.max(repeater.getMinimumDelay(), Math.min(repeater.getMaximumDelay(), delay)));
            }
            if (protocolValue == 16) {
                if (data instanceof Comparator comparator) {
                    comparator.setMode(Comparator.Mode.SUBTRACT);
                }
                if (data instanceof Bisected bisected) {
                    bisected.setHalf(Bisected.Half.TOP);
                }
                if (data instanceof Slab slab) {
                    slab.setType(Slab.Type.TOP);
                }
            }
        }

        World world = player.getWorld();
        Location loc = new Location(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        Block block = loc.getBlock();
        if (data instanceof Door door) {
            FoliaUtils.tryRunTask(
                    plugin, () -> {
                        if (block.canPlace(data)) {
                            world.setBlockData(loc, door);
                            door.setHalf(Bisected.Half.TOP);
                            world.setBlockData(loc.add(0,1,0), door);
                        }
                    }
            );
            return;
        }
        if (data instanceof Bed bed) {
            FoliaUtils.tryRunTask(
                    plugin, () -> {
                        if (block.canPlace(data)) {
                            world.setBlockData(loc, bed);
                            Location location = getBedLocation(loc, bed);
                            bed.setPart(Bed.Part.HEAD);
                            world.setBlockData(location, bed);
                        }
                    }
            );
            return;
        }
        FoliaUtils.tryRunTask(plugin, () -> {
            if (block.canPlace(data)) {
                world.setBlockData(loc, data);
            }
        });
    }


    private BlockFace getTrueFace(int i) {
        return switch (i) {
            case 0 -> BlockFace.DOWN;
            case 1 -> BlockFace.UP;
            case 2 -> BlockFace.NORTH;
            case 3 -> BlockFace.SOUTH;
            case 4 -> BlockFace.WEST;
            case 5 -> BlockFace.EAST;
            default -> BlockFace.NORTH;
        };
    }

    private Location getBedLocation(Location location, Bed bed) {
        BlockFace facing = bed.getFacing();
        return location.add(facing.getModX(), facing.getModY(), facing.getModZ());
        /*return switch (facing) {
            case WEST -> location.subtract(1, 0, 0);
            case EAST -> location.add(1, 0, 0);
            case NORTH -> location.subtract(0, 0, 1);
            case SOUTH -> location.add(0, 0, 1);
            default -> location;
        };*/
    }
}
