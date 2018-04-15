package com.nem.auradiagnostic.crane;

import java.io.IOException;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
@SuppressWarnings("deprecation")

public class HbaseClient implements IHbaseClient 
{
	public static Connection connection;
	public static Admin admin;

	public HbaseClient(Connection connection, Admin admin)
	{
		HbaseClient.connection = connection;
		HbaseClient.admin = admin;
	}	
	
	@Override
	public void put( String table, String rowkey, String family) throws IOException
	{
		Table t = connection.getTable(TableName.valueOf(table));
        Put P1 = new Put(Bytes.toBytes(rowkey));  
        P1.add(Bytes.toBytes(family),   Bytes.toBytes("family"),Bytes.toBytes("aupa athletic"));
        t.put(P1);
        System.out.println("Datos insertados en Hbase");   

	}
	
	@Override
	public void deleteData(String table, String rowkey, String family) throws IOException
	{
		Table t = connection.getTable(TableName.valueOf(table));
		Delete delete = new Delete(Bytes.toBytes(rowkey));
		delete.deleteColumn(Bytes.toBytes(family), Bytes.toBytes(rowkey));
		t.delete(delete);        
        System.out.println("Datos eliminados de Hbase");       
	}

	@Override
	public void printLittleScan(Table table, byte[] row) throws IOException
	{
		//que sea corta, es para tests.
        Scan scan = new Scan(Bytes.toBytes("ia"), Bytes.toBytes("iz"));
        ResultScanner scanner = table.getScanner(scan);
        
        int count = 0;
        for (Result result : scanner) {
            System.out.println("Resultado = " + result);
            count++;
        }
        System.out.println(String.format("Escaneados %d resultados\n", count));	
	}
	
	
	public HTableDescriptor[] getTables() throws IOException
	{
		return admin.listTables();
	}

	public void createTable(HTableDescriptor descriptor) throws IOException
	{
		admin.createTable(descriptor);
	}

	public void delete(String tableName, String rowKey, String columnFamily) throws IOException 
	{
		try (Table table = connection.getTable(TableName.valueOf(tableName)))
		{
			Delete delete = new Delete(Bytes.toBytes(rowKey));
			delete.addFamily(Bytes.toBytes(columnFamily));
			table.delete(delete);
		}
	}

	public void deleteTable(HTableDescriptor descriptor) throws IOException 
	{
		Admin admin = connection.getAdmin();
		TableName tabla = descriptor.getTableName();

		admin.deleteTable(tabla);
	}

}


