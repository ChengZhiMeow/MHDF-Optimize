package cn.chengzhiya.mhdfoptimize.listener.fallingblock;

import cn.chengzhiya.mhdfoptimize.Main;
import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.scheduler.MHDFScheduler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.List;

public final class FallingBlockListener implements Listener {

    @EventHandler
    public void EntityChangeBlockEvent(final EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            final int maxCountInRange = ConfigUtil.getConfig().getInt("falling-block.count");
            final int radiusX = ConfigUtil.getConfig().getInt("falling-block.radius.x");
            final int radiusY = ConfigUtil.getConfig().getInt("falling-block.radius.y");
            final int radiusZ = ConfigUtil.getConfig().getInt("falling-block.radius.z");
            final List<Entity> allEntitiesInRadius = event.getEntity().getNearbyEntities(radiusX, radiusY, radiusZ).stream().filter(entity -> entity.getType() == EntityType.FALLING_BLOCK).toList();
            if (allEntitiesInRadius.size() > maxCountInRange) {
                MHDFScheduler.getRegionScheduler().run(Main.instance, event.getEntity().getWorld(), event.getEntity().getChunk().getX(), event.getEntity().getChunk().getZ(), (task) -> {
                    for (final Entity entitiesInRadius : allEntitiesInRadius) {
                        MHDFScheduler.getEntityScheduler().run(Main.instance, entitiesInRadius, (a) ->
                                entitiesInRadius.remove(), null);
                    }
                });
            }
        }
    }
}
