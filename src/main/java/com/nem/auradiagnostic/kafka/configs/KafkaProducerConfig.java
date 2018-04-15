package com.nem.auradiagnostic.kafka.configs;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.nem.auradiagnostic.utils.ConfigProperties;
import com.nem.auradiagnostic.utils.StringUtils;


public class KafkaProducerConfig {

	//	default
	public static final Integer DefaultKafkaProducerMaxRetries = 2;
	public static final Integer DefaultKafkaNumMss = 5000;
	public static final Integer DefaultKafkaProducerMaxMessageSize = 5242880;
	public static final String DefaultKafkaProducerCompressionType = "none";
	public static final Integer DefaultKafkaProducerRequiredACKs = 0;
	public static final Integer DefaultKafkaProducerConnectionTimeout = 30000;
	public static final String DefaultKeySerializer = "org.apache.kafka.common.serialization.ByteArraySerializer";
	public static final String DefaultValueSerializer = "org.apache.kafka.common.serialization.ByteArraySerializer";
	public static final String DefaultProducerType = "sync";	
	private static final String DefaultKafkaMss = "aupa athletic";

	// 
	public HashMap<String, KafkaProducerParams> producers;


	/** Clase interna ProducerParams, encargada de almacenar los parámetros del productor.
	 *  Incluye la función getProps() ->> Devuelve los parámetros en forma de Properties.
	 */
	public class KafkaProducerParams
	{		
		// Kafka
		public String BootstrapServers;	
		public String ProducerType;
		public Integer MaxRetries;
		public Integer RequiredACKs;
		public Integer ConectionTimeout;
		public String KeySerializer;
		public String ValueSerializer;
		public String Topic;
		public String PartitionerClass;
		public Long MaxMessageSize;
		public String CompressionType;
		public Integer NumMss;
		public String Mss;

		// Seguridad
		public String SecurityProtocol;
		public String SaslMechanism;
		public String KerberosService;
		public String Krb5Conf;
		public String KeytabFile;
		public String Principal;
		public String JaasPath;

		public Properties getProps()
		{
			return getProperties(this);
		}
	}


	public KafkaProducerConfig (String propertiesFile, List<String> producerList) throws IOException
	{
		ConfigProperties properties = new ConfigProperties();
		properties.load(new FileInputStream(propertiesFile));

		Init(properties, producerList);
	}

	public KafkaProducerConfig (ConfigProperties properties, List<String> producerList) throws IOException
	{
		Init(properties, producerList);
	}

	private void Init(ConfigProperties properties, List<String> producerList) throws IOException 
	{
		producers = new HashMap<String, KafkaProducerParams>();

		if (producerList != null && producerList.size() > 0)
		{
			for (String producer : producerList)
			{			
				if (Boolean.parseBoolean(properties.getProperty(properties.GetKey(producer,"ProduceToKafka"), "true")))
					producers.put(producer, readParams(properties, producer));				
			}
		}		
		else 
		{				
			producers.put("", readParams(properties, ""));
		}

	}

	private KafkaProducerParams readParams(ConfigProperties properties, String producerKey) throws IOException
	{
		KafkaProducerParams proParams = new KafkaProducerParams();

		proParams.BootstrapServers = properties.ReadMandatoryString
				(properties.GetKey(producerKey,"KafkaProducerBootstrapServers"));
		proParams.SecurityProtocol = properties.ReadMandatoryString
				(producerKey,"SecurityProtocol");
		proParams.SaslMechanism = properties.ReadMandatoryString
				(producerKey,"SaslMechanism");
		proParams.KerberosService = properties.ReadMandatoryString
				(producerKey,"KerberosService");
		proParams.Krb5Conf = properties.ReadMandatoryString
				(producerKey,"Krb5Conf");
		proParams.KeytabFile = properties.ReadMandatoryString
				(producerKey,"KeytabFile");
		proParams.Principal = properties.ReadMandatoryString
				(producerKey,"Principal");
		proParams.JaasPath = properties.ReadMandatoryString
				(producerKey,"JaasPath");
		proParams.Mss =properties.getProperty
				(properties.GetKey(producerKey,"Mss"), DefaultKafkaMss);

		proParams.NumMss =Integer.parseInt(properties.getProperty
				((properties.GetKey(producerKey,"NumMss")), DefaultKafkaNumMss.toString()));
		proParams.MaxRetries = Integer.parseInt(properties.getProperty
				(properties.GetKey(producerKey,"KafkaProducerMaxRetries"), DefaultKafkaProducerMaxRetries.toString()));	
		proParams.RequiredACKs = Integer.parseInt(properties.getProperty
				(properties.GetKey(producerKey,"KafkaProducerRequiredACKs"), DefaultKafkaProducerRequiredACKs.toString()));	
		proParams.ConectionTimeout = Integer.parseInt(properties.getProperty
				(properties.GetKey(producerKey,"KafkaProducerConectionTimeout"), DefaultKafkaProducerConnectionTimeout.toString()));	
		proParams.KeySerializer = properties.getProperty
				(properties.GetKey(producerKey,"KafkaProducerKeySerializer"), DefaultKeySerializer);
		proParams.ValueSerializer = properties.getProperty
				(properties.GetKey(producerKey,"KafkaProducerValueSerializer"), DefaultValueSerializer);
		proParams.ProducerType = properties.getProperty
				(properties.GetKey(producerKey,"KafkaProducerProducerType"), DefaultProducerType);
		proParams.Topic = properties.ReadMandatoryString
				(properties.GetKey(producerKey,"KafkaProducerTopic"));	
		proParams.PartitionerClass = properties.getProperty
				(properties.GetKey(producerKey,"KafkaProducerPartitionerClass"));
		proParams.MaxMessageSize = Long.parseLong(properties.getProperty
				(properties.GetKey(producerKey,"KakfaProducerMaxLogSize"), DefaultKafkaProducerMaxMessageSize.toString()));
		proParams.CompressionType = properties.getProperty(
				properties.GetKey(producerKey,"KakfaProducerCompressionType"), DefaultKafkaProducerCompressionType);


		return proParams;		
	}

	/**
	 * Función encargada de devolver las properties del productor pasado como parámetro
	 */
	private Properties getProperties(KafkaProducerParams producer)
	{
		Properties props = new Properties();

		props.put("bootstrap.servers", producer.BootstrapServers);
		props.put("request.timeout.ms", Integer.toString(producer.ConectionTimeout));
		props.put("retries", Integer.toString(producer.MaxRetries));
		props.put("acks", Integer.toString(producer.RequiredACKs));
		props.put("key.serializer", producer.KeySerializer);
		props.put("value.serializer", producer.ValueSerializer);
		props.put("max.request.size", Long.toString(producer.MaxMessageSize));
		props.put("compression.type	", producer.CompressionType);
		props.put("security.protocol", producer.SecurityProtocol);
		if (!StringUtils.IsNullOrEmptyOrWhiteSpace(producer.PartitionerClass))
			props.put("partitioner.class", producer.PartitionerClass);
		props.put("producer.type", producer.ProducerType);
		props.put("max.in.flight.requests.per.connection", 200);
		props.put("sasl.mechanism", producer.SaslMechanism);
		props.put("sasl.kerberos.service.name", producer.KerberosService);


		return props;
	}

	public KafkaProducerParams getProducerParams(String producerId)
	{
		KafkaProducerParams theClass = producers.get(producerId);
		return theClass != null ? theClass : null;
	}

}
