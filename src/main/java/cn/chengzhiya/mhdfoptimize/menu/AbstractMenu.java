package cn.chengzhiya.mhdfoptimize.menu;

import cn.chengzhiya.mhdfoptimize.Main;
import cn.chengzhiya.mhdfoptimize.interfaces.Menu;
import cn.chengzhiya.mhdfoptimize.util.scheduler.MHDFScheduler;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
public abstract class AbstractMenu implements InventoryHolder, Menu {
    private final Player player;

    public AbstractMenu(Player player) {
        this.player = player;
    }

    public void onOpen(InventoryOpenEvent event) {
        open(event);
    }

    public void onClick(InventoryClickEvent event) {
        click(event);
    }

    public void onClose(InventoryCloseEvent event) {
        close(event);
    }

    public void openMenu() {
        MHDFScheduler.getAsyncScheduler().runNow(Main.instance, (task) -> {
            Inventory menu = getInventory();

            MHDFScheduler.getGlobalRegionScheduler().run(Main.instance, (task1) -> player.openInventory(menu));
        });
    }
}
