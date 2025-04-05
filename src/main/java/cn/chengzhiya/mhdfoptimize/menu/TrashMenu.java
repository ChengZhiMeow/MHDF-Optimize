package cn.chengzhiya.mhdfoptimize.menu;

import cn.chengzhiya.mhdfoptimize.Main;
import cn.chengzhiya.mhdfoptimize.builder.ItemStackBuilder;
import cn.chengzhiya.mhdfoptimize.util.action.ActionUtil;
import cn.chengzhiya.mhdfoptimize.util.clean.CleanUtil;
import cn.chengzhiya.mhdfoptimize.util.config.MenuConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.menu.MenuUtil;
import cn.chengzhiya.mhdfoptimize.util.message.ColorUtil;
import de.tr7zw.changeme.nbtapi.NBT;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public final class TrashMenu extends AbstractMenu {
    private final YamlConfiguration config;
    private final int page;

    public TrashMenu(Player player, int page) {
        super(
                player
        );
        this.config = MenuConfigUtil.getMenuConfig("trash.yml");
        this.page = page;
    }

    @Override
    public @NotNull Inventory getInventory() {
        int size = getConfig().getInt("size");
        int itemSize = getConfig().getInt("itemSize");
        String title = getConfig().getString("title");

        Inventory menu = Bukkit.createInventory(this, size, ColorUtil.color(Objects.requireNonNull(title)));

        ConfigurationSection items = getConfig().getConfigurationSection("items");
        if (items == null) {
            return menu;
        }

        List<String> itemList = new ArrayList<>(CleanUtil.getTrashHashMap().keySet());
        int start = (page - 1) * itemSize;
        int maxEnd = page * itemSize;
        int end = Math.min(itemList.size(), maxEnd);

        for (String key : items.getKeys(false)) {
            ConfigurationSection item = items.getConfigurationSection(key);

            if (item == null) {
                continue;
            }

            String name = item.getString("name");
            List<String> lore = item.getStringList("lore");
            Integer amount = item.getInt("amount");
            Integer customModelData = item.getInt("customModelData");

            switch (key) {
                case "物品" -> {
                    for (int i = start; i < end; i++) {
                        String nbt = itemList.get(i);
                        int dataItemAmount = CleanUtil.getTrashHashMap().get(nbt);
                        ItemStack dataItem = NBT.itemStackFromNBT(NBT.parseNBT(nbt));

                        ItemStack itemStack = new ItemStackBuilder(getPlayer(), dataItem)
                                .name(applyItemStackString(name, dataItem, dataItemAmount))
                                .lore(lore.stream()
                                        .map(s -> applyItemStackString(s, dataItem, dataItemAmount))
                                        .toList())
                                .amount(amount)
                                .customModelData(customModelData)
                                .persistentDataContainer("key", PersistentDataType.STRING, key)
                                .persistentDataContainer("nbt", PersistentDataType.BYTE_ARRAY, nbt.getBytes())
                                .build();

                        menu.addItem(itemStack);
                    }
                    continue;
                }
                case "上一页" -> {
                    if (page <= 1) {
                        continue;
                    }
                }
                case "下一页" -> {
                    if (itemList.size() <= maxEnd) {
                        continue;
                    }
                }
            }

            MenuUtil.setMenuItem(getPlayer(), menu, item, key);
        }

        return menu;
    }

    @Override
    public void open(InventoryOpenEvent event) {
        ActionUtil.runActionList(getPlayer(), getConfig().getStringList("openActions"));
    }

    @Override
    public void click(InventoryClickEvent event) {
        ItemStack itemStack = MenuUtil.getClickItem(event);
        if (itemStack == null) {
            return;
        }

        event.setCancelled(true);

        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();

        String key = container.get(new NamespacedKey(Main.instance, "key"), PersistentDataType.STRING);
        if (key == null) {
            return;
        }

        switch (key) {
            case "物品" -> {
                byte[] nbtBytes = container.get(new NamespacedKey(Main.instance, "nbt"), PersistentDataType.BYTE_ARRAY);
                if (nbtBytes == null) {
                    return;
                }

                String nbt = new String(nbtBytes);
                int dataItemAmount = CleanUtil.getTrashHashMap().get(nbt);
                ItemStack dataItem = NBT.itemStackFromNBT(NBT.parseNBT(nbt));
                if (dataItem == null) {
                    return;
                }

                int getAmount = 1;
                if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                    getAmount = Math.min(dataItem.getMaxStackSize(), dataItemAmount);
                }

                ItemStack getItem = dataItem.clone();
                getItem.setAmount(getAmount);
                getPlayer().getInventory().addItem(getItem);

                dataItemAmount = dataItemAmount - getAmount;
                if (dataItemAmount > 0) {
                    CleanUtil.getTrashHashMap().put(nbt, dataItemAmount);
                } else {
                    CleanUtil.getTrashHashMap().remove(nbt);
                }

                new TrashMenu(getPlayer(), getPage()).openMenu();
            }
            case "上一页" -> new TrashMenu(getPlayer(), getPage() - 1).openMenu();
            case "下一页" -> new TrashMenu(getPlayer(), getPage() + 1).openMenu();
        }

        MenuUtil.runItemClickAction(getPlayer(), getConfig(), key);
    }

    @Override
    public void close(InventoryCloseEvent event) {
        ActionUtil.runActionList(getPlayer(), getConfig().getStringList("closeActions"));
    }

    /**
     * 处理物品实例文本
     *
     * @param message   文本
     * @param itemStack 物品实例
     * @param amount    物品数量
     * @return 处理后的文本
     */
    private String applyItemStackString(String message, ItemStack itemStack, int amount) {
        if (message == null) {
            return null;
        }

        return message
                .replace("{name}", Main.instance.getLangManager()
                        .getItemName(itemStack))
                .replace("{amount}", String.valueOf(amount));
    }
}