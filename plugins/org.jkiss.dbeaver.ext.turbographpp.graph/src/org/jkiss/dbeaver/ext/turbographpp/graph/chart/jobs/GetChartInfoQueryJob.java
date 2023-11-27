package org.jkiss.dbeaver.ext.turbographpp.graph.chart.jobs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jkiss.dbeaver.ext.turbographpp.graph.GraphChart;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.runtime.AbstractJob;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;

public class GetChartInfoQueryJob extends AbstractJob {
	protected DBPDataSource dataSource;
	protected GraphChart graphChart;
	protected String currentQuery;
	protected String infoLabel;
	protected String infoProperty;
    private long min;
    private long max;

    private LinkedHashMap<String, Object> data = new LinkedHashMap<>();
    
    private List<String> retVars = new ArrayList<>();

    public GetChartInfoQueryJob(String jobName, DBPDataSource datasource, GraphChart chart, String query, String label, String property) {
        super(jobName);
        setUser(true);
        this.dataSource = datasource;
        this.graphChart = chart;
        this.infoLabel = label;
        this.infoProperty = property;
        this.currentQuery = query;
    }

    @Override
    protected IStatus run(DBRProgressMonitor monitor) {

        if (dataSource == null) {
            return Status.CANCEL_STATUS;
        }

        monitor.beginTask("Update GraphDB Type info", 1);
        try {
            String[] temp;
            long count = 0;
            min = 0;
            max = 0;
            data.clear();
            
            graphChart.UILock();
            
            if (currentQuery == null || currentQuery.isEmpty()) {
            	return Status.CANCEL_STATUS;
        	}
        
            retVars = getVariable(currentQuery);
            List<Long> valList = new ArrayList<>();
            
            boolean ret = getMinMaxInfo(monitor, valList);
            
            if (!ret) {
            	return Status.CANCEL_STATUS;
            }
            
            for (String key : data.keySet()) {
                if (key.contains("-")) {
                    temp = key.split("-");
                    count = getNumofStepInfo(valList, Long.valueOf(temp[0]), 
                            Long.valueOf(temp[1]));
                    data.put(key, count);
                } else {
                    count = getNumofStepInfo(valList, Long.valueOf(key), 
                    		Long.valueOf(key));
                    data.put(key, count);
                }
                
            }
            graphChart.runUpdateChart(data);
        } finally {
            monitor.done();
            graphChart.UIUnLock();
        }
        
        return Status.OK_STATUS;
    }

    public boolean getMinMaxInfo(DBRProgressMonitor monitor, List<Long> valList) {
    	
    	StringBuilder queryBuilder = new StringBuilder();
    	String collectAsString = nodeAsString("AllNodes", currentQuery.toString());
    	String rowAsString = nodeAsString("Nodes", currentQuery.toString());
    	String midRetAsString = nodeAsString("MiddleRet", currentQuery.toString());
    	String LastRetAsString = nodeAsString("LastRet", currentQuery.toString());
    	queryBuilder.append(getCurrentQueryWithOutReturn(currentQuery)).append(" WITH ");
        boolean first = true;
        Iterator<String> itr = retVars.iterator();
        while (itr.hasNext()) {
        	String variable = itr.next();
        	if (first) {
        		first = false;
        	} else {
        		queryBuilder.append(" + ");
        	}
        	queryBuilder.append(" COLLECT(" + variable + ")");
        }
        
        queryBuilder.append(" AS ").append(collectAsString)
        .append(" WITH ").append("[" + rowAsString + " IN " + collectAsString)
        .append(" WHERE " + rowAsString + ":" + infoLabel + "]")
        .append(" AS " + midRetAsString)
        .append(" UNWIND " + midRetAsString + " AS " + LastRetAsString)
        .append(" RETURN " + LastRetAsString + "." + infoProperty );
        
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

        calcStep(min, max);
        
        return true;
    }

    private long getNumofStepInfo(List<Long> valList, long fromVal, long toVal) {
        long result = 0;
        Iterator<Long> itr = valList.iterator();
        while (itr.hasNext()) {
        	long val = itr.next();
        	if (fromVal <= val && toVal >= val) {
        		result++;
        		itr.remove();
        	}
        }
        return result;
    }

    protected void calcStep(long min, long max) {
        final int maxStep = 10;
        long current = 0;
        long totalStep = 0, quotient = 0, remainder = 0;
        String key = "";
        current = min;
        if (max != 0) {
            totalStep = max - min;
        }
        quotient = totalStep / 10;
        remainder = totalStep % 10;
        if (totalStep >= 10) {
            for (int i = 0; i < maxStep; i++) {
                if (remainder > 0) {
                    key = String.valueOf(current) + "-";
                    current += quotient + 1;
                    key += String.valueOf(current);
                } else {
                    key = String.valueOf(current) + "-";
                    current += quotient;
                    key += String.valueOf(current);
                }
                remainder--;
                data.put(key, 0);
            }
        } else {
            for (int i = 0; i < totalStep; i++) {
                key = String.valueOf(current);
                current += 1;
                data.put(key, 0);
            }
        }
    }
    
    protected String getCurrentQueryWithOutReturn(String query) {
        int idx = 0;
        String ret = "";
        idx = query.toLowerCase().lastIndexOf("return");
        if (idx > 0) {
            ret = query.substring(0, idx);
            return ret;
        }
        return query; 
    }
    
    private String getCurrentLimitQuery(String query) {
        int idx = 0;
        String ret = "";
        idx = query.toLowerCase().lastIndexOf("limit");
        if (idx > 0) {
            ret = query.substring(idx, query.length());
            return ret;
        }
        return ""; 
    }
    
    private List<String> getVariable(String query) {
        List<String> retVar = new ArrayList<String>();
        
        Pattern pattern = Pattern.compile("[(](.*?)[)]");
        Matcher matcher = pattern.matcher(query);
                
        while (matcher.find()) {
            if(matcher.group(1) ==  null)
                break;
            
            String temp = matcher.group(1);
            int idx = temp.indexOf(":");
            if (idx != -1) {
                temp = temp.substring(0,idx).trim();
            }
            retVar.add(temp);
        }
        return retVar;
    }
    
    protected String nodeAsString(String input, String query) {
    	String ret = input;
    	
    	while (query.contains(ret)) {
    		ret = ret + "x";
    	}
    	return ret;
    }
    
    protected void setMinMax(long min, long max) {
    	this.min = min;
    	this.max = max;
    }
}