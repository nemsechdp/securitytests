package com.nem.auradiagnostic.kafka.configs;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.nem.auradiagnostic.utils.ConfigProperties;

public class KafkaConsumerConfig 
{
	public static final Boolean DefaultAutoCommit = true;
	public static final Integer DefaultKafkaNumMss = 150;
	public static final Integer DefaultAutoCommitInterval = 1000;
	public static final Integer DefaultZookeeperTimeout = 5000;
	public static final Long DefaultMaxMessageSize = 3000L;
	public static final String DefaultAutoOffsetReset = "earliest";
	public static final String DefaultValueDeserializer = "org.apache.kafka.common.serialization.ByteArrayDeserializer";
	public static final String DefaultKeyDeserializer = "org.apache.kafka.common.serialization.ByteArrayDeserializer";
	public static final Integer DefaultMaxPollRecords = 1; 	
	public static final Integer DefaultPollTime = 2000; 
	//
	public HashMap<String, KafkaConsumerParams> consumers;

	public class KafkaConsumerParams
	{
		// Kafka
		public String BootstrapServers;
		public String Topic;
		public String ConsumerGroup;
		public Boolean AutoCommit;
		public Integer AutoCommitInterval;
		public Long KafkaConsumerMaxLogSize;
		public Integer ZookeeperTimeout;
		public String AutoOffsetReset;
		public Integer MaxPollRecords;
		public String ValueDeserializer;
		public String KeyDeserializer;
		public Integer PollTime;
		public Integer NumMss;

		// Autenticación
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

	public KafkaConsumerConfig(String propertiesFile, List<String> consumerList) throws IOException
	{		
		ConfigProperties properties = new ConfigProperties();
		properties.load(new FileInputStream(propertiesFile));
		Init(properties, consumerList);
	}

	public KafkaConsumerConfig (ConfigProperties properties, List<String> consumerList) throws IOException
	{
		Init(properties, consumerList);
	}	

	private void Init(ConfigProperties properties, List<String> consumerList) throws IOException 
	{
		consumers = new HashMap<String, KafkaConsumerParams>();

		if (consumerList != null && consumerList.size() > 0)
		{
			for (String consumer : consumerList)
			{				
				if (Boolean.parseBoolean(properties.getProperty(properties.GetKey(consumer,"ConsumeFromKafka"), "true")))
					consumers.put(consumer, readParams(properties, consumer));
			}		
		}	
		else // Si la lista está vacía/nula, cargamos el consumer por defecto.
		{				
			consumers.put("", readParams(properties, ""));
		}
	}

	private KafkaConsumerParams readParams(ConfigProperties properties, String consumerKey) throws IOException 
	{
		KafkaConsumerParams conParams = new KafkaConsumerParams();

		conParams.Topic = properties.ReadMandatoryString
				(consumerKey,"KafkaConsumerTopic");
		conParams.ConsumerGroup = properties.ReadMandatoryString
				(consumerKey,"KafkaConsumerGroup");

		conParams.SecurityProtocol = properties.ReadMandatoryString
				(consumerKey,"SecurityProtocol");
		conParams.SaslMechanism = properties.ReadMandatoryString
				(consumerKey,"SaslMechanism");
		conParams.KerberosService = properties.ReadMandatoryString
				(consumerKey,"KerberosService");
		conParams.Krb5Conf = properties.ReadMandatoryString
				(consumerKey,"Krb5Conf");
		conParams.KeytabFile = properties.ReadMandatoryString
				(consumerKey,"KeytabFile");
		conParams.Principal = properties.ReadMandatoryString
				(consumerKey,"Principal");
		conParams.JaasPath = properties.ReadMandatoryString
				(consumerKey,"JaasPath");
		
		conParams.NumMss =Integer.parseInt(properties.getProperty
				((properties.GetKey(consumerKey,"NumMss")), DefaultKafkaNumMss.toString()));
		conParams.KafkaConsumerMaxLogSize = Long.parseLong(properties.getProperty
				(properties.GetKey(consumerKey,"KakfaConsumerMaxLogSize"), DefaultMaxMessageSize.toString()));
		conParams.ZookeeperTimeout = Integer.parseInt(properties.getProperty
				(properties.GetKey(consumerKey,"KafkaConsumerZookeeperTimeout"), DefaultZookeeperTimeout.toString()));						
		conParams.AutoCommit = Boolean.parseBoolean(properties.getProperty
				(properties.GetKey(consumerKey,"KafkaConsumerAutoCommit"), DefaultAutoCommit.toString()));
		conParams.AutoCommitInterval = Integer.parseInt(properties.getProperty
				(properties.GetKey(consumerKey,"KafkaConsumerAutoCommitInterval"), DefaultAutoCommitInterval.toString()));	
		conParams.AutoOffsetReset = properties.getProperty
				(properties.GetKey(consumerKey,"KafkaConsumerAutoOffsetReset"), DefaultAutoOffsetReset);
		conParams.PollTime =  Integer.parseInt(properties.getProperty
				(properties.GetKey(consumerKey,"KafkaConsumerPollTime"), DefaultPollTime.toString()));	
		conParams.BootstrapServers = properties.ReadMandatoryString
				(properties.GetKey(consumerKey,"KafkaConsumerBootstrapServers"));
		conParams.MaxPollRecords = Integer.parseInt(properties.getProperty
				(properties.GetKey(consumerKey,"KafkaConsumerMaxPollRecords"), DefaultMaxPollRecords.toString()));	
		conParams.ValueDeserializer = properties.getProperty
				(properties.GetKey(consumerKey,"KafkaConsumerValueDeserializer"), DefaultValueDeserializer);
		conParams.KeyDeserializer = properties.getProperty
				(properties.GetKey(consumerKey,"KafkaConsumerKeyDeserializer"), DefaultKeyDeserializer);

		return conParams;
	}

	/**
	 * Función encargada de devolver las properties del consumidor pasado como parámetro
	 */
	private Properties getProperties(KafkaConsumerParams cp)
	{
		Properties props = new Properties();
		if (cp != null)
		{
			props.put("bootstrap.servers", cp.BootstrapServers);
			props.put("group.id", cp.ConsumerGroup);
			props.put("session.timeout.ms", Integer.toString(cp.ZookeeperTimeout)); 
			props.put("auto.offset.reset", cp.AutoOffsetReset); 
			props.put("enable.auto.commit", cp.AutoCommit==true?"true":"false"); 
			props.put("auto.commit.interval.ms", Integer.toString(cp.AutoCommitInterval)); 
			props.put("fetch.message.max.bytes", Long.toString(cp.KafkaConsumerMaxLogSize));
			props.put("max.partition.fetch.bytes", Long.toString(cp.KafkaConsumerMaxLogSize));
			props.put("max.poll.records", Integer.toString(cp.MaxPollRecords));
			props.put("value.deserializer", cp.ValueDeserializer);
			props.put("key.deserializer", cp.KeyDeserializer);
			props.put("security.protocol",cp.SecurityProtocol);
			props.put("sasl.mechanism", cp.SaslMechanism);
			props.put("sasl.kerberos.service.name", cp.KerberosService);
		}
		return props;
	}
	
	/**
 * 	 * @param consumerId Identificador del consumidor.
	 * @return KafkaConsumerConfig.ConsumerParams
	 */
	public KafkaConsumerParams getConsumerParams(String consumerId)
	{
		KafkaConsumerParams theClass = consumers.get(consumerId);
		return theClass != null ? theClass : null;
	}

	public KafkaConsumerParams getDefaultConsumerParams()
	{
		KafkaConsumerParams theClass = consumers.get("");
		return theClass != null ? theClass : null;
	}

	public void setDefaultConsumerParams (KafkaConsumerParams defaultParams)
	{
		consumers.put("", defaultParams);	
	}


}
