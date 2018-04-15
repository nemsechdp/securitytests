package com.nem.auradiagnostic.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

public class KeyValueJaasReader 
{
	private final static String moduleField = "module";
	private final static String keyValueField = "=";

    public static TreeMap<String, String> getJaasProperties(String infile) throws IOException 
    {
    	System.out.println(infile);
        TreeMap<String, String> map = new TreeMap<String, String>();
        BufferedReader  bfr = new BufferedReader(new FileReader(new File(infile)));
        String line;        
        
        while ((line = bfr.readLine()) != null) 
        {        	
        	if (line.toLowerCase().contains(moduleField) || !line.contains(keyValueField))
        		continue;
        	
        	String[] pair = line.trim().split("=");
        	map.put(pair[0].trim(), pair[1].trim().replace(";", ""));
        }
        
        bfr.close();
        return(map);
    }
}
