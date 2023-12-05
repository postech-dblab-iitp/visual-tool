package org.jkiss.dbeaver.ext.turbographpp.graph.chart;

import java.sql.SQLException;
import java.util.List;

import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;

public class GetChartInfoAllJob extends GetChartInfoQueryJob {

	public GetChartInfoAllJob(String jobName, DBPDataSource datasource, GraphChart chart, String query, String label,
			String property) {
		super(jobName, datasource, chart, query, label, property);
	}
	
	@Override
	public boolean getMinMaxInfo(DBRProgressMonitor monitor, List<Long> valList) {
		long min = 0, max = 0;
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("MATCH (n:" + infoLabel + ")" + "RETURN n." + infoProperty);
		boolean first = true;
		
		first = true;
		
		String query = queryBuilder.toString();
		
		try (JDBCSession session =
		        DBUtils.openMetaSession(monitor, dataSource, "Get Min, Max Value")) {
		        JDBCPreparedStatement dbStat = session.prepareStatement(query);
		        try (JDBCResultSet dbResult = dbStat.executeQuery()) {
		            while (dbResult.next()) {
		            	Object temp = JDBCUtils.safeGetObject(dbResult, 1);
		            	if (temp != null) {
		            		long val = Long.valueOf(String.valueOf(temp));
		                	valList.add(val);
		                	
		                	if (first) {
		                		min = max = val;
		                		first = false;
		                	}
		                	if (min > val) {
		                		min = val;
		                	} 
		                	
		                	if (max < val) {
		                		max = val;
		                	}
		            	}
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        } finally {
		            if (dbStat != null) dbStat.close();
		        }
		    } catch (DBCException | SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
		
		    setMinMax(min, max);
		    calcStep(min, max);
		    
		    return true;
		}
}
