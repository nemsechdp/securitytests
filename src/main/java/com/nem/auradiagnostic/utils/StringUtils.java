package com.nem.auradiagnostic.utils;

public class StringUtils
{
	
	public static Boolean IsNullOrEmpty(String s)
	{
		return s == null || s.length() == 0;
	}
	
	public static Boolean IsNullOrWhiteSpace(String s)
	{
		return s == null || s.trim().length() == 0;
	}
	
	public static Boolean IsNullOrEmptyOrWhiteSpace(String s)
	{
		return s == null || s.length() == 0 || s.trim().length() == 0;
	}
}
