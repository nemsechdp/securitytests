package com.nem.auradiagnostic.crane;

import com.nem.auradiagnostic.authentication.simple.hbase.HbaseSimpleAuthenticatedEntity;


public class HbaseClientNemPlate extends HbaseSimpleAuthenticatedEntity
{
	private HbaseClient client;
	
	public HbaseClientNemPlate(String jaas) throws Exception 
	{
		super(jaas);
		client = new HbaseClient(connection, admin);
	}
	
	public HbaseClient getClient()
	{
		return client;
	}
}

//Los dos clientes se parecen, pero no pueden unirse. Cada uno extiende de una entidad diferente. 
//La raz�n es que el desarrollo se centra en la autenticaci�n y no en la l�gica del cliente.
//Lo mismo sucede con los Kafkas.
