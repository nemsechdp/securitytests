package com.nem.auradiagnostic.crane.tests;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.nem.auradiagnostic.crane.HbaseClient;
import com.nem.auradiagnostic.crane.HbaseClientKerberos;
import com.nem.auradiagnostic.crane.HbaseClientNemPlate;
import com.nem.auradiagnostic.utils.ConfigProperties;

public class HbaseClientTest 
{
	static HbaseClientNemPlate hbn;
	static HbaseClientKerberos hbk;
	static 
	{
		System.setProperty("hadoop.home.dir", "C:\\winutils");
		//System.setProperty("hadoop.home.dir", "/usr/bin/");  
	}	
	private static final String DEFAULT_CONF_KEY = "hbase";
	
	public static void main(String args[]) throws Exception 
	{	
		if (args.length<2)
			throw new Exception("Args: [0]props file;[1]auth mode(kerberos/nemplate)");

		try 
		{
			ConfigProperties properties = new ConfigProperties();
			properties.load(new FileInputStream(args[0]));
			HbaseParams params = new HbaseParams();
			params.readParams(properties);
			
			String authMode = args[1];
	
			switch (authMode.toLowerCase())
			{
				case "kerberos": 			launchHbaseKerb(params);
											break;
				
				case "nemplate": 			launchHbaseNemPlate(params);
											break;
										
				default: 					System.out.println("Opción no reconocida: [" + authMode + "]");										
			}
		}
		catch (Exception e)
		{
			System.out.println("Fallo en hilo principal: ");
			e.printStackTrace();
		}		
		finally 
		{
			if (hbn!=null)
			{
				hbn.logout();
				hbn.disconnect();
			}
			
			if (hbk!=null)
			{
				hbk.logout();
				hbk.disconnect();
			}
		}
	}
	
	private static void launchHbaseNemPlate(HbaseParams params) throws Exception
	{
		
		try 
		{
			hbn = new HbaseClientNemPlate(params.jaas);
			launchTest(hbn.getClient(), params);
		}
		catch (Exception e)
		{
			System.out.println("Test fallido: "+ e);
			e.printStackTrace();
		}
		finally 
		{
			if (hbn!=null)
				hbn.disconnect();
		}
	}


	private static void launchHbaseKerb(HbaseParams params) throws Exception 
	{

		try 
		{
			hbk = new HbaseClientKerberos(params.krb5, params.principal,
					params.jaas, params.entry, params.keytab);		

			launchTest(hbk.getClient(), params);
		}
		catch (Exception e)
		{
			System.out.println("Test fallido: "+ e);
			e.printStackTrace();
		}
		finally 
		{
			if (hbk!=null)
				hbk.disconnect();
		}
	}

	private static void launchTest(HbaseClient client, HbaseParams params) throws Exception
	{
		TableName tableTest = TableName.valueOf(params.Table);
		String familyTestOne = params.ColumnFamilyOne;
		HTableDescriptor desc = new HTableDescriptor(tableTest);
		desc.addFamily(new HColumnDescriptor(familyTestOne));

		try 
		{
			//***TEST****
			//Crea tablas temporales. Pensado para tareas de autenticación.
			//Modificar órden y funciones enable/disable para alterar el tipo de test
			
			//1- list
/*			for (HTableDescriptor d : client.getTables())
			{
				String n = d.getNameAsString();
				System.out.println(n);
				if (n.equalsIgnoreCase(desc.getTableName().getNameAsString()))
				{
					System.out.println("La tabla ya existe. Borrando tabla ..." + n);

					if (HbaseClient.admin.isTableEnabled(desc.getTableName()))
						HbaseClient.admin.disableTable(tableTest);
					return;
				}
			}
				*/

			//2- create
			client.createTable(desc);					
			//para crear la tabla, utilizar el username=hbase en el fichero JAAS.
			System.out.println("[CREATE] -> OK");
			
			//3- put
//			HbaseClient.admin.disableTable(tableTest);

			client.put(params.Table, params.Row, params.ColumnFamilyOne);  
			System.out.println("[WRITE] -> OK");

			
			//4- scan
			if (!HbaseClient.admin.isTableEnabled(desc.getTableName()))
				HbaseClient.admin.enableTable(tableTest);

			Table tabla= HbaseClient.connection.getTable(tableTest);
			client.printLittleScan(tabla, Bytes.toBytes(params.Row));
			System.out.println("[READ] -> OK");
			//*		   
			//5- delete
//			client.deleteData(params.Table, params.Row, params.ColumnFamilyOne);
		
		}
		finally
		{
//			HbaseClient.admin.disableTable(tableTest);

			//6- delete table
//			client.deleteTable(desc);     
		}
	}


	static class HbaseParams
	{		
		public String krb5;
		public String principal;
		public String jaas;
		public String keytab;
		public String file;
		public String entry;
		public String Table;
		public String Row;
		public String ColumnFamilyOne;
		public String ColumnFamilyTwo;

		private void readParams(ConfigProperties properties) throws IOException 
		{
			this.krb5 = properties.ReadMandatoryString
					(properties.GetKey(DEFAULT_CONF_KEY, "krb5"));
			this.entry = properties.ReadMandatoryString
					(properties.GetKey(DEFAULT_CONF_KEY, "entry"));

			this.principal = properties.ReadMandatoryString
					(properties.GetKey(DEFAULT_CONF_KEY, "principal"));

			this.jaas= properties.ReadMandatoryString
					(properties.GetKey(DEFAULT_CONF_KEY, "jaas"));
			this.keytab = properties.ReadMandatoryString
					(properties.GetKey(DEFAULT_CONF_KEY, "keytab"));
			this.Table = properties.ReadMandatoryString
					(properties.GetKey(DEFAULT_CONF_KEY, "table"));
			this.Row = properties.ReadMandatoryString
					(properties.GetKey(DEFAULT_CONF_KEY, "row"));
			this.ColumnFamilyOne = properties.ReadMandatoryString
					(properties.GetKey(DEFAULT_CONF_KEY, "columnfamily_1"));
			this.ColumnFamilyTwo = properties.ReadMandatoryString
					(properties.GetKey(DEFAULT_CONF_KEY, "columnfamily_2"));
		}
	}
}
