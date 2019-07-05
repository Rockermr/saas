package com.altimetrik.databaseinterface;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public interface DbOperations {

	
   public void insert(List<String> data) throws SQLException, SQLIntegrityConstraintViolationException; 
    public List<String> retrive() throws SQLException; 
    public List<String> modify(int id) throws SQLException;


	
}
