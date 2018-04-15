package com.nem.auradiagnostic.kafka.simple;

import com.nem.auradiagnostic.authentication.simple.SimpleAuthenticatedEntity;
import com.nem.auradiagnostic.kafka.KafkaByteArrayProducerCore;
import com.nem.auradiagnostic.kafka.configs.KafkaProducerConfig;
import com.nem.auradiagnostic.kafka.configs.KafkaProducerConfig.KafkaProducerParams;


public class KafkaProducerNemPlate extends SimpleAuthenticatedEntity
{	
	KafkaByteArrayProducerCore producer;
	KafkaProducerConfig producerConf;

	public KafkaProducerNemPlate(KafkaProducerParams kafkaProducerParams) throws Exception
	{
		super(kafkaProducerParams.JaasPath);
		producer = new KafkaByteArrayProducerCore(kafkaProducerParams);
	}	
	
	
	public KafkaByteArrayProducerCore getProducer()
	{
		return producer;
	}

	public void finish() throws Exception 
	{
		logout();

		if (producer!=null)
			producer.Close();
	}


}
