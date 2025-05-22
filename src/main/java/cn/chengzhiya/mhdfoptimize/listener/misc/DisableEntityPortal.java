package cn.chengzhiya.mhdfoptimize.listener.misc;

import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.scheduler.MHDFScheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;

public final class DisableEntityPortal implements Listener {
    /**
     * 禁止非玩家实体通过传送门
     */
    @EventHandler(ignoreCancelled = true)
    public void disableEntityPortal(EntityPortalEvent event) {
        if (MHDFScheduler.isFolia()) {
            return;
        }

        if (!ConfigUtil.getConfig().getBoolean("disable.entity-enter-portal")) {
            return;
        }

        event.setCancelled(true);
    }
}
