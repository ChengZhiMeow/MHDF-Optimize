package cn.chengzhiya.mhdfoptimize.commands;

import cn.chengzhiya.mhdfoptimize.Main;
import cn.chengzhiya.mhdfoptimize.util.clean.CleanUtil;
import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.config.LangUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class MHDFOptimize implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "about" -> {
                    sender.sendMessage(LangUtil.i18n("commands.mhdfoptimize.sub-commands.about.message")
                            .replace("{version}", Main.instance.getDescription().getVersion())
                    );
                    return false;
                }
                case "clean" -> {
                    CleanUtil.clean();
                    return false;
                }
                case "reload" -> {
                    ConfigUtil.reloadConfig();
                    LangUtil.reloadLang();
                    Main.instance.getLangManager().reloadLang();

                    sender.sendMessage(LangUtil.i18n("commands.mhdfoptimize.sub-commands.reload.message"));
                    return false;
                }
            }
        }
        {
            sender.sendMessage(
                    LangUtil.i18n("commands.mhdfoptimize.sub-commands.help.message")
                            .replace("{helpList}", LangUtil.getHelpList("mhdfoptimize"))
                            .replace("{command}", label)
            );
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(LangUtil.getKeys("commands.mhdfoptimize.sub-commands"));
        }

        return new ArrayList<>();
    }
}
