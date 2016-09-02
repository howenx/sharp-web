package modules;

import akka.actor.ActorRef;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import util.ConcurrentRedisListener;
import util.RedisPool;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * 订阅
 * Created by howen on 16/6/22.
 */
@Singleton
public class ExecSubscribe {

    @Inject
    @Named("webRunActor")
    private ActorRef webRunActor;

    public ExecSubscribe( ) {
        try (Jedis jedis = RedisPool.createPool().getResource()) {
            JedisPubSub listener = new ConcurrentRedisListener(webRunActor);
            jedis.psubscribe(listener, "style-web-version");
        }
    }
}
