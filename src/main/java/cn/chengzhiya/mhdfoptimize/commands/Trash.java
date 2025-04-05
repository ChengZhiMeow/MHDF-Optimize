package cn.chengzhiya.mhdfoptimize.commands;

import cn.chengzhiya.mhdfoptimize.menu.TrashMenu;
import cn.chengzhiya.mhdfoptimize.util.config.LangUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class Trash implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(LangUtil.i18n("only-player"));
            return false;
        }

        new TrashMenu(player, 1).openMenu();
        sender.sendMessage(LangUtil.i18n("commands.trash.message"));
        return false;
    }
}
