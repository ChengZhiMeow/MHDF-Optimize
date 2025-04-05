package cn.chengzhiya.mhdfoptimize.util.item;

import cn.chengzhiya.mhdfoptimize.Main;
import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.message.ColorUtil;
import cn.chengzhiya.mhdfoptimize.util.reflection.EntityUtil;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public final class DropStackUtil {
    /**
     * 显示物品的全息名字
     *
     * @param item 当前物品
     */
    public static void displayItemHologram(Item item) {
        if (!ConfigUtil.getConfig().getBoolean("dropStack.hologram.enable")) {
            return;
        }
        if (ConfigUtil.getConfig().getString("dropStack.hologram.format") == null) {
            return;
        }

        item.setCustomNameVisible(true);

        ItemStack stack = item.getItemStack();
        String itemName = Main.instance.getLangManager().getItemName(stack);

        if (itemName == null) return;

        String hologramText = Objects.requireNonNull(ConfigUtil.getConfig().getString("dropStack.hologram.format"))
                .replace("{name}", itemName)
                .replace("{amount}", String.valueOf(stack.getAmount()));

        item.customName(ColorUtil.color(hologramText));
    }

    /**
     * 处理掉落物品堆叠逻辑
     *
     * @param item 丢弃的物品
     */
    public static void stackItem(Item item) {
        Item nearbyItem = findNearbyItem(item);

        if (nearbyItem == null) {
            displayItemHologram(item);
            return;
        }

        ItemStack itemStack = item.getItemStack();
        ItemStack nearbyItemStack = nearbyItem.getItemStack();

        int maxStack = ConfigUtil.getConfig().getInt("dropStack.maxStack");
        int combinedAmount = itemStack.getAmount() + nearbyItemStack.getAmount();

        if (maxStack > 0 && combinedAmount <= maxStack) {
            nearbyItemStack.setAmount(combinedAmount);
            item.remove();
            item.setPickupDelay(15);
            item.setTicksLived(1);
        }
        displayItemHologram(nearbyItem);
    }

    /**
     * 查找附近的物品
     *
     * @param item 当前物品
     * @return 相似的物品
     */
    public static Item findNearbyItem(Item item) {
        ItemStack itemStack = item.getItemStack();

        double range = ConfigUtil.getConfig().getDouble("dropStack.range");
        for (Entity entity : item.getNearbyEntities(range, range, range)) {
            if (entity.getType() != EntityUtil.DROPPED_ITEM) continue;

            Item nearbyItem = (Item) entity;
            ItemStack nearbyItemStack = nearbyItem.getItemStack();

            if (nearbyItem.getUniqueId().equals(item.getUniqueId())) continue;
            if (nearbyItem.getPickupDelay() > 1000) continue;
            if (!nearbyItemStack.isSimilar(itemStack)) continue;

            int maxStack = ConfigUtil.getConfig().getInt("dropStack.maxStack");
            int combinedAmount = itemStack.getAmount() + nearbyItemStack.getAmount();
            if (combinedAmount >= maxStack) continue;

            return nearbyItem;
        }
        return null;
    }

    /**
     * 处理容器内物品堆叠
     *
     * @param inventory 容器背包
     * @param item      当前物品
     */
    public static void inventoryStacking(Inventory inventory, Item item) {
        ItemStack stack = item.getItemStack();
        int slots = inventory instanceof PlayerInventory ? 35 : inventory.getContents().length;

        for (int i = 0; i < slots; i++) {
            if (stack.getAmount() <= 0) {
                break;
            }

            ItemStack currentItem = inventory.getContents()[i];

            if (currentItem == null || currentItem.getAmount() <= 0) {
                currentItem = stack.clone();
                if (stack.getAmount() > stack.getMaxStackSize()) {
                    currentItem.setAmount(stack.getMaxStackSize());
                    stack.setAmount(stack.getAmount() - stack.getMaxStackSize());
                } else {
                    currentItem.setAmount(stack.getAmount());
                    stack.setAmount(0);
                }

                inventory.setItem(i, currentItem);
                item.setItemStack(stack);
                continue;
            }

            if (stack.getType() == currentItem.getType() && NBT.readNbt(stack).toString().equals(NBT.readNbt(currentItem).toString())) {
                if (currentItem.getAmount() >= currentItem.getMaxStackSize()) continue;

                int availableSpace = currentItem.getMaxStackSize() - currentItem.getAmount();
                if (stack.getAmount() >= availableSpace) {
                    stack.setAmount(stack.getAmount() - availableSpace);
                    currentItem.setAmount(currentItem.getMaxStackSize());
                } else {
                    currentItem.setAmount(stack.getAmount() + availableSpace);
                    stack.setAmount(0);
                }

                item.setItemStack(stack);
                inventory.setItem(i, currentItem);
            }
        }

        item.setItemStack(stack);
        displayItemHologram(item);
    }
}
