package cn.chengzhiya.mhdfoptimize.listener.dropstack;

import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.item.DropStackUtil;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class DropStack implements Listener {
    /**
     * 处理掉落物堆叠
     */
    @EventHandler
    public void onItemSpawn(EntitySpawnEvent event) {
        if (!ConfigUtil.getConfig().getBoolean("drop-stack.enable")) {
            return;
        }
        if (event.getEntity() instanceof Item item) {
            DropStackUtil.stackItem(item);
        }
    }

    /**
     * 玩家捡起堆叠掉落物时
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!ConfigUtil.getConfig().getBoolean("drop-stack.enable")) {
            return;
        }

        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();
        if (itemStack.getAmount() > itemStack.getMaxStackSize()) {
            event.setCancelled(true);
            DropStackUtil.inventoryStacking(event.getPlayer().getInventory(), item);
        }
    }

    /**
     * 方块捡起堆叠掉落物时
     */
    @EventHandler(ignoreCancelled = true)
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        if (!ConfigUtil.getConfig().getBoolean("drop-stack.enable")) {
            return;
        }

        Inventory inventory = event.getInventory();
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();

        if (itemStack.getAmount() > itemStack.getMaxStackSize()) {
            event.setCancelled(true);
            DropStackUtil.inventoryStacking(inventory, item);
        }
    }
}