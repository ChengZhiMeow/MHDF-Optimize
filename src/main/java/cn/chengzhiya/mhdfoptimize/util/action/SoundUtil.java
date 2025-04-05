package cn.chengzhiya.mhdfoptimize.util.action;

import cn.chengzhiya.mhdfoptimize.util.reflection.ReflectionUtil;
import cn.chengzhiya.mhdfoptimize.util.version.VersionUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;

import java.util.Locale;

public final class SoundUtil {
    /**
     * 获取指定音效key的音效实例
     *
     * @param key 音效key
     * @return 音效实例
     */
    public static Sound getSound(String key) {
        if (VersionUtil.is1_21orAbove()) {
            return Registry.SOUNDS.get(NamespacedKey.minecraft(key
                    .replace("_", ".")
                    .toLowerCase(Locale.ROOT))
            );
        }

        return ReflectionUtil.invokeMethod(
                ReflectionUtil.getMethod(Sound.class, "valueOf", true, String.class),
                Sound.class
        );
    }
}
