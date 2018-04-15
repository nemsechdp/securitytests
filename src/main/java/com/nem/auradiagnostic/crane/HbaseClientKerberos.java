package com.nem.auradiagnostic.crane;

import com.nem.auradiagnostic.authentication.kerberos.hbase.HbaseKerberosAuthenticatedEntity;


public class HbaseClientKerberos extends HbaseKerberosAuthenticatedEntity
{
	private HbaseClient client;
	
	public HbaseClientKerberos(String krb5Conf, String principal, String jaas, String jaasEntry, String keytab) throws Exception 
	{
		super(krb5Conf, principal, jaas, jaasEntry, keytab);
		client = new HbaseClient(connection, admin);
	}		

	public HbaseClient getClient()
	{
		return client;
	}

}
