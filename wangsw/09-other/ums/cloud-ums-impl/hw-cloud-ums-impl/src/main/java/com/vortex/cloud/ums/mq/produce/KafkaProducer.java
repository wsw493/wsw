package com.vortex.cloud.ums.mq.produce;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vortex.cloud.common.kafka.IProducer;
import com.vortex.cloud.common.kafka.msg.KafkaMsg;
import com.vortex.cloud.common.kafka.producer.SimpleProcuder;
import com.vortex.cloud.common.kafka.producer.SimpleProducerConfig;

/**
 * 生产者
 * @author ll
 *
 */
@Component
public class KafkaProducer {
	
	private static IProducer producer = null;
	
	private static KafkaProducer instance = null;
	
	private KafkaProducer() {
		
	}
	
	public static String kafkaAddress;
	
	@Value("${server.kafka.address}")
	public void setName(String kafkaAddress) {
		KafkaProducer.kafkaAddress = kafkaAddress;
	}
	/**
	 * 
	 * @return
	 */
	public static synchronized KafkaProducer getInstance() {
		if (instance == null) {
			instance = new KafkaProducer();
			if (producer == null) {
				producer = new SimpleProcuder(new SimpleProducerConfig(kafkaAddress, "com.vortex.cloud.ums.mq.produce.KafkaProducer"));
				try {
					producer.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return instance;
	}
	
	public void produce(String topic, String key, Object bean) throws Exception {
		KafkaMsg msg = KafkaMsg.buildMsg(topic, key, bean);
		producer.send(msg);
	}
	
	public static void stop() {
		if (producer != null) {
			try {
				producer.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
