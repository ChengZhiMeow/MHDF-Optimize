package cn.chengzhiya.mhdfoptimize.listener.misc;

import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

public final class DisableMushRoomGrow implements Listener {
    /**
     * 阻止蘑菇在末地蔓延/生长
     */
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (!ConfigUtil.getConfig().getBoolean("disable.mushroom-grow")) {
            return;
        }

        Material sourceType = event.getSource().getType();
        if ((sourceType == Material.RED_MUSHROOM || sourceType == Material.BROWN_MUSHROOM)
                && event.getBlock().getWorld().getEnvironment() != World.Environment.NORMAL) {
            event.setCancelled(true);
        }
    }
}
