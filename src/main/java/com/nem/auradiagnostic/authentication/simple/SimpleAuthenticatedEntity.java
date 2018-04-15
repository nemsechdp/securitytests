package com.nem.auradiagnostic.authentication.simple;

import java.util.TreeMap;

import javax.security.auth.login.LoginException;

import com.nem.auradiagnostic.authentication.AuthenticatedEntity;
import com.nem.auradiagnostic.utils.KeyValueJaasReader;

public class SimpleAuthenticatedEntity extends AuthenticatedEntity
{	
	protected SimpleAuthenticationEnvironment dumbEnv;
	protected TreeMap<String, String> jaasMap;

	public static  final class SimpleEnums 
	{
		public static final String jaasPrincipal = "username";
		public static final String jaasFile = "java.security.auth.login.config";
	}
	
	public SimpleAuthenticatedEntity(String jaasPath) throws Exception 
	{
		super();
		
		jaasMap = KeyValueJaasReader.getJaasProperties(jaasPath);		
		String jaasPrincipal;
		
		try
		{
			jaasPrincipal = jaasMap.get(SimpleEnums.jaasPrincipal);		
			dumbEnv = new SimpleAuthenticationEnvironment(jaasPrincipal, jaasPath); 
		}
		catch(Exception e)
		{
			System.out.println(String.format("Error creando el entorno. Comprobar formato del fichero %s", jaasPath));
			throw(e);
		}
		
		if (!login())
			throw new Exception(String.format("Error autenticando al usuario en base a los parámetros de seguridad: jaas= %s, "
					+ "principal=%s",jaasPath, jaasPrincipal));
	}		
	
	
	@Override
	public String getSecurityInfo() 
	{
		return String.format("Principal: %s || Jaas file: %s || Password: %s", dumbEnv.principal, dumbEnv.jaasPath, dumbEnv.key);
	}

	@Override
	public boolean login() 
	{
		try 
		{
			System.setProperty( SimpleEnums.jaasFile, dumbEnv.key);
			System.setProperty(SimpleEnums.jaasPrincipal, dumbEnv.principal);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}		
		return true;			
	}

	@Override
	public boolean logout()
	{
		try 
		{
			if (lc!=null)
				lc.logout();
		} catch (LoginException e) 
		{
			e.printStackTrace();
		}
		return true;
	}	
	
	public class SimpleAuthenticationEnvironment extends AuthenticationEnvironment
	{	
		int loginMode;
		
		public final class LoginModes
		{
			public static final int UserNoPass = 0;	
			public static final int UserAndPass = 1;
		}
		
		public SimpleAuthenticationEnvironment(String principal, String jaas, String jaasEntry) 
		{
			super(principal, jaas, jaasEntry);
			loginMode = LoginModes.UserNoPass;
		}
		
		public SimpleAuthenticationEnvironment(String principal, String password) 
		{
			super(principal, password);
			loginMode = LoginModes.UserAndPass;
		}		
	}

}