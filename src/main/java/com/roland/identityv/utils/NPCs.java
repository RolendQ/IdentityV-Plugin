package com.roland.identityv.utils;

import com.mojang.authlib.GameProfile;
import com.roland.identityv.core.IdentityV;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCs {
    public static IdentityV plugin;

    public NPCs(IdentityV plugin) {
        this.plugin = plugin;
    }

    public static EntityPlayer spawnNPC(Player p, Location location) {
        MinecraftServer server = ((CraftServer) plugin.getServer()).getServer();
        WorldServer world = ((CraftWorld) plugin.getServer().getWorlds().get(0)).getHandle();
        final EntityPlayer npc = new EntityPlayer(server,
                world,
                new GameProfile(p.getUniqueId(),
                        p.getDisplayName()),
                new PlayerInteractManager(world));

        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ScoreboardUtil.addNPCToTeam(npc.displayName);

        ItemStack feet = CraftItemStack.asNMSCopy(p.getInventory().getBoots());
        ItemStack legs = CraftItemStack.asNMSCopy(p.getInventory().getLeggings());
        ItemStack chest = CraftItemStack.asNMSCopy(p.getInventory().getChestplate());
        ItemStack head = CraftItemStack.asNMSCopy(p.getInventory().getHelmet());
        ItemStack hand = CraftItemStack.asNMSCopy(p.getInventory().getItemInHand());

        for (Player pl : plugin.getServer().getOnlinePlayers()) {
            final PlayerConnection connection = ((CraftPlayer)pl).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), 4, head));
            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), 3, chest));
            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), 2, legs));
            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), 1, feet));
            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), 0, hand));
        }

        return npc;
    }
}
