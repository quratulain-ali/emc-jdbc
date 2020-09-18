package org.eclipse.epsilon.emc.mysql;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.dom.OperationCallExpression;
import org.eclipse.epsilon.eol.dom.Statement;
import org.eclipse.epsilon.eol.dom.StringLiteral;
import org.junit.Test;

public class JDBCTest {
	String translatedQuery = "";
	ArrayList<String> expected = new ArrayList<String>();
	
	@Test
	public void testEolToSqlTranslation() throws Exception{
		
		EolModule module = new EolModule();
		module.parse(new File("src/org/eclipse/epsilon/emc/mysql/JDBCTestCase.eol"));
		module.getCompilationContext().setModelFactory(new SubModelFactory());
		module.compile();
		
		expected.add("SELECT * FROM Flight");
		expected.add("SELECT Departure FROM Flight");
		expected.add("SELECT Arrival FROM Flight");
		expected.add("SELECT Duration FROM Flight");
		expected.add("SELECT Name FROM Flight");
		expected.add("SELECT * FROM Flight limit 1");
		expected.add("SELECT COUNT(*) FROM Flight");
		expected.add("SELECT DISTINCT Departure FROM Flight");
	
		List<Statement> statements=module.getMain().getStatements();
		int index = 0;
		for (Statement st : statements)
		{
			StringLiteral string = ((StringLiteral)((OperationCallExpression)st.getChildren().get(0)).getParameterExpressions().get(0));
			translatedQuery = string.getValue();
			assertTrue(expected.get(index).equalsIgnoreCase(translatedQuery));
			index++;
		}
		
	}

}
