package cn.chengzhiya.mhdfoptimize.util.reflection;

import cn.chengzhiya.mhdfoptimize.util.version.VersionUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class EntityUtil {
    public static EntityType DROPPED_ITEM;

    static {
        try {
            DROPPED_ITEM = ReflectionUtil.getFieldValue(
                    ReflectionUtil.getField(EntityType.class, "ITEM", true),
                    EntityType.class
            );
        } catch (Exception e) {
            DROPPED_ITEM = ReflectionUtil.getFieldValue(
                    ReflectionUtil.getField(EntityType.class, "DROPPED_ITEM", true),
                    EntityType.class
            );
        }
    }

    /**
     * 调整指定实体的AI
     *
     * @param entity 实体实例
     */
    public static void controlEntityAI(LivingEntity entity, boolean enable) {
        if (VersionUtil.is1_9orAbove()) {
            entity.setAI(enable);
        } else {
            setNoAIWithReflection(entity, enable);
        }
    }

    /**
     * 使用反射设置 NoAI 属性，适用于 1.8
     *
     * @param entity 要禁用 AI 的实体
     */
    private static void setNoAIWithReflection(LivingEntity entity, boolean enable) {
        try {
            Object craftEntity = ReflectionUtil.invokeMethod(
                    ReflectionUtil.getMethod(
                            entity.getClass(),
                            "getHandle",
                            true
                    ),
                    entity
            );

            if (craftEntity == null) {
                return;
            }

            Class<?> nbtTagCompoundClass = Class.forName("net.minecraft.server." + VersionUtil.getServerVersion() + ".NBTTagCompound");
            Object nbtTag = ReflectionUtil.newClass(nbtTagCompoundClass);

            ReflectionUtil.invokeMethod(
                    ReflectionUtil.getMethod(
                            craftEntity.getClass(),
                            "c",
                            true
                    ),
                    craftEntity,
                    nbtTag
            );

            ReflectionUtil.invokeMethod(
                    ReflectionUtil.getMethod(
                            nbtTagCompoundClass,
                            "setByte",
                            true,
                            String.class,
                            byte.class
                    ),
                    nbtTag,
                    "NoAI",
                    enable ? (byte) 0 : 1
            );

            ReflectionUtil.invokeMethod(
                    ReflectionUtil.getMethod(
                            craftEntity.getClass(),
                            "f",
                            true,
                            nbtTagCompoundClass
                    ),
                    craftEntity,
                    nbtTag
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
