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
//La razón es que el desarrollo se centra en la autenticación y no en la lógica del cliente.
//Lo mismo sucede con los Kafkas.
