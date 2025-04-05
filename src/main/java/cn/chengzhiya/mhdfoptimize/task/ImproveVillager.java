package cn.chengzhiya.mhdfoptimize.task;

import cn.chengzhiya.mhdfoptimize.Main;
import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.reflection.EntityUtil;
import cn.chengzhiya.mhdfoptimize.util.runnable.MHDFRunnable;
import cn.chengzhiya.mhdfoptimize.util.scheduler.MHDFScheduler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.HashSet;
import java.util.Set;

public final class ImproveVillager extends MHDFRunnable {
    @Override
    public void run() {
        if (!ConfigUtil.getConfig().getBoolean("villager-ai-improve.enable")) {
            return;
        }

        Set<Villager> activeVillagers = new HashSet<>(16);
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            MHDFScheduler.getEntityScheduler().run(Main.instance, player, task -> {
                double x = ConfigUtil.getConfig().getDouble("villager-ai-improve.radius.x");
                double y = ConfigUtil.getConfig().getDouble("villager-ai-improve.radius.y");
                double z = ConfigUtil.getConfig().getDouble("villager-ai-improve.radius.z");
                if (y == -1) {
                    y = 0;
                }
                if (x == -1) {
                    x = 0;
                }
                if (z == -1) {
                    z = 0;
                }

                player.getNearbyEntities(x, y, z)
                        .stream()
                        .filter(entity -> entity instanceof Villager)
                        .map(entity -> (Villager) entity)
                        .forEach(activeVillagers::add);
            }, null);

            for (World world : Bukkit.getServer().getWorlds()) {
                MHDFScheduler.getRegionScheduler().run(Main.instance, world, 0, 0, task -> {
                    for (Villager villager : world.getEntitiesByClass(Villager.class)) {
                        MHDFScheduler.getEntityScheduler().run(Main.instance, villager, (a) -> {
                            boolean disableAI = !activeVillagers.contains(villager);
                            EntityUtil.controlEntityAI(villager, disableAI);
                        }, null);
                    }
                });
            }
        }
    }
}
