package cn.chengzhiya.mhdfoptimize.util.clean;

import cn.chengzhiya.mhdfoptimize.Main;
import cn.chengzhiya.mhdfoptimize.util.action.ActionUtil;
import cn.chengzhiya.mhdfoptimize.util.action.BossBarUtil;
import cn.chengzhiya.mhdfoptimize.util.action.SoundUtil;
import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.message.LogUtil;
import cn.chengzhiya.mhdfoptimize.util.runnable.MHDFRunnable;
import cn.chengzhiya.mhdfoptimize.util.scheduler.MHDFScheduler;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("deprecation")
public final class CleanUtil {
    @Getter
    private static final List<Boolean> cleanDoneList = new ArrayList<>();
    @Getter
    private static final HashMap<String, Integer> trashHashMap = new HashMap<>();
    @Getter
    @Setter
    private static int entityAmount = 0;
    @Getter
    @Setter
    private static int itemAmount = 0;

    public static void cleanEntity(World world) {
        Configuration config = ConfigUtil.getConfig();
        boolean filterAnimal = config.getBoolean("world-clean.entity.filter.animal");
        boolean filterMonster = config.getBoolean("world-clean.entity.filter.monster");
        boolean filterVillager = config.getBoolean("world-clean.entity.filter.villager");
        boolean filterBoss = config.getBoolean("world-clean.entity.filter.boss");
        boolean filterArmorStand = config.getBoolean("world-clean.entity.filter.armor-stand");
        boolean filterNamed = config.getBoolean("world-clean.entity.filter.name");
        boolean filterLeashed = config.getBoolean("world-clean.entity.filter.lead");
        boolean blacklistMode = config.getBoolean("world-clean.entity.black-list");
        List<String> entityList = config.getStringList("world-clean.entity.list");

        MHDFScheduler.getRegionScheduler().run(Main.instance, world, 0, 0, (task) -> {
            for (LivingEntity entity : world.getLivingEntities()) {
                MHDFScheduler.getEntityScheduler().run(Main.instance, entity, (task1) -> {
                    if (entity instanceof Player) {
                        return;
                    }

                    if ((entity instanceof Animals && filterAnimal) ||
                            (entity instanceof Monster && filterMonster) ||
                            (entity instanceof Villager && filterVillager) ||
                            (entity instanceof Boss && filterBoss) ||
                            (entity instanceof ArmorStand && filterArmorStand)) {
                        return;
                    }

                    if ((entity.getCustomName() != null && filterNamed) ||
                            (entity.isLeashed() && filterLeashed)) {
                        return;
                    }

                    boolean inList = entityList.contains(entity.getType().name());
                    boolean shouldKeep = (blacklistMode && !inList) || (!blacklistMode && inList);
                    if (shouldKeep) {
                        return;
                    }

                    entity.remove();
                    CleanUtil.setEntityAmount(CleanUtil.getEntityAmount() + 1);
                }, null);
            }
            getCleanDoneList().add(true);
        });
    }


    /**
     * 清理物品
     *
     * @param world 世界实例
     */
    public static void cleanItem(World world) {
        Configuration config = ConfigUtil.getConfig();
        boolean filterName = config.getBoolean("world-clean.item.filter.name");
        boolean filterLore = config.getBoolean("world-clean.item.filter.lore");
        boolean filterCustomModelData = config.getBoolean("world-clean.item.filter.custom-model-data");
        boolean filterEnchant = config.getBoolean("world-clean.item.filter.enchant");
        boolean filterWrittenBook = config.getBoolean("world-clean.item.filter.written-book");
        boolean blacklistMode = config.getBoolean("world-clean.item.black-list");
        List<String> itemList = config.getStringList("world-clean.item.list");

        MHDFScheduler.getRegionScheduler().run(Main.instance, world, 0, 0, (task) -> {
            for (Entity entity : world.getEntities()) {
                MHDFScheduler.getEntityScheduler().run(Main.instance, entity, (task1) -> {
                    if (!(entity instanceof Item item)) {
                        return;
                    }
                    ItemStack itemStack = item.getItemStack();
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    if (itemMeta != null) {
                        if (filterName && itemMeta.hasDisplayName()) {
                            return;
                        }

                        if (filterLore && itemMeta.hasLore()) {
                            return;
                        }

                        if (filterCustomModelData && itemMeta.hasCustomModelData()) {
                            return;
                        }

                        if (filterEnchant && !itemMeta.getEnchants().isEmpty()) {
                            return;
                        }

                        if (filterWrittenBook &&
                                itemStack.getType() == Material.WRITABLE_BOOK &&
                                ((BookMeta) itemMeta).hasPages()
                        ) {
                            return;
                        }
                    }

                    boolean inList = itemList.contains(itemStack.getType().name());
                    boolean shouldKeep = (blacklistMode && !inList) || (!blacklistMode && inList);
                    if (shouldKeep) {
                        return;
                    }

                    ItemStack cloneItem = itemStack.clone();
                    cloneItem.setAmount(1);
                    ReadWriteNBT nbt = NBT.itemStackToNBT(cloneItem.clone());

                    int amount = getTrashHashMap().get(nbt.toString()) != null
                            ? getTrashHashMap().get(nbt.toString()) : 0;
                    getTrashHashMap().put(nbt.toString(), amount + itemStack.getAmount());

                    item.remove();
                    CleanUtil.setItemAmount(CleanUtil.getItemAmount() + 1);
                }, null);
            }
            getCleanDoneList().add(true);
        });
    }

    /**
     * 开始清理
     */
    public static void clean() {
        CleanUtil.getCleanDoneList().clear();
        CleanUtil.setEntityAmount(0);
        CleanUtil.setItemAmount(0);

        MHDFScheduler.getGlobalRegionScheduler().run(Main.instance, (task) -> {
            CopyOnWriteArrayList<String> worldList = new CopyOnWriteArrayList<>(ConfigUtil.getConfig().getStringList("world-clean.world"));
            if (!Bukkit.getAllowEnd()) {
                worldList.remove("world_the_end");
            }

            for (String name : worldList) {
                World world = Bukkit.getWorld(name);
                if (world == null) {
                    worldList.remove(name);
                    continue;
                }

                CleanUtil.cleanEntity(world);
                CleanUtil.cleanItem(world);
            }

            String type = ConfigUtil.getConfig().getString("world-clean.message-type");
            String message = ConfigUtil.getConfig().getString("world-clean.message.finish");
            if (message == null) {
                return;
            }

            MHDFRunnable messageRunnable = new MHDFRunnable() {
                @Override
                public void run() {
                    if (getCleanDoneList().size() / 2 >= worldList.size()) {
                        String amountMessage = message
                                .replace("{cleanEntity}", String.valueOf(getEntityAmount()))
                                .replace("{cleanItem}", String.valueOf(getItemAmount()));

                        sendMessage(type, amountMessage, ConfigUtil.getConfig().getInt("world-clean.bossbar.showTime"));
                        playSound(ConfigUtil.getConfig().getString("world-clean.sound.finish"));
                        this.cancel();
                    }
                }
            };

            messageRunnable.runTaskTimerAsynchronously(Main.instance, 0L, 20L);
        });
    }

    /**
     * 全服发送消息
     *
     * @param message 消息
     */
    public static void sendMessage(String type, String message, int showTime) {
        if (type == null || message == null) {
            return;
        }

        LogUtil.log(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (type.equals("bossbar")) {
                ActionUtil.sendTimeBossbar(
                        player,
                        BossBarUtil.getBossBar(
                                message,
                                BossBar.Color.valueOf(ConfigUtil.getConfig().getString("world-clean.bossbar.color")),
                                BossBar.Overlay.valueOf(ConfigUtil.getConfig().getString("world-clean.bossbar.style"))
                        ),
                        (long) showTime
                );
            } else if (type.equals("actionBar")) {
                ActionUtil.sendActionBar(player, message);
            } else {
                ActionUtil.sendMessage(player, message);
            }
        }
    }

    /**
     * 全服播放音频
     *
     * @param sound 音频
     */
    public static void playSound(String sound) {
        if (sound == null) {
            return;
        }
        String[] data = sound.split("\\|");
        for (Player player : Bukkit.getOnlinePlayers()) {
            ActionUtil.playSound(
                    player,
                    SoundUtil.getSound(data[0]),
                    Float.parseFloat(data[1]),
                    Float.parseFloat(data[2])
            );
        }
    }
}
