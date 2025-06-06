package cn.chengzhiya.mhdfoptimize;

import cn.chengzhiya.langutil.LangAPI;
import cn.chengzhiya.langutil.manager.lang.LangManager;
import cn.chengzhiya.mhdfoptimize.commands.MHDFOptimize;
import cn.chengzhiya.mhdfoptimize.commands.Trash;
import cn.chengzhiya.mhdfoptimize.listener.EntityAi;
import cn.chengzhiya.mhdfoptimize.listener.EntitySpawn;
import cn.chengzhiya.mhdfoptimize.listener.Menu;
import cn.chengzhiya.mhdfoptimize.listener.dropstack.DropStack;
import cn.chengzhiya.mhdfoptimize.listener.dropstack.hologram.Hologram;
import cn.chengzhiya.mhdfoptimize.listener.dropstack.hologram.OldHologram;
import cn.chengzhiya.mhdfoptimize.listener.misc.DisableEntityPortal;
import cn.chengzhiya.mhdfoptimize.listener.misc.DisableMushRoomGrow;
import cn.chengzhiya.mhdfoptimize.listener.misc.DisableMushRoomPlace;
import cn.chengzhiya.mhdfoptimize.listener.misc.DisableVillagerTrade;
import cn.chengzhiya.mhdfoptimize.manager.AdventureManager;
import cn.chengzhiya.mhdfoptimize.manager.PluginHookManager;
import cn.chengzhiya.mhdfoptimize.task.CleanWorld;
import cn.chengzhiya.mhdfoptimize.task.ImproveVillager;
import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.config.LangUtil;
import cn.chengzhiya.mhdfoptimize.util.config.MenuConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.message.LogUtil;
import cn.chengzhiya.mhdfoptimize.util.version.VersionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

@Getter
public final class Main extends JavaPlugin {
    public static Main instance;

    private boolean nativeSupportAdventureApi;

    private LangManager langManager;
    private PluginHookManager pluginHookManager;
    private AdventureManager adventureManager;

    @Override
    public void onLoad() {
        instance = this;

        try {
            Class.forName("net.kyori.adventure.text.Component");
            this.nativeSupportAdventureApi = true;
        } catch (Exception e) {
            this.nativeSupportAdventureApi = false;
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.adventureManager = new AdventureManager();
        this.adventureManager.init();

        ConfigUtil.saveDefaultConfig();
        ConfigUtil.reloadConfig();

        LangUtil.saveDefaultLang();
        LangUtil.reloadLang();

        LangAPI langAPI = new LangAPI(this, new File(getDataFolder(), "minecraftLang"));
        this.langManager = langAPI.getLangManager();
        this.langManager.downloadLang();
        this.langManager.reloadLang();

        MenuConfigUtil.saveDefaultMenu();

        Bukkit.getPluginManager().registerEvents(new DropStack(), this);
        Bukkit.getPluginManager().registerEvents(new EntitySpawn(), this);
        Bukkit.getPluginManager().registerEvents(new EntityAi(), this);

        Bukkit.getPluginManager().registerEvents(new DisableEntityPortal(), this);
        Bukkit.getPluginManager().registerEvents(new DisableVillagerTrade(), this);

        Bukkit.getPluginManager().registerEvents(new DisableMushRoomGrow(), this);
        Bukkit.getPluginManager().registerEvents(new DisableMushRoomPlace(), this);

        Bukkit.getPluginManager().registerEvents(new Menu(), this);
        new CleanWorld().runTaskTimerAsynchronously(Main.instance, 0L, 20L);
        new ImproveVillager().runTaskTimerAsynchronously(Main.instance, 0L, 40L);

        if (VersionUtil.is1_12orAbove()) {
            Bukkit.getPluginManager().registerEvents(new Hologram(), this);
        } else {
            Bukkit.getPluginManager().registerEvents(new OldHologram(), this);
        }

        Objects.requireNonNull(getCommand("mhdfoptimize")).setExecutor(new MHDFOptimize());
        Objects.requireNonNull(getCommand("mhdfoptimize")).setTabCompleter(new MHDFOptimize());

        Objects.requireNonNull(getCommand("trash")).setExecutor(new Trash());

        this.pluginHookManager = new PluginHookManager();
        this.pluginHookManager.hook();

        LogUtil.log("&e-----------&6=&e梦之优化&6=&e-----------");
        LogUtil.log("&a插件启动成功! 官方交流群: 129139830");
        LogUtil.log("&e-----------&6=&e梦之优化&6=&e-----------");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.pluginHookManager.unhook();

        LogUtil.log("&e-----------&6=&e梦之优化&6=&e-----------");
        LogUtil.log("&a插件卸载成功! 官方交流群: 129139830");
        LogUtil.log("&e-----------&6=&e梦之优化&6=&e-----------");

        this.pluginHookManager = null;
        this.langManager = null;

        this.adventureManager.close();
        this.adventureManager = null;

        instance = null;
    }
}
