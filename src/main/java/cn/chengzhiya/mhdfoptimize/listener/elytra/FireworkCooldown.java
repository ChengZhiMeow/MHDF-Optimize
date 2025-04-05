package cn.chengzhiya.mhdfoptimize.listener.elytra;

import cn.chengzhiya.mhdfoptimize.Main;
import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.scheduler.MHDFScheduler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;

public final class FireworkCooldown implements Listener {
    /**
     * 烟花火箭冷却
     */
    @EventHandler(ignoreCancelled = true)
    public void fireworkCooldown(PlayerInteractEvent event) {
        if (ConfigUtil.getConfig().getInt("elytra.firework-cooldown") <= 0) {
            return;
        }

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        if (event.getItem() == null) {
            return;
        }
        if (event.getItem().getType() != Material.FIREWORK_ROCKET) {
            return;
        }

        if (inventory.getChestplate() == null) {
            return;
        }
        if (inventory.getChestplate().getType() != Material.ELYTRA) {
            return;
        }

        if (player.getCooldown(Material.FIREWORK_ROCKET) > 0) {
            event.setUseItemInHand(Event.Result.DENY);
            return;
        }
        MHDFScheduler.getRegionScheduler().run(Main.instance, player.getLocation(), (task) -> {
            player.setCooldown(Material.FIREWORK_ROCKET, ConfigUtil.getConfig().getInt("elytra.firework-cooldown"));
        });
    }
}
