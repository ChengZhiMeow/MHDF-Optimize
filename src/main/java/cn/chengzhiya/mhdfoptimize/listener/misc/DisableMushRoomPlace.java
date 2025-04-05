package cn.chengzhiya.mhdfoptimize.listener.misc;

import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public final class DisableMushRoomPlace implements Listener {
    /**
     * 阻止玩家在末地放置蘑菇
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!ConfigUtil.getConfig().getBoolean("disable.mushroom-place")) {
            return;
        }

        Material placedType = event.getBlockPlaced().getType();
        if ((placedType == Material.RED_MUSHROOM || placedType == Material.BROWN_MUSHROOM)
                && event.getBlockPlaced().getWorld().getEnvironment() != World.Environment.NORMAL) {
            event.setCancelled(true);
        }
    }
}
