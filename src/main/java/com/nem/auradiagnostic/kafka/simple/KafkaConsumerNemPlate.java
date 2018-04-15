package com.nem.auradiagnostic.kafka.simple;

import com.nem.auradiagnostic.authentication.simple.SimpleAuthenticatedEntity;
import com.nem.auradiagnostic.kafka.KafkaConsumerCore;
import com.nem.auradiagnostic.kafka.KafkaReader;
import com.nem.auradiagnostic.kafka.configs.KafkaConsumerConfig.KafkaConsumerParams;

/**
 * Kafka Consumer (CORE) basado en PLAIN -> NemPlateLoginModule.
 *
 */
public class KafkaConsumerNemPlate extends SimpleAuthenticatedEntity
{	
	KafkaConsumerCore kc;

	public KafkaConsumerNemPlate(KafkaConsumerParams kafkaConsumerParams) throws Exception
	{
		super(kafkaConsumerParams.JaasPath);
		kc = new KafkaConsumerCore(kafkaConsumerParams,1);
	}		

	public KafkaReader getKafkaReader (int n)
	{
		return kc.getReader(n);
	}	

	public void shutdown() throws Exception 
	{
		logout();

		if (kc!=null)
			kc.shutdown();
	}
}
