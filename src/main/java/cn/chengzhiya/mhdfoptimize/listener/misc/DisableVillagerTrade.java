package cn.chengzhiya.mhdfoptimize.listener.misc;

import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public final class DisableVillagerTrade implements Listener {

    /**
     * 禁止村民交易
     */
    @EventHandler(ignoreCancelled = true)
    public void disableVillagerTrade(PlayerInteractEntityEvent event) {
        if (!ConfigUtil.getConfig().getBoolean("misc.villager-trade")) {
            return;
        }
        if (event.getRightClicked().getType() != EntityType.VILLAGER) {
            return;
        }

        event.setCancelled(true);
    }
}
