package actor;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.databind.SerializationFeature;
import play.Logger;
import play.libs.Json;
import redis.clients.jedis.Jedis;

import javax.inject.Inject;

import static modules.SysParCom.REDIS_CHANNEL;

/**
 * 用于produce消息到阿里云mns的Actor
 * Created by howen on 15/12/24.
 */
public class MnsActor extends AbstractActor {

    @Inject
    public MnsActor(Jedis jedis) {
        receive(ReceiveBuilder.match(Object.class, event -> {

            if (event instanceof ILoggingEvent) {
                ((ILoggingEvent) event).getMDCPropertyMap().put("projectId", "style-web");
//                System.out.println("日志Json格式---->" + Json.toJson(event).toString());
//                String content=Json.toJson(event).toString();
//                if(null!=content&&!"".equals(content)){
                    try {
                     //   jedis.publish(REDIS_CHANNEL,content);
                        jedis.publish(REDIS_CHANNEL,Json.mapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).valueToTree(event).toString());
                    }catch(Exception e){
                        Logger.error("用于produce消息到阿里云mns异常"+e.getMessage());
                    }
//                }else{
//                    Logger.error("用于produce消息到阿里云mns内容为空"+content);
//                }

            }
        }).matchAny(s -> {
            Logger.error("MnsActor received messages not matched: {}", s.toString());
            unhandled(s);
        }).build());
    }
}
