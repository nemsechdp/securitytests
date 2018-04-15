package com.nem.auradiagnostic.kafka.kerberos;

import com.nem.auradiagnostic.authentication.kerberos.KerberosAuthenticatedEntity;
import com.nem.auradiagnostic.kafka.KafkaConsumerCore;
import com.nem.auradiagnostic.kafka.KafkaReader;
import com.nem.auradiagnostic.kafka.configs.KafkaConsumerConfig.KafkaConsumerParams;

/**
 * Kafka Consumer (CORE) basado en Kerberos.
 *
 */
public class KafkaConsumerKerberos extends KerberosAuthenticatedEntity
{	
	KafkaConsumerCore kc;
	
	public KafkaConsumerKerberos(KafkaConsumerParams kafkaConsumerParams) throws Exception
	{
		super(kafkaConsumerParams.Krb5Conf, kafkaConsumerParams.Principal, kafkaConsumerParams.JaasPath, kafkaConsumerParams.KeytabFile);
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
