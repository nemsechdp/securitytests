package com.nem.auradiagnostic.authentication;

import java.security.Principal;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public abstract class AuthenticatedEntity 
{	
	//El LoginContext es el encargado de realizar las llamadas a los loginModules.
	//En el caso en el que el contexto no lo creen las propias librerías de los servicios Hadoop, será necesario inicializar esta variable,
	//indicando el bloque que debe leer del fichero JAAS.
	protected LoginContext lc;
    public abstract String getSecurityInfo();
    
	public boolean login()
	{
		try 
		{
			if (lc != null)
				lc.login();			
		} catch (LoginException e) 
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean logout()
	{
		try 
		{
			if (lc != null)
				lc.logout();			
		} catch (LoginException e) 
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	
	protected LoginContext getLoginContext()
	{
		return lc;
	}
	
	protected Subject getSubject()
	{
		return lc.getSubject();
	}	
	
	
	
	// Por defecto devuelve el primer principal que encuentra. Si se desea una implementación propia, override.
	protected Principal getOnePrincipal()
	{
		Subject subject = getSubject();
		return subject != null? (subject.getPrincipals().size()>0? subject.getPrincipals().iterator().next() : null) :null;
	}
	
	protected Set<Principal> getAllPrincipals()
	{
		return getSubject().getPrincipals();
	}
		
	public class AuthenticationEnvironment 
	{
		public String principal;
		public String key;
	    public String jaasPath;
	    public String jaasEntry;	    

	    
		public AuthenticationEnvironment(String principal, String jaasPath, String jaasEntry)
		{
			this(principal, jaasPath);
			this.jaasEntry = jaasEntry;
		}

		public AuthenticationEnvironment(String principal, String key)
		{
			this.principal = principal;
			this.key = key;
		}
	}
}
