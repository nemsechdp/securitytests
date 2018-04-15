package com.nem.auradiagnostic.kafka.kerberos;

import com.nem.auradiagnostic.authentication.kerberos.KerberosAuthenticatedEntity;
import com.nem.auradiagnostic.kafka.KafkaByteArrayProducerCore;
import com.nem.auradiagnostic.kafka.configs.KafkaProducerConfig;
import com.nem.auradiagnostic.kafka.configs.KafkaProducerConfig.KafkaProducerParams;


public class KafkaProducerKerberos extends KerberosAuthenticatedEntity
{	
	KafkaByteArrayProducerCore producer;
	KafkaProducerConfig producerConf;

	public KafkaProducerKerberos(KafkaProducerParams kafkaProducerParams) throws Exception
	{
		super(kafkaProducerParams.Krb5Conf, kafkaProducerParams.Principal, kafkaProducerParams.JaasPath, kafkaProducerParams.KeytabFile);
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
