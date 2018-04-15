package com.nem.auradiagnostic.crane;

import java.io.IOException;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Table;

public interface IHbaseClient 
{	
    public HTableDescriptor[] getTables() throws IOException;
    public void createTable(HTableDescriptor descriptor) throws IOException;
	public void deleteTable(HTableDescriptor descriptor) throws IOException;
	public void deleteData(String table, String rowkey, String family) throws IOException;
	void put(String table, String rowkey, String family) throws IOException;
	public void printLittleScan(Table table, byte[] row) throws IOException;
}
