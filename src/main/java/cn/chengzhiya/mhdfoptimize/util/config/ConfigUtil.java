package cn.chengzhiya.mhdfoptimize.util.config;

import cn.chengzhiya.mhdfoptimize.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

@SuppressWarnings("unused")
public final class ConfigUtil {
    /**
     * 获取插件数据文件夹
     */
    public static File getDataFolder() {
        return Main.instance.getDataFolder();
    }

    /**
     * 获取配置文件实例
     */
    public static FileConfiguration getConfig() {
        return Main.instance.getConfig();
    }

    /**
     * 重新加载配置文件
     */
    public static void reloadConfig() {
        Main.instance.reloadConfig();
    }

    /**
     * 保存初始配置文件
     */
    public static void saveDefaultConfig() {
        FileUtil.createFolder(getDataFolder());
        FileUtil.saveResource("config.yml", "config_zh.yml", false);
    }
}
