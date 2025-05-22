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

public class ImproveVillager extends MHDFRunnable {

    @Override
    public void run() {
        optimizeVillagerAI();
    }

    /**
     * 优化村民AI方法：
     * <p>
     * 1. 遍历所有在线玩家，在玩家周围12格范围内寻找村民实体，并记录到活跃村民集合中。
     * 2. 遍历所有世界中的所有村民，根据是否在活跃村民集合中判断是否禁用其AI：
     * - 如果村民不在任何玩家附近，则禁用其AI以降低性能消耗；
     * - 否则，保持村民的AI正常运行。
     * </p>
     */
    private void optimizeVillagerAI() {
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

                player.getNearbyEntities(x, y ,z)
                        .stream()
                        .filter(entity -> entity instanceof Villager)
                        .map(entity -> (Villager) entity)
                        .forEach(activeVillagers::add);
            }, null);

            for (World world : Bukkit.getServer().getWorlds()) {
                MHDFScheduler.getRegionScheduler().run(Main.instance, world, 0, 0, task -> {
                    for (Villager villager : world.getEntitiesByClass(Villager.class)) {
                        MHDFScheduler.getEntityScheduler().run(Main.instance, villager, (a) -> {
                            boolean disableAI = activeVillagers.contains(villager);
                            EntityUtil.controlEntityAI(villager, disableAI);
                        }, null);
                    }
                });
            }
        }
    }
}
