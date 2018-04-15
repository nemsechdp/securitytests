package com.nem.auradiagnostic.authentication.kerberos.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.security.UserGroupInformation;

import com.nem.auradiagnostic.authentication.kerberos.KerberosAuthenticatedEntity;
import com.nem.auradiagnostic.crane.utils.ConnectionGetter;

public class HbaseKerberosAuthenticatedEntity extends KerberosAuthenticatedEntity 
{
	protected Connection connection;
	protected Admin admin;

	public class HbKrbEnums 
	{
		public final static String hadoop_auth = "hadoop.security.authentication";
	}
	protected UserGroupInformation ugi;
    private static Configuration hBaseconfiguration = null;

    static 
    {
    	hBaseconfiguration = HBaseConfiguration.create();
    	hBaseconfiguration.set(HbKrbEnums.hadoop_auth, KrbEnums.kerberos);
    }

	public HbaseKerberosAuthenticatedEntity(String krb5Conf, String principal, String jaas, String jaasEntry, String keytab) throws Exception  
	{
		super(krb5Conf, principal, jaas, jaasEntry);
		
		if (!trySetConnection(principal, keytab))
			throw new Exception(String.format("No se ha podido realizar la conexión a HBASE, principal : %s , keytab : %s",principal,keytab));
	}
	
	
	/**
	 * Utilizamos el keytab y el principal para autenticarnos en Kerberos.
	 */
	private boolean trySetConnection(String hbasePrincipal, String keyTab) 
	{
	    try 
	    {
			String hBaseprincipal = System.getProperty(KrbEnums.kerberosPrincipal, hbasePrincipal);
			String keytabLocation = System.getProperty(KrbEnums.kerberosKeytab, keyTab);
	        
			UserGroupInformation.setConfiguration(hBaseconfiguration);
	        ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(hBaseprincipal, keytabLocation);
	        UserGroupInformation.setLoginUser(ugi);
			
	        connection = new ConnectionGetter(hBaseconfiguration).getConnection();
	        admin = connection.getAdmin();
	    } 
	    
	    catch(IOException e) 
	    {
	    	e.printStackTrace();
	    	return false;
	    }
	    
	    return true;		
	}

	public Connection getConnection() 
	{
		return connection;
	}

	public void setConnection(Connection connection) 
	{
		this.connection = connection;
	}
	
	public void disconnect() throws IOException
	{
		connection.close();
	}


	public UserGroupInformation getUgi() 
	{
		return ugi;
	}

	public void setUgi(UserGroupInformation ugi) 
	{
		this.ugi = ugi;
	}

}
