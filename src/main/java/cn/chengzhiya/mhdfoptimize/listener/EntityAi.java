package cn.chengzhiya.mhdfoptimize.listener;

import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.reflection.EntityUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public final class EntityAi implements Listener {
    /**
     * 关闭自然生成的实体AI
     */
    @EventHandler(ignoreCancelled = true)
    public void disableNatural(CreatureSpawnEvent event) {
        if (!ConfigUtil.getConfig().getBoolean("entity-AI.disable-natural")) {
            return;
        }
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }

        if (ConfigUtil.getConfig().getStringList("entity-AI.disable-entity-list").contains(event.getEntityType().toString())) {
            EntityUtil.controlEntityAI(event.getEntity(), false);
        }
    }

    /**
     * 关闭刷怪笼生成的实体AI
     */
    @EventHandler(ignoreCancelled = true)
    public void disableSpawner(CreatureSpawnEvent event) {
        if (!ConfigUtil.getConfig().getBoolean("entity-AI.disable-spawner")) {
            return;
        }
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER) {
            return;
        }

        if (ConfigUtil.getConfig().getStringList("entity-AI.disable-entity-list").contains(event.getEntityType().toString())) {
            EntityUtil.controlEntityAI(event.getEntity(), false);
        }
    }

    /**
     * 关闭指定实体类型的实体AI
     */
    @EventHandler(ignoreCancelled = true)
    public void disableEntityList(CreatureSpawnEvent event) {
        if (ConfigUtil.getConfig().getStringList("entity-AI.disable-entity-list").contains(event.getEntityType().toString())) {
            EntityUtil.controlEntityAI(event.getEntity(), false);
        }
    }
}