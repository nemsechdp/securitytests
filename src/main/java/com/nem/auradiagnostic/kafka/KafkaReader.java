package com.nem.auradiagnostic.kafka;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

import com.nem.auradiagnostic.kafka.configs.KafkaConsumerConfig.KafkaConsumerParams;
/** 
 * los obreros de lectura de Kafka.
 *
 */
public class KafkaReader
{
	private KafkaConsumerParams readerParams;
	public int lastPartition;
	public long lastOffset;
	public KafkaConsumerCore fatherConsumer;
    private KafkaConsumer<byte[], byte[]> consumer;
	private Iterator<ConsumerRecord<byte[], byte[]>> it;
	public Long lastTimestamp;

	protected final static Logger LOG =  Logger.getLogger(KafkaReader.class.getName());
  
   
	/** Constructor del KafkaReader. Encargado de implementar las funciones GetMsg(int) y GetNextKafkaMsg(), básicas para consumir de Kafka.
     * 
     * @param kafkaConsumer Consumidor padre
     * @param cParams	Parámetros del consumer.
     **/
    public KafkaReader(KafkaConsumerCore kafkaConsumer, KafkaConsumerParams cParams) 
    {
    	readerParams = cParams;
    	fatherConsumer = kafkaConsumer;
    	consumer = new KafkaConsumer<byte[],byte[]> (cParams.getProps());
    	consumer.subscribe(Arrays.asList(readerParams.Topic));
     }
	
    // función BASE.
    private List<KafkaMsg> TryGetKafkaMsgs(int numMsgs) 
    {    	
    	int countMsgs = 0;
    	List<KafkaMsg> msgList = new ArrayList<KafkaMsg>();
    	
    	while (countMsgs < numMsgs)
    	{
        	if ( (it != null) && (it.hasNext()) )
    		{
        		ConsumerRecord<byte[],byte[]> record = it.next();
        		msgList.add(new KafkaMsg(record.key(), record.value()));
        		lastPartition = record.partition();
    			countMsgs++;    			
        	}
        	else 
        	{
        		it = consumer.poll(readerParams.PollTime).iterator();        		
        		if (!it.hasNext())
        	       break;
        	}
   		}
    	return msgList;   	    	     
    }    
     
    /** Devuelve un único mensaje.
     * @return mensaje kafka.
     */
    public KafkaMsg GetOneKafkaMsg() 
    {
    	List<KafkaMsg> justOne = this.TryGetKafkaMsgs(1);
    	return ( ((justOne == null) || (justOne.isEmpty()) )? null : justOne.get(0));
	}
    
    /** Devuelve una lista de mensajes.
     *  @return Lista de mensajes kafka. Devuelve NULL si no ha leído ningún mensaje.
     */
    public List<KafkaMsg> GetMultipleKafkaMsgs(int nMessagesToRead) 
    {
    	List<KafkaMsg> multipleMsgs = this.TryGetKafkaMsgs(nMessagesToRead);
    	return ( ((multipleMsgs == null) || (multipleMsgs.isEmpty()) )? null : multipleMsgs );
	}
    
    public void shutdown()
    {
    	this.consumer.close();
    }
    
}
