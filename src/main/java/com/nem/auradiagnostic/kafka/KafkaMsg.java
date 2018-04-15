package com.nem.auradiagnostic.kafka;
//oop y tal
public class KafkaMsg
{
	public KafkaMsg(byte[] key, byte[] payload)
	{
		Key = key;
		Payload = payload;
	}
	
	public KafkaMsg(byte[] key, byte[] payload, Long timestamp)
	{
		Key = key;
		Payload = payload;
		Timestamp = timestamp;
	}

	public byte[] Key;
	public byte[] Payload;
	public Long Timestamp;
}
