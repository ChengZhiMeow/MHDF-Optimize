package cn.chengzhiya.mhdfoptimize.manager;

import cn.chengzhiya.mhdfoptimize.hook.CraftEngineHook;
import cn.chengzhiya.mhdfoptimize.hook.MythicMobsHook;
import cn.chengzhiya.mhdfoptimize.hook.PlaceholderApiHook;
import cn.chengzhiya.mhdfoptimize.interfaces.Hook;
import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public final class PluginHookManager implements Hook {
    private final PlaceholderApiHook placeholderAPIHook = new PlaceholderApiHook();
    private final CraftEngineHook craftEngineHook = new CraftEngineHook();
    private final MythicMobsHook mythicMobsHook = new MythicMobsHook();

    /**
     * 初始化所有对接的API
     */
    @Override
    public void hook() {
        placeholderAPIHook.hook();
        craftEngineHook.hook();
        mythicMobsHook.hook();
    }

    /**
     * 卸载所有对接的API
     */
    @Override
    public void unhook() {
        placeholderAPIHook.unhook();
        craftEngineHook.unhook();
        mythicMobsHook.unhook();
    }
}
