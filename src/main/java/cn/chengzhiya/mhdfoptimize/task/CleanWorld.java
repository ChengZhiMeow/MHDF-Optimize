package cn.chengzhiya.mhdfoptimize.task;

import cn.chengzhiya.mhdfoptimize.util.clean.CleanUtil;
import cn.chengzhiya.mhdfoptimize.util.config.ConfigUtil;
import cn.chengzhiya.mhdfoptimize.util.runnable.MHDFRunnable;

public final class CleanWorld extends MHDFRunnable {
    private int i = 0;

    @Override
    public void run() {
        if (!ConfigUtil.getConfig().getBoolean("world-clean.enable")) {
            return;
        }

        int delay = ConfigUtil.getConfig().getInt("world-clean.delay");
        if (i < delay) {
            String type = ConfigUtil.getConfig().getString("world-clean.message-type");
            if (type == null) {
                return;
            }

            int time = delay - i;
            int showTime = ConfigUtil.getConfig().getInt("world-clean.bossbar.show-time");
            if (type.equals("bossbar")) {
                String message = ConfigUtil.getConfig().getString("world-clean.bossbar.format");
                if (message == null) {
                    return;
                }
                message = message.replace("{time}", String.valueOf(time));

                CleanUtil.sendMessage(type, message, 20);
            } else {
                CleanUtil.sendMessage(type, ConfigUtil.getConfig().getString("world-clean.message." + time), showTime);
            }

            CleanUtil.playSound(ConfigUtil.getConfig().getString("world-clean.sound." + time));

            i++;
            return;
        }

        CleanUtil.clean();

        i = 0;
    }
}
