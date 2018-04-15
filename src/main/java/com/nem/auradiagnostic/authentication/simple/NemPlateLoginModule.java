package com.nem.auradiagnostic.authentication.simple;

import java.util.Map;


import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.mortbay.log.Log;

import com.nem.auradiagnostic.authentication.simple.SimpleAuthenticatedEntity.SimpleEnums;
import com.nem.auradiagnostic.utils.KeyValueJaasReader;
@SuppressWarnings("unused")

public class NemPlateLoginModule implements LoginModule 
{
	   private static final String dummyPass = "dummy";
	   private static final String userName = "username";

	   private Subject subject;
	   private boolean debug = false;
	   private boolean succeeded = false;
	   private boolean commitSucceeded = false;
	   private CallbackHandler callbackHandler;
	   private String username;
	   private char[] password;
	   private SamplePrincipal userPrincipal;

	   /**
	    * LoginModule pensado para autenticación mediante chapa con un único parámetro: [NOMBRE]
	    *
	    *
	    * @param subject El Sujeto que va a ser autenticado. <p>
	    * @param sharedState Estado compartido del módulo. <p>
	    * 
	    * Llamado al leer el JAAS y descubrir cuál es el loginModule
	    */
	   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<java.lang.String, ?> sharedState, Map<java.lang.String, ?> options) 
	   {
	       this.subject = subject;
	       subject.getPublicCredentials().add(username);
	       subject.getPrivateCredentials().add(dummyPass);
	       this.callbackHandler = callbackHandler;
	       debug = "true".equalsIgnoreCase((String)options.get("debug"));
	   }

	   /**
	    * Login DUMMY 
	    * Autoriza si existe el parámetro <<username>> en el JAAS
	    *
	    */
	   public boolean login()  
	   {
		   try 
		   {			   
			   username = System.getProperty(SimpleAuthenticatedEntity.SimpleEnums.jaasPrincipal);

			   if (null==username || username.isEmpty())
			   	   succeeded = false;
			   else
				   succeeded = true;

		       System.out.println("Autenticado como " + username);
		   }		   

		   catch (Exception e)
		   {
			   System.out.println("Error en la lectura del JAAS");
			   e.printStackTrace();
		   }

		   return succeeded;
	   }

	   /** Inyecta el subject en el loginContext
	    */
	   public boolean commit() throws LoginException 
	   {
	       if (succeeded == false) 
	           return false;
	       
	       else 
	       {
	           userPrincipal = new SamplePrincipal(username);
	         
	           if (!subject.getPrincipals().contains(userPrincipal))
	               subject.getPrincipals().add(userPrincipal);

		       subject.getPrivateCredentials().clear();
		       subject.getPublicCredentials().clear();
		       //*credenciales
		       subject.getPublicCredentials().add(username);
		       subject.getPrivateCredentials().add(dummyPass);
		       //*
	           if (debug) 
	           {
	               System.out.println("\t\t[NemPlateLoginModule] " +
	                               "añadido Principal.");
	           }
	           // Resetear para otros posibles commits.
	           username = null;

	           commitSucceeded = true;
	           return true;
	       }
	   }

	   public boolean abort() throws LoginException 
	   {
	       if (succeeded == false) 
	       {
	           return false;
	       } 
	       
	       else if (succeeded == true && commitSucceeded == false) 
	       {
	    	   //Ha realizado el login(había nombre en la chapa) pero la autenticación ha fallado
	           succeeded = false;
	           username = null;
	           if (password != null) 
	           {
	               for (int i = 0; i < password.length; i++)
	                   password[i] = ' ';
	               password = null;
	           }
	           userPrincipal = null;
	       } else 
	       {
	    	   //Otros errores (red,..)
	    	   logout();
	       }
	       
	       return true;
	   }

	   public boolean logout() throws LoginException 
	  {
	       subject.getPrincipals().remove(userPrincipal);
	       succeeded = false;
	       succeeded = commitSucceeded;
	       username = null;
	       if (password != null) {
	           for (int i = 0; i < password.length; i++)
	               password[i] = ' ';
	           password = null;
	       }
	       userPrincipal = null;
	       return true;
	   }

}
