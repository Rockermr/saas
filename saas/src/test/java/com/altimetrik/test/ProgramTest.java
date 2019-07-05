package com.altimetrik.test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.altimetrik.databaseinterface.DatabaseManipulator;

public class ProgramTest {

	@Test
	public void testretrive() {
		DatabaseManipulator db=new DatabaseManipulator();
		List<String> test1=Arrays.asList("12345678","100");
		List<String> test2=Arrays.asList();

		try {
		assertEquals(test1,db.modify(1));
		assertEquals(test2,db.modify(100));
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	

}
