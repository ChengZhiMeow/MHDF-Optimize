package cn.chengzhiya.mhdfoptimize.listener.dropstack.hologram;

import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.item.DropStackUtil;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public final class OldHologram implements Listener {
    /**
     * 玩家捡起堆叠掉落物时
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!ConfigUtil.getConfig().getBoolean("drop-stack.enable")) {
            return;
        }

        if (!ConfigUtil.getConfig().getBoolean("drop-stack.hologram.enable")) {
            return;
        }

        Item item = event.getItem();
        DropStackUtil.displayItemHologram(item);
    }

    /**
     * 方块捡起堆叠掉落物时
     */
    @EventHandler(ignoreCancelled = true)
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        if (!ConfigUtil.getConfig().getBoolean("drop-stack.enable")) {
            return;
        }
        if (!ConfigUtil.getConfig().getBoolean("drop-stack.hologram.enable")) {
            return;
        }

        Item item = event.getItem();
        DropStackUtil.displayItemHologram(item);
    }
}
