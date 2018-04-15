package com.nem.auradiagnostic.authentication.kerberos;

import com.nem.auradiagnostic.authentication.AuthenticatedEntity;

public class KerberosAuthenticatedEntity extends AuthenticatedEntity
{
	protected KrbAuthenticationEnvironment krbEnv;
	private static final String krbDebugVerbose = "true";
	
	protected class KrbEnums 
	{
		public static final String kerberosPrincipal = "kerberosPrincipal";
		public static final String kerberosKeytab = "kerberosKeytab";
		public static final String kerberos = "Kerberos";
		public static final String krb5_conf = "java.security.krb5.conf";
		public static final String krb5_debug = "sun.security.krb5.debug";		
		public static final String krb5_jaas = "java.security.auth.login.config";
	}

	public KerberosAuthenticatedEntity(String krb5, String principal, String jaas, String jaasEntry) throws Exception 
	{
		super();
		krbEnv = new KrbAuthenticationEnvironment(krb5, principal, jaas, jaasEntry); 
		
		if (!login())
			throw new Exception(String.format("Error autenticando al usuario en base a los parámetros de seguridad: krb5= %s, "
					+ "principal=%s, Jaas=%s, JaasEntry=%s",krb5,principal,jaas,jaasEntry));
	}
	
	public KerberosAuthenticatedEntity(String krb5, String principal, String password) 
	{
		super();
		krbEnv = new KrbAuthenticationEnvironment(krb5, principal, null, password); 
	}	
	
	@Override
	public String getSecurityInfo() 
	{
		return String.format("Principal: %s || Krb5 file: %s || Keytab: %s", krbEnv.principal, krbEnv.krb5ConfPath, krbEnv.key);
	}
	
	public boolean login() 
	{		
		try 
		{
		    System.setProperty( KrbEnums.krb5_conf , krbEnv.krb5ConfPath);
		    System.setProperty( KrbEnums.krb5_jaas, krbEnv.key);
	        System.setProperty( KrbEnums.krb5_debug, krbDebugVerbose);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}		
		return true;			
	}
	
	public class KrbAuthenticationEnvironment extends AuthenticationEnvironment
	{	
		String krb5ConfPath;
		int loginMode;
		
		public final class LoginModes
		{
			public static final int KeyTab = 0;	
			public static final int Password = 1;
		}
		
		
		public KrbAuthenticationEnvironment(String krb5ConfPath, String principal, String jaasPath, String jaasEntry) 
		{
			super(principal, jaasPath, jaasEntry);
			this.krb5ConfPath = krb5ConfPath;
			
			loginMode = LoginModes.KeyTab;
		}
		
		public KrbAuthenticationEnvironment(String krb5ConfPath, String principal, String password) 
		{
			super(principal,password);
			this.krb5ConfPath = krb5ConfPath;
			
			loginMode = LoginModes.Password;
		}
		
	}
}
