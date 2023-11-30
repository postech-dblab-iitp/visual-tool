package org.jkiss.dbeaver.ext.turbographpp.graph.chart.jobs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jkiss.dbeaver.ext.turbographpp.graph.FXGraph;
import org.jkiss.dbeaver.ext.turbographpp.graph.GraphChart;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherNode;
import org.jkiss.dbeaver.model.runtime.AbstractJob;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;

public class GetChartInfoInGraphJob extends AbstractJob {
	private FXGraph graph;
	private GraphChart graphChart;
    private String infoLabel;
    private String infoProperty;
    private long min;
    private long max;

    private LinkedHashMap<String, Object> data = new LinkedHashMap<>();

    public GetChartInfoInGraphJob(String jobName, FXGraph graph, GraphChart chart, String label, String property) {
        super(jobName);
        setUser(true);
        this.graph = graph;
        this.graphChart = chart;
        this.infoLabel = label;
        this.infoProperty = property;
    }

    @Override
    protected IStatus run(DBRProgressMonitor monitor) {

        if (graph == null) {
            return Status.CANCEL_STATUS;
        }
        
        if (graphChart == null) {
        	return Status.CANCEL_STATUS;
        }

        graphChart.UILock();
        
        monitor.beginTask("Update GraphDB Type info", 1);
        try {
            String[] temp;
            long count = 0;
            min = 0;
            max = 0;
            data.clear();
            List<Long> valList = getMinMaxInfo(monitor);
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
        	graphChart.UIUnLock();
            monitor.done();
        }
        return Status.OK_STATUS;
    }

    private List<Long> getMinMaxInfo(DBRProgressMonitor monitor) {
        ArrayList<String> nodeList = graph.getDataModel().getNodeLabelList(infoLabel);
        ArrayList<Long> valList = new ArrayList<>();
        Iterator<String> list = nodeList.iterator();
        
        try {
	        boolean first = true;
	        while (list.hasNext()) {
	        	CypherNode node = graph.getDataModel().getNode(list.next()).element();
	        	if (node != null) { 
	        		Object temp = node.getProperty(infoProperty);
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
	        }
        } catch (Exception e) {
			e.printStackTrace();
		}
        
        calcStep(min, max);
        
        return valList;
    }

    public long getNumofStepInfo(List<Long> valList, long fromVal, long toVal) {
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

    private void calcStep(long min, long max) {
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
}