package com.nem.auradiagnostic.utils;

import java.io.IOException;
import java.util.Properties;


public class ConfigProperties extends Properties {
	
	private static final long serialVersionUID = 1L;

	public String ReadMandatoryString(String key) throws IOException
	{
		String value = getProperty(key);
		if (value == null || value.isEmpty())
			throw new IOException(String.format("Falta el parámetro de configuración: %s.", key));
		return value;
	}
	
	public String ReadMandatoryString(String prefix, String key) throws IOException
	{
		return ReadMandatoryString(GetKey(prefix, key));
	}
	
	public int ReadMandatoryInteger(String key) throws IOException
	{
		String value = getProperty(key);
		if (value == null || value.isEmpty())
			throw new IOException(String.format("Falta el parámetro de configuración: %s.", key));
		return Integer.parseInt(value);
	}
	
	public String ReadMandatoryStringFromJson(String JsonPrefix, String value) throws IOException
	{
		if (value == null || value.isEmpty())
			throw new IOException(String.format("Falta el parámetro de configuración: %s.", JsonPrefix));
		return value;
	}
	
	public int ReadMandatoryInteger(String prefix, String key) throws IOException
	{
		return ReadMandatoryInteger(GetKey(prefix, key));
	}
	
	public long ReadMandatoryLong(String key) throws IOException
	{
		String value = getProperty(key);
		if (value == null || value.isEmpty())
			throw new IOException(String.format("Falta el parámetro de configuración: %s.", key));
		return Long.parseLong(value);
	}
	
	public long ReadMandatoryLong(String prefix, String key) throws IOException
	{
		return ReadMandatoryLong(GetKey(prefix, key));
	}
	
	public String GetKey(String prefix, String key)
	{
		String value = key;
		if (prefix != null && !prefix.isEmpty())
			value = prefix + "." + key;
		return value;
	}

}
