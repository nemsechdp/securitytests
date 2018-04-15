package com.nem.auradiagnostic.kafka;
import java.util.ArrayList;
import java.util.List;

import com.nem.auradiagnostic.kafka.configs.KafkaConsumerConfig.KafkaConsumerParams;

/**
 * Esta clase no lee de Kafka, sino que genera los lectores y controla sus hilos.
 * @see KafkaReader
 */

public class KafkaConsumerCore
{
	public List<KafkaReader> kafkaReaders = new ArrayList<KafkaReader>();
    public KafkaConsumerParams consumerParams;   
    
    /**
     * Crea un consumer de Kafka. La gestión de los offset la hará el broker.
     * @param consumerParams 
     * @param nReaders Número de hilos KafkaReader que voy a apadrinar.
     * @throws Exception
     */
    public KafkaConsumerCore(KafkaConsumerParams consumerParams, int nReaders) throws Exception 
    {       
    	this.consumerParams = consumerParams;
		createReaders(nReaders);
    } 
     
	public void shutdown() 
    {       
        for (int i = 0; i < kafkaReaders.size(); i++)
        	kafkaReaders.get(i).shutdown();        
    }
 

	private void createReaders(int numReaders) throws Exception   
    {
	   	if (kafkaReaders.size()>0)
	   		throw new Exception("Readers ya creados, no puedo dejarlos sueltos.");
	   	
	   	
    	if (numReaders > 0)
    	{
    		kafkaReaders = new ArrayList<KafkaReader>();
        	
        	for (int i=0; i<numReaders; i++)
        		kafkaReaders.add(new KafkaReader(this, consumerParams));
    	}
    }
    
    /** Devuelve a mi hijo n.
     * @return KafkaReader
     * @throws Exception si nos vamos de rango.
     */
    public KafkaReader getReader(int n)
    {
    	try 
    	{
    		return kafkaReaders.get(n);
    	}
    	catch(Exception e)
    	{
			e.printStackTrace();
			return null;
    	}
    }
 
}
