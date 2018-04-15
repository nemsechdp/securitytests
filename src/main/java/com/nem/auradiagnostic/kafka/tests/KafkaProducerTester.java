package com.nem.auradiagnostic.kafka.tests;

import java.util.Arrays;

import com.nem.auradiagnostic.kafka.KafkaByteArrayProducerCore;
import com.nem.auradiagnostic.kafka.configs.KafkaProducerConfig;
import com.nem.auradiagnostic.kafka.configs.KafkaProducerConfig.KafkaProducerParams;
import com.nem.auradiagnostic.kafka.kerberos.KafkaProducerKerberos;
import com.nem.auradiagnostic.kafka.simple.KafkaProducerNemPlate;

public class KafkaProducerTester 
{
	static KafkaByteArrayProducerCore producer;
	static KafkaProducerKerberos kpp;
	static KafkaProducerNemPlate kss;
	
	public static void main(String args[]) throws Exception 
	{		
		
		if (args.length<3)
			throw new Exception("Args: [0]Producer properties;[1]ProducerEntry;[2]authMode");
		try 
		{	
		
			KafkaProducerConfig producerConf = new KafkaProducerConfig(args[0],Arrays.asList(args[1]));
			KafkaProducerParams params = producerConf.getProducerParams(args[1]);
			String authMode = args[2];	
			
			switch (authMode.toLowerCase())
			{
				case "kerberos": 			KafkaProducerKerberos kpp = new KafkaProducerKerberos(params);
											producer = kpp.getProducer(); break;
				
				case "nemplate": 			KafkaProducerNemPlate kss = new KafkaProducerNemPlate(params);
											producer = kss.getProducer(); break;
										
				default: 					System.out.println("Opción no reconodida" + authMode); return;											
			}			
			
			sendMessages(producer, params.Topic, params.NumMss, params.Mss);
		}
		catch (Exception e)
		{
			System.out.println("Fallo en hilo principal: ");
			e.printStackTrace();
		}
		finally
		{
			if (kss!=null)
				kss.logout();
			
			if (kpp!=null)
				kpp.logout();				
		}
	}
	
	
	public static void sendMessages(KafkaByteArrayProducerCore producer, String topic, int nMsgs, String mss) 
	{
    	long timer = System.currentTimeMillis();

		if (producer!=null)
			for (int i=0;i<nMsgs;i++)
				producer.SendKafkaMsg(topic, mss.getBytes());

		System.out.println(String.format("Mensajes enviados: %s; TOPIC: %s; Tiempo: %s", nMsgs, topic, ((System.currentTimeMillis() - timer))));

	}
}
