package cn.chengzhiya.mhdfoptimize.manager;

import cn.chengzhiya.mhdfoptimize.Main;
import cn.chengzhiya.mhdfoptimize.interfaces.Init;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

@Getter
public final class AdventureManager implements Init {
    private BukkitAudiences adventure;

    @Override
    public void init() {
        this.adventure = BukkitAudiences.create(Main.instance);
    }

    public void close() {
        if (this.adventure != null) {
            this.adventure.close();
        }

        this.adventure = null;
    }
}
