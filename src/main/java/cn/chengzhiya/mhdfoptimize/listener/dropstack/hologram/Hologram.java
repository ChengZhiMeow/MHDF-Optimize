package cn.chengzhiya.mhdfoptimize.listener.dropstack.hologram;

import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.item.DropStackUtil;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

public final class Hologram implements Listener {
    /**
     * 实体捡起堆叠掉落物时
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
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
