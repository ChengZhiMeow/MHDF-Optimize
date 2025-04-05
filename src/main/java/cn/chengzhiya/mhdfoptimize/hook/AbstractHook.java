package cn.chengzhiya.mhdfoptimize.hook;

import cn.chengzhiya.mhdfoptimize.interfaces.Hook;
import lombok.Getter;

@Getter
public abstract class AbstractHook implements Hook {
    public boolean enable = false;
}
