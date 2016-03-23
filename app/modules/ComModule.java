package modules;

import com.google.inject.AbstractModule;

/**
 * 配置模块,方便注入
 * Created by howen on 16/2/19.
 */
public class ComModule extends AbstractModule {

    protected void configure() {
        bind(SysParCom.class).asEagerSingleton();
    }
}
