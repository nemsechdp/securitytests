package com.nem.auradiagnostic.kafka.tests;

import java.util.Arrays;

import com.nem.auradiagnostic.kafka.KafkaMsg;
import com.nem.auradiagnostic.kafka.KafkaReader;
import com.nem.auradiagnostic.kafka.configs.KafkaConsumerConfig;
import com.nem.auradiagnostic.kafka.configs.KafkaConsumerConfig.KafkaConsumerParams;
import com.nem.auradiagnostic.kafka.kerberos.KafkaConsumerKerberos;
import com.nem.auradiagnostic.kafka.simple.KafkaConsumerNemPlate;

/*
 * Se acredita en Kerberos y trata de leer N mensajes de un topic.
 * Escupe por consola el payload (byte[]).
 */
public class KafkaConsumerTester
{		
	static KafkaConsumerNemPlate kss;
	static KafkaConsumerKerberos ksk;

	public static void main(String args[]) throws Exception 
	{
		if (args.length<3)
			throw new Exception("Args: [0]Cons properties;[1]ConsumerEntry;[2]AuthMode (kerberos/nemplate)");

		try 
		{			
			KafkaConsumerConfig consumerConf = new KafkaConsumerConfig(args[0],Arrays.asList(args[1]));
			KafkaConsumerParams params = consumerConf.getConsumerParams(args[1]);
			String authMode = args[2];
			
				switch (authMode.toLowerCase())
				{
					case "kerberos": 			
						launchKafkaKerb(params); break;
					
					case "nemplate": 			
						launchKafkaNemPlate(params); break;
											
					default: 					
						System.out.println("Opción no reconodida" + authMode); return;											
				}		
		}
		catch (Exception e)
		{
			System.out.println("Fallo en hilo principal: ");
			e.printStackTrace();
		}		
		finally 
		{
			if (kss!=null)
			{
				kss.logout();
				kss.shutdown();
			}
			
			if (ksk!=null)
			{
				ksk.logout();
				ksk.shutdown();
			}
		}		
	}
	
	
	private static void launchKafkaNemPlate(KafkaConsumerParams params) throws Exception 
	{
		kss = new KafkaConsumerNemPlate(params);
		KafkaReader reader = kss.getKafkaReader(0);
		try 
		{
			readMsgs(reader, params.NumMss);
		}
		finally	
		{
			kss.shutdown();	
		}
				
	}


	private static void launchKafkaKerb(KafkaConsumerParams params) throws Exception 
	{
		KafkaConsumerKerberos kcc = new KafkaConsumerKerberos(params);
		KafkaReader reader = kcc.getKafkaReader(0);		
		try 
		{
			readMsgs(reader, params.NumMss);
		}
		finally	
		{
			kcc.shutdown();
		}

	}


	public static void readMsgs(KafkaReader reader, int n) throws InterruptedException
	{
    	for (int i=0;i<n;i++)
    	{
    		KafkaMsg m = reader.GetOneKafkaMsg();
    		
    		if (m == null)
    			Thread.sleep(1000);    		
    		else
    			System.out.println(":: !KafkaMsg READ! [ACK] :: " + m.Payload);
    	}
	}	


}
