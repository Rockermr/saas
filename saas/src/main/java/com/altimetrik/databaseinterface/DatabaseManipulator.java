package com.altimetrik.databaseinterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DatabaseManipulator implements DbOperations {
	@Override
	public void insert(List<String> data) throws SQLException{
		Connection conn = null;
		PreparedStatement pStatement = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/saas", "root", "toor");
			pStatement = conn.prepareCall(
					"INSERT INTO amountpayable (invoiceNo,invoiceDate,invoicePo,address,payableAmount) values (?,?,?,?,?)");

			Iterator<String> itr = data.iterator();
			int count = 1;
			while (itr.hasNext()) {
				pStatement.setString(count, itr.next().toString());
				count++;
			}
			pStatement.execute();

		}

		finally {

			if (pStatement != null)
				pStatement.close();
			if (conn != null)
				conn.close();

		}
	}

	@Override
	public List<String> retrive() throws SQLException {
		Connection conn = null;
		Statement stm = null;
		ResultSet rs = null;
		List<String> retrivedData = new ArrayList<String>();

		try {

			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/saas", "root", "toor");

			stm = conn.createStatement();
			rs = stm.executeQuery("select * from amountpayable");

			while (rs.next()) {
				retrivedData.add(String.valueOf(rs.getInt(1)));
				retrivedData.add(rs.getString(2));
				retrivedData.add(rs.getString(3));
				retrivedData.add(rs.getString(4));
				retrivedData.add(rs.getString(5));
				retrivedData.add(rs.getString(6));
				retrivedData.add(rs.getString(7));

			}

		} finally {

			if (rs != null)
				rs.close();
			if (stm != null)
				stm.close();
			if (conn != null)
				conn.close();

		}

		return retrivedData;

	}

	@Override
	public List<String> modify(int id) throws SQLException {
		Connection conn = null;
		PreparedStatement pStatement = null;
		PreparedStatement retriver = null;
		ResultSet resultSet = null;
		List<String> retrivedData = new ArrayList<String>();

		try {

			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/saas", "root", "toor");

			if (id != 0) {

				retriver = conn
						.prepareCall("select invoiceNo,remittanceStatus,payableAmount from amountpayable where id=?");
				retriver.setInt(1, id);

				resultSet = retriver.executeQuery();
				while (resultSet.next()) {
					if (resultSet.getString("remittanceStatus").equals("Not Approved")) {
						retrivedData.add(resultSet.getString("invoiceNo"));
						retrivedData.add(resultSet.getString("payableAmount"));

						pStatement = conn
								.prepareCall("update amountpayable set remittanceStatus = 'Approved' where id = ?");
						pStatement.setInt(1, id);
						pStatement.executeUpdate();
					}

				}

			} else {
				retriver = conn.prepareCall("select invoiceNo,remittanceStatus,payableAmount from amountpayable");
				resultSet = retriver.executeQuery();
				while (resultSet.next()) {
					if (resultSet.getString("remittanceStatus").equals("Not Approved")) {
						retrivedData.add(resultSet.getString("invoiceNo"));
						retrivedData.add(resultSet.getString("payableAmount"));
					}

					pStatement = conn.prepareCall("update amountpayable set remittanceStatus = 'Approved'");
					pStatement.executeUpdate();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (pStatement != null)
				pStatement.close();
			if (conn != null)
				conn.close();

		}

		return retrivedData;

	}

}
