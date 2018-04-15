package com.nem.auradiagnostic.kafka;
import java.io.IOException;
import java.util.Arrays;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.nem.auradiagnostic.kafka.configs.KafkaProducerConfig;
import com.nem.auradiagnostic.kafka.configs.KafkaProducerConfig.KafkaProducerParams;

public class KafkaByteArrayProducerCore
{
	private final KafkaProducer<byte[], byte[]> producer;
    private String topic;
	public String configKey;
    
	/** Constructor del productor. 
	 * @param kpc Clase de config del producer.
	 * @param configKey identificativo del producer.
	 */
	public KafkaByteArrayProducerCore (KafkaProducerParams producerParams)
	{
		this.configKey = "";
    	producer = new KafkaProducer<byte[], byte[]>(producerParams.getProps());
    	topic = producerParams.Topic;
	}
	
    /** Constructor del productor.
     * @param configFile 
     * @param configKey Identificativo del producer.
     * @throws IOException Si no consigue cargar el fichero, la que habrás liao.
     */
    public KafkaByteArrayProducerCore(String configFile, String configKey) throws IOException
    {
    	this.configKey = configKey;
    	KafkaProducerConfig kProducerConfig = new KafkaProducerConfig(configFile, Arrays.asList(configKey));
    	KafkaProducerParams producerParams = kProducerConfig.producers.get(configKey);
    	this.topic = producerParams.Topic;
      
    	producer = new KafkaProducer<byte[], byte[]>(producerParams.getProps()); 
    }    

    public void SendKafkaMsg(byte[] message)
    {    	
    	producer.send(new ProducerRecord<byte[], byte[]>(this.topic, message));
    }        
  
    public void SendKafkaMsg(String topic, byte[] message)
    {    	
    	producer.send(new ProducerRecord<byte[], byte[]>(topic, message));
    }
  
    public void SendKafkaMsg(String topic, byte[] partitionKey, byte[] message)
    {
        producer.send(new ProducerRecord<byte[], byte[]>(topic, partitionKey, message));        
    }
    
    /**
     * a dormir.
     */
    public void Close()
    {
    	if (producer != null)
    		producer.close();
    }
}
