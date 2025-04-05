package cn.chengzhiya.mhdfoptimize.hook;

import cn.chengzhiya.mhdfoptimize.hook.impl.MythicMobsImpl;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

@Getter
public final class MythicMobsHook extends AbstractHook {
    private MythicMobsImpl api;

    @Override
    public void hook() {
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            this.api = new MythicMobsImpl();
            super.enable = true;
        }
    }

    @Override
    public void unhook() {
        this.api = null;
        super.enable = false;
    }

    /**
     * 获取指定物品ID的物品实例
     *
     * @param id 物品ID
     * @return 物品实例
     */
    public ItemStack getItem(String id) {
        if (isEnable()) {
            return getApi().getItem(id);
        }
        return new ItemStack(Material.AIR);
    }

    /**
     * 获取指定物品实例的物品ID
     *
     * @param itemStack 物品实例
     * @return 物品ID
     */
    public String getItemId(ItemStack itemStack) {
        if (isEnable()) {
            return getApi().getItemId(itemStack);
        }
        return null;
    }

    /**
     * 检测指定实体实例是否为MM实体
     *
     * @param entity 实体实例
     * @return 结果
     */
    public boolean isMythicMob(Entity entity) {
        if (isEnable()) {
            return getApi().isMythicMob(entity);
        }
        return false;
    }
}
