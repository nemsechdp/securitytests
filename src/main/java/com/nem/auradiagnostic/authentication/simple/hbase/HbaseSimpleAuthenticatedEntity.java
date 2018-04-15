package com.nem.auradiagnostic.authentication.simple.hbase;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.login.LoginException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.security.UserGroupInformation;

import com.nem.auradiagnostic.authentication.simple.SimpleAuthenticatedEntity;
import com.nem.auradiagnostic.crane.utils.ConnectionGetter;

public class HbaseSimpleAuthenticatedEntity extends SimpleAuthenticatedEntity 
{
	protected Connection connection;
	protected HBaseAdmin admin;
	protected UserGroupInformation ugi;
    private static Configuration hBaseconfiguration = null;

	public class HbSimpleEnums 
	{
		public final static String hadoop_auth = "hadoop.security.authentication";
		public final static String hadoop_user = "HADOOP_USER_NAME";
	}

    static 
    {
    	hBaseconfiguration = HBaseConfiguration.create();
    	hBaseconfiguration.set(HbSimpleEnums.hadoop_auth, "simple");
    	hBaseconfiguration.set(HbSimpleEnums.hadoop_user, "hbase");
    }

	public HbaseSimpleAuthenticatedEntity(String jaas) throws Exception  
	{
		super(jaas);
				
		if (!trySetConnection())
			throw new Exception(String.format("No se ha podido realizar la conexión a HBASE, jaas : %s",jaas));
	}
	
	private boolean trySetConnection() throws LoginException, InterruptedException 
	{
		try 
	    {	    	
			if (dumbEnv.principal.equals(null))
				throw new Exception("Principal es nulo, comprobar JAAS y env");
       
	        UserGroupInformation ugi = UserGroupInformation.createRemoteUser(dumbEnv.principal); 
	        
	        //Realizamos la conexión como usuarios de la chapa, y recogemos la conexión y su admin para el cliente.  
	        ugi.doAs(new PrivilegedExceptionAction<Void>() {
	            public Void run() throws Exception 
	            {
	    	        connection = new ConnectionGetter(hBaseconfiguration).getConnection();
	                return null; 
	            }
	        });

	        admin = (HBaseAdmin)connection.getAdmin();
        } 
	    
	    catch( Exception e) 
	    {
	    	System.out.println(e.getMessage());
	    	return false;
	    }
	    
	    return true;		
	}
	
	@Override
	public boolean login() 
	{
		//en el caso de hbase, si se realiza el setteo del env como lo define su padre, las conexiones
		//que se realicen desde el cliente java al zookeeper creerá que pide GSSAPI, o sea, usando
		//Kerberos. Devolvemos true directamente para evitarlo.
		
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

	public UserGroupInformation getUgi() 
	{
		return ugi;
	}

	public void setUgi(UserGroupInformation ugi) 
	{
		this.ugi = ugi;
	}
	
	public void disconnect() throws IOException
	{
		connection.close();
	}
}
