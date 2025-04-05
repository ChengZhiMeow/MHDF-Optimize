package cn.chengzhiya.mhdfoptimize.listener;

import cn.chengzhiya.mhdfoptimize.Main;
import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public final class EntitySpawn implements Listener {
    /**
     * 禁止指定实体生成
     */
    @EventHandler(ignoreCancelled = true)
    public void disableEntityList(CreatureSpawnEvent event) {
        if (Main.instance.getPluginHookManager().getMythicMobsHook()
                .isMythicMob(event.getEntity())
        ) {
            return;
        }

        if (!ConfigUtil.getConfig().getStringList("entity-spawn.disable-entity-list").contains(event.getEntityType().toString())) {
            return;
        }

        event.setCancelled(true);
    }

    /**
     * 调整指定原因生成实体的概率
     */
    @EventHandler(ignoreCancelled = true)
    public void changeSpawnReasonChange(CreatureSpawnEvent event) {
        if (Main.instance.getPluginHookManager().getMythicMobsHook()
                .isMythicMob(event.getEntity())
        ) {
            return;
        }

        ConfigurationSection config = ConfigUtil.getConfig().getConfigurationSection("entity-spawn.change-spawn-chance.spawn-reason");
        if (config == null) {
            return;
        }

        for (String key : config.getKeys(false)) {
            if (!CreatureSpawnEvent.SpawnReason.valueOf(key).equals(event.getSpawnReason())) {
                continue;
            }
            if (Math.random() <= config.getDouble(key)) {
                return;
            }

            event.setCancelled(true);
        }
    }

    /**
     * 调整指定实体生成实体的概率
     */
    @EventHandler(ignoreCancelled = true)
    public void changeSpawnEntityChange(EntitySpawnEvent event) {
        if (Main.instance.getPluginHookManager().getMythicMobsHook()
                .isMythicMob(event.getEntity())
        ) {
            return;
        }

        ConfigurationSection config = ConfigUtil.getConfig().getConfigurationSection("entity-spawn.change-spawn-chance.spawn-entity");
        if (config == null) {
            return;
        }

        for (String key : config.getKeys(false)) {
            if (!EntityType.valueOf(key).equals(event.getEntityType())) {
                continue;
            }
            if (Math.random() <= config.getDouble(key)) {
                return;
            }

            event.setCancelled(true);
        }
    }
}
