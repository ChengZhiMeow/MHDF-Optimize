package cn.chengzhiya.mhdfoptimize.util.message;

import cn.chengzhiya.mhdfoptimize.util.action.ActionUtil;
import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import org.bukkit.Bukkit;

public final class LogUtil {
    private static final String CONSOLE_PREFIX = "[MHDF-Optimize] ";
    private static final String DEBUG_PREFIX = "[MHDF-Optimize-Debug] ";

    /**
     * 日志消息
     *
     * @param message 内容
     * @param args    参数
     */
    public static void log(String message, String... args) {
        for (Object var : args) {
            message = message.replaceFirst("\\{}", var.toString());
        }
        ActionUtil.sendMessage(Bukkit.getConsoleSender(), ColorUtil.color(CONSOLE_PREFIX + message));
    }

    /**
     * 调试消息
     *
     * @param message 内容
     * @param args    参数
     */
    public static void debug(String message, String... args) {
        if (!ConfigUtil.getConfig().getBoolean("debug")) {
            return;
        }

        log(DEBUG_PREFIX + message, args);
    }
}
