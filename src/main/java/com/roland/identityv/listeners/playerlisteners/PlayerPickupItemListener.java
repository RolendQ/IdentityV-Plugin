package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.gamecompmanagers.ItemManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.utils.Console;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerPickupItemListener implements Listener {
    private IdentityV plugin;

    public PlayerPickupItemListener(IdentityV plugin) {
        this.plugin = plugin;
    }

    /**
     * Detects when a player picks up an item
     * @param e
     */
    @EventHandler
    public void onPlayerPickupItem(final PlayerPickupItemEvent e) {

        if (SurvivorManager.isSurvivor(e.getPlayer())) {
            Survivor s = SurvivorManager.getSurvivor(e.getPlayer());

            // If different item from hand, cancel
            if (s.getItem() != null && (s.getItem().getMat() != e.getItem().getItemStack().getType() ||
                    s.getItem().getMat() != e.getPlayer().getItemInHand().getType())) {
                e.setCancelled(true);
                return;
            }

            if (ItemManager.isItem(e.getItem().getItemStack().getType())) {
                e.getPlayer().sendMessage("Picked up item: "+e.getItem().getItemStack().getType());
                // One
                if (s.getItem() == null) {
                    ItemManager.setItem(e.getItem().getItemStack().getType(),s);
                    // Check for double (stacks all)
                    new BukkitRunnable() {
                        public void run() {
                            // Counts copies of that material and puts total into hand
                            PlayerInventory inv = e.getPlayer().getInventory();
                            Material mat = e.getPlayer().getItemInHand().getType();
                            int total = 0;
                            for (ItemStack stack : inv.getContents()) {
                                if (stack != null && stack.getType() == mat) {
                                    total += stack.getAmount();
                                }
                            }
                            inv.remove(mat);
                            e.getPlayer().setItemInHand(new ItemStack(mat,total));
                        }
                    }.runTaskLater(plugin,5);
                }
                // Stack
                else {
                    e.setCancelled(true);
                    e.getItem().remove();
                    e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount() + 1);
                }
            }
        }
    }
}