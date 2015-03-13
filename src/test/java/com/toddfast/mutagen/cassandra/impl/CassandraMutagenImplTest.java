package com.toddfast.mutagen.cassandra.impl;

import com.toddfast.mutagen.cassandra.CassandraMutagen;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.toddfast.mutagen.Plan;
import com.toddfast.mutagen.State;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 * @author Todd Fast
 */
public class CassandraMutagenImplTest {

	@BeforeClass
	public static void setUpClass()
			throws Exception {
		cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		session = cluster.connect(keyspace);
	}

	private static void createKeyspace(){

		System.out.println("Creating keyspace "+keyspace+"...");
		//params for keyspace
		int keyspaceReplicationFactor=1;
		String keyspaceStrategyClass="SimpleStrategy";
		
		session.execute("CREATE KEYSPACE " + keyspace + " WITH REPLICATION "
				+ "= {'class':"+
				keyspaceStrategyClass+"', 'replication_factor':"+keyspaceReplicationFactor+"};");
		System.out.println("Created keyspace "+keyspace);
	}


	@AfterClass
	public static void tearDownClass()
			throws Exception {
//		ResultSet result=session.execute("DROP KEYSPACE" + keyspace);
//		System.out.println("Dropped keyspace "+keyspace);
	}


	@Before
	public void setUp() {
	}


	@After
	public void tearDown() {
	}


	/**
	 * This is it!
	 *
	 */
	private Plan.Result<Integer> mutate()
			throws IOException {

		// Get an instance of CassandraMutagen
		// Using Nu: CassandraMutagen mutagen=$(CassandraMutagen.class);
		CassandraMutagen mutagen=new CassandraMutagenImpl();

		// Initialize the list of mutations
		String rootResourcePath="com/toddfast/mutagen/cassandra/test/mutations";
		mutagen.initialize(rootResourcePath);

		// Mutate!
		Plan.Result<Integer> result=mutagen.mutate(keyspace);

		return result;
	}


	/**
	 *
	 *
	 */
	@Test
	public void testInitialize() throws Exception {
		
		Plan.Result<Integer> result = mutate();

		// Check the results
		State<Integer> state=result.getLastState();

		System.out.println("Mutation complete: "+result.isMutationComplete());
		System.out.println("Exception: "+result.getException());
		if (result.getException()!=null) {
			result.getException().printStackTrace();
		}
		System.out.println("Completed mutations: "+result.getCompletedMutations());
		System.out.println("Remining mutations: "+result.getRemainingMutations());
		System.out.println("Last state: "+(state!=null ? state.getID() : "null"));

		assertTrue(result.isMutationComplete());
		assertNull(result.getException());
		assertEquals((state!=null ? state.getID() : (Integer)(-1)),(Integer)4);
	}


	/**
	 *
	 *
	 */
	public ResultSet query(Object... values){
		String columnFamily = "Test1";
		//query
		String selectStatement = "SELECT * FROM \"" + columnFamily + "\" " + "WHERE key=?";
		PreparedStatement preparedSelectStatement = session.prepare(selectStatement);
		BoundStatement boundSelectStatement = preparedSelectStatement.bind(values);
		return session.execute(boundSelectStatement);
	}
	@Test
	public void testData() throws Exception {
		ResultSet results1 = query("row1");
		Row row1 = results1.one();
		assertEquals("foo",row1.getString("value1"));
		assertEquals("bar",row1.getString("value2"));
		
		ResultSet results2 = query("row2");
		Row row2 = results2.one();
		assertEquals("chicken",row2.getString("value1"));
		assertEquals("sneeze",row2.getString("value2"));

		ResultSet results3 = query("row3");
		Row row3 = results3.one();

		assertEquals("bar",row3.getString("value1"));
		assertEquals("baz",row3.getString("value2"));
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private static String keyspace = "apispark";
	private static Cluster cluster;
	private static Session session;
}
