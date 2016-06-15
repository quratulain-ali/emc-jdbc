package org.eclipse.epsilon.emc.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

import org.eclipse.epsilon.eol.models.IModelElement;

public class Result implements IModelElement {
	
	protected ResultSet resultSet;
	protected int row = -1;
	protected JdbcModel model;
	protected Table table;
	protected HashMap<String, Object> cache = new HashMap<String, Object>();
	protected boolean streamed = false;
	
	public Result(ResultSet rs, JdbcModel model, Table table, boolean streamed) {
		this.resultSet = rs;
		this.model = model;
		this.table = table;
		this.streamed = streamed;
		cacheValues();
	}
	
	public Result(ResultSet rs, int row, JdbcModel model, Table table, boolean streamed) {
		this.resultSet = rs;
		this.row = row;
		this.model = model;
		this.table = table;	
		this.streamed = streamed;
		cacheValues();
	}
	
	protected void cacheValues() {
		
		if (!streamed) return;
		
		try {
			ResultSetMetaData metadata = resultSet.getMetaData();
			for (int i = 1; i<= metadata.getColumnCount(); i++) {
				cache.put(metadata.getColumnName(i).toLowerCase(), resultSet.getObject(i));
			}
		}
		catch (Exception ex) { throw new RuntimeException(ex); }
	}
	
	
	public int getRow() {
		return row;
	}
	
	protected Object getValue(String name) throws SQLException {
		
		if (streamed) {
			return cache.get(name.toLowerCase());
		}
		else {
			//int oldRow = resultSet.getRow();
			if (row != -1) resultSet.absolute(row);
			return resultSet.getObject(name);
			//if (oldRow > 0) resultSet.absolute(oldRow);
		}
	}
	
	protected void setValue(String name, Object value) throws SQLException {
		
		if (streamed) {
			throw new UnsupportedOperationException();
		}
		else {
			//int oldRow = resultSet.getRow();
			if (row != -1) resultSet.absolute(row);
			resultSet.updateObject(name, value);
			resultSet.updateRow();
			//if (oldRow > 0) resultSet.absolute(oldRow);	
		}
	}
	
	public JdbcModel getOwningModel() {
		return model;
	}
	
	public ResultSet getResultSet() {
		return resultSet;
	}
	
	public Table getTable() {
		return table;
	}
	
	@Override
	public boolean equals(Object arg0) {
		try {
			if (arg0 instanceof Result) {
				final Result otherResult = (Result) arg0;

				final int nColumns = getColumnCount();
				if (nColumns != otherResult.getColumnCount()) {
					return false;
				}
				for (int iColumn = 1; iColumn <= nColumns; iColumn++) {
					final String cName = resultSet.getMetaData().getColumnName(iColumn);
					final Object myValue = getValue(cName);
					final Object otherValue = otherResult.getValue(cName);
					if (myValue == null && otherValue != null) {
						return false;
					} else if (!myValue.equals(otherValue)) {
						return false;
					}
				}

				return true;
			}
		} catch (Exception ex) {
			System.err.println("Exception in Result#equals");
			ex.printStackTrace();
		}
		return false;
		// return super.equals(arg0);
	}

	private int getColumnCount() throws SQLException {
		return resultSet.getMetaData().getColumnCount();
	}
	
	public String toString(){
		return getClass()+"#"+hashCode()+" : "+table.getName()+" : "+ row + " : "+cache;		
	}
}
