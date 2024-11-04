/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2024 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ext.turbographpp.graph.chart;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jkiss.dbeaver.ext.turbographpp.graph.FXGraph;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherNode;
import org.jkiss.dbeaver.model.runtime.AbstractJob;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;

public class GetChartInfoInGraphJob extends AbstractJob {
    private FXGraph graph;
    private GraphChart graphChart;
    private String infoLabel;
    private String infoProperty;
    private Object min;
    private Object max;

    private LinkedHashMap<String, Long> data = new LinkedHashMap<>();

    public GetChartInfoInGraphJob(
            String jobName, FXGraph graph, GraphChart chart, String label, String property) {
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
            data.clear();
            createChartData();
            graphChart.runUpdateChart(data);
        } finally {
            graphChart.UIUnLock();
            monitor.done();
        }
        return Status.OK_STATUS;
    }

    private void createChartData() {
        ArrayList<Long> longValList = new ArrayList<>();
        ArrayList<BigDecimal> decimalValList = new ArrayList<>();
        ArrayList<Date> dateValList = new ArrayList<>();

        ArrayList<String> nodeList = graph.getDataModel().getNodeLabelList(infoLabel);
        Iterator<String> list = nodeList.iterator();

        long longMin = 0, longmax = 0;
        BigDecimal decimalMin = BigDecimal.valueOf(0), decimalMax = BigDecimal.valueOf(0);
        Date dateMin = Date.valueOf("1111-11-11"), dateMax = Date.valueOf("1111-11-11");

        try {
            boolean first = true;
            while (list.hasNext()) {
                CypherNode node = graph.getDataModel().getNode(list.next()).element();
                if (node != null) {
                    Object objectVal = node.getProperty(infoProperty);
                    if (objectVal != null) {
                        String stringVal = String.valueOf(objectVal);
                        if (objectVal instanceof Long || objectVal instanceof Integer) {
                            long longVal = Long.valueOf(stringVal);
                            longValList.add(longVal);
                            if (first) {
                                min = max = longMin = longmax = longVal;
                                first = false;
                            }
                            if (longMin > longVal) {
                                min = longMin = longVal;
                            }

                            if (longmax < longVal) {
                                max = longmax = longVal;
                            }
                        } else if (objectVal instanceof BigDecimal || objectVal instanceof Double) {
                            BigDecimal decimalVal = BigDecimal.valueOf(Double.valueOf(stringVal));
                            decimalValList.add(decimalVal);
                            if (first) {
                                min = max = decimalMin = decimalMax = decimalVal;
                                first = false;
                            }
                            if (decimalMin.compareTo(decimalVal) > 0) {
                                min = decimalMin = decimalVal;
                            }

                            if (decimalMax.compareTo(decimalVal) < 0) {
                                max = decimalMax = decimalVal;
                            }
                        } else if (objectVal instanceof Date) {
                            Date dateVal = Date.valueOf(stringVal);
                            dateValList.add(dateVal);
                            if (first) {
                                min = max = dateMin = dateMax = dateVal;
                                first = false;
                            }
                            if (dateMin.compareTo(dateVal) > 0) {
                                min = dateMin = dateVal;
                            }

                            if (dateMax.compareTo(dateVal) < 0) {
                                max = dateMax = dateVal;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        CalculateStep.calcStep(min, max, data);

        int step = 1;
        String[] temp;
        long count = 0;
        String stepMin, stepMax;

        for (String key : data.keySet()) {
            if (key.contains(GraphChart.STEP_RANGE_SEPARATOR)) {
                temp = key.split(GraphChart.STEP_RANGE_SEPARATOR);
                stepMin = temp[0];
                stepMax = temp[1];
            } else {
                stepMin = stepMax = key;
            }

            if (!longValList.isEmpty()) {
                count =
                        getNumofStepInfo(
                                longValList, Long.valueOf(stepMin), Long.valueOf(stepMax), step);
            } else if (!decimalValList.isEmpty()) {
                count =
                        getNumofStepInfo(
                                decimalValList,
                                BigDecimal.valueOf(Double.valueOf(stepMin)),
                                BigDecimal.valueOf(Double.valueOf(stepMax)),
                                step);
            } else if (!dateValList.isEmpty()) {
                count =
                        getNumofStepInfo(
                                dateValList, Date.valueOf(stepMin), Date.valueOf(stepMax), step);
            }

            data.put(key, count);
            step++;
        }
    }

    private long getNumofStepInfo(List<Long> valList, Long fromVal, Long toVal, int step) {
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

    private long getNumofStepInfo(
            List<BigDecimal> valList, BigDecimal fromVal, BigDecimal toVal, int step) {
        long result = 0;
        Iterator<BigDecimal> itr = valList.iterator();
        while (itr.hasNext()) {
            BigDecimal val = itr.next();
            if (step == 1) {
                if (fromVal.compareTo(val) <= 0 && toVal.compareTo(val) >= 0) {
                    result++;
                    itr.remove();
                }
            } else {
                if (fromVal.compareTo(val) < 0 && toVal.compareTo(val) >= 0) {
                    result++;
                    itr.remove();
                }
            }
        }

        return result;
    }

    private long getNumofStepInfo(List<Date> valList, Date fromVal, Date toVal, int step) {
        long result = 0;
        Iterator<Date> itr = valList.iterator();
        while (itr.hasNext()) {
            Date val = itr.next();
            if (fromVal.compareTo(val) <= 0 && toVal.compareTo(val) >= 0) {
                result++;
                itr.remove();
            }
        }

        return result;
    }
}
