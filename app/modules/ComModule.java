package modules;

import com.google.inject.AbstractModule;
import play.Configuration;
import play.Environment;
import redis.clients.jedis.Jedis;
import util.LogUtil;
import util.RedisPool;

/**
 * 配置模块,方便注入
 * Created by howen on 16/2/19.
 */
public class ComModule extends AbstractModule {

    private final Environment environment;
    private final Configuration configuration;

    public ComModule(
            Environment environment,
            Configuration configuration) {
        this.environment = environment;
        this.configuration = configuration;
    }

    protected void configure() {
        bind(SysParCom.class).asEagerSingleton();
        bind(LogUtil.class).asEagerSingleton();
        bind(RedisPool.class).asEagerSingleton();
        bind(Jedis.class).toInstance(RedisPool.create());
    }
}
