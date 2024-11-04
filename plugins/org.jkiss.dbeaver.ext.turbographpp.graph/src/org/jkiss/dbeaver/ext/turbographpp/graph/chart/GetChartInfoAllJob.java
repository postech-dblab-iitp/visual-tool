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

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPDataSource;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.runtime.AbstractJob;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.runtime.DBWorkbench;

public class GetChartInfoAllJob extends AbstractJob {

    protected TurboGraphPPDataSource dataSource;
    protected boolean isTurboGraph = false;

    protected GraphChart graphChart;
    protected String infoLabel;
    protected String infoProperty;
    private Object min = 0;
    private Object max = 0;

    private LinkedHashMap<String, Long> stepData = new LinkedHashMap<>();

    private static final String[] SUPPORT_MIN_MAX_TYPE_STRING_NEO4J =
            new String[] {"Double", "Long", "Date"};

    public GetChartInfoAllJob(
            String jobName,
            DBPDataSource datasource,
            GraphChart chart,
            String label,
            String property) {
        super(jobName);
        setUser(true);
        this.dataSource = (TurboGraphPPDataSource) datasource;
        this.graphChart = chart;
        this.infoLabel = label;
        this.infoProperty = property;
        this.isTurboGraph = this.dataSource.isTurboGraph();
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
            stepData.clear();

            graphChart.UILock();

            List<Long> valList = new ArrayList<>();
            min = 0;
            max = 0;

            boolean ret = getMinMaxInfo(monitor);

            if (!ret) {
                return Status.CANCEL_STATUS;
            }

            CalculateStep.calcStep(min, max, stepData);

            int index = 0;
            int step = 1;

            for (String key : stepData.keySet()) {
                index = key.lastIndexOf(GraphChart.STEP_RANGE_SEPARATOR);
                if (index > 0) {
                    temp = new String[2];
                    temp[0] = key.substring(0, index);
                    temp[1] = key.substring(index + 1, key.length());
                    count = getNumofStepInfo(monitor, stepData.size(), temp[0], temp[1], step);
                    stepData.put(key, count);
                } else {
                    count = getNumofStepInfo(monitor, stepData.size(), key, key, step);
                    stepData.put(key, count);
                }
                step++;
            }
            graphChart.runUpdateChart(stepData);
        } finally {
            monitor.done();
            graphChart.UIUnLock();
        }

        return Status.OK_STATUS;
    }

    public boolean getMinMaxInfo(DBRProgressMonitor monitor) {
        // Object min = 0, max = 0;
        Object retMin = 0, retMax = 0;
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("MATCH (n:" + infoLabel + ")");
        queryBuilder.append(" WITH MIN(n." + infoProperty + ") as MINVALUE,");
        queryBuilder.append(" MAX(n." + infoProperty + ") as MAXVALUE");
        queryBuilder.append(" RETURN MINVALUE, MAXVALUE");

        String query = queryBuilder.toString();
        try (JDBCSession session =
                DBUtils.openMetaSession(monitor, dataSource, "Get Min, Max Value")) {
            JDBCPreparedStatement dbStat = session.prepareStatement(query);
            try (JDBCResultSet dbResult = dbStat.executeQuery()) {
                while (dbResult.next()) {
                    retMin = JDBCUtils.safeGetObject(dbResult, 1);
                    retMax = JDBCUtils.safeGetObject(dbResult, 2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (dbStat != null) dbStat.close();
            }
        } catch (DBCException | SQLException e) {
            e.printStackTrace();
        }

        if (!checkSupportType(retMin.getClass().getSimpleName())) {
            DBWorkbench.getPlatformUI().showError("Chart Error", "not support Type");
            return false;
        }

        setMinMax(retMin, retMax);
        return true;
    }

    protected long getNumofStepInfo(
            DBRProgressMonitor monitor, int size, String fromVal, String toVal, int step) {
        long count = 0;
        String fromOperator;
        String toOperator;

        if (fromVal.indexOf("-") > 0) {
            fromVal = "date('" + fromVal + "')";
            toVal = "date('" + toVal + "')";
        }

        if (isTurboGraph) {
            if (fromVal.indexOf("-") == 0) {
                fromVal = String.valueOf("0" + fromVal);
            }
            if (toVal.indexOf("-") == 0) {
                toVal = String.valueOf("0" + toVal);
            }
        }

        fromOperator = " >= ";
        toOperator = " <= ";

        if (fromVal.contains(".") && step > 1) {
            fromOperator = " > ";
        }

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("MATCH (n:" + infoLabel + ")");
        queryBuilder.append(" WHERE n." + infoProperty + fromOperator + fromVal);
        queryBuilder.append(" AND n." + infoProperty + toOperator + toVal);
        queryBuilder.append(" RETURN COUNT(*) AS node_count");

        String query = queryBuilder.toString();
        try (JDBCSession session =
                DBUtils.openMetaSession(monitor, dataSource, "Get Min, Max Value")) {
            JDBCPreparedStatement dbStat = session.prepareStatement(query);
            try (JDBCResultSet dbResult = dbStat.executeQuery()) {
                while (dbResult.next()) {
                    count = JDBCUtils.safeGetLong(dbResult, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (dbStat != null) dbStat.close();
            }
        } catch (DBCException | SQLException e) {
            e.printStackTrace();
            return 0;
        }

        return count;
    }

    private boolean checkSupportType(String simpleTypeName) {

        if (dataSource.isTurboGraph()) {
            return true;
        }

        for (int i = 0; i < SUPPORT_MIN_MAX_TYPE_STRING_NEO4J.length; i++) {
            if (SUPPORT_MIN_MAX_TYPE_STRING_NEO4J[i].equals(simpleTypeName)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> calculateDoubleIntervals(double min, double max, int numIntervals) {
        List<String> intervals = new ArrayList<>();
        double step = (max - min) / numIntervals;
        DecimalFormat df = new DecimalFormat("0.#####");

        for (int i = 0; i < numIntervals; i++) {
            double start = min + i * step;
            double end = start + step;
            if (i == numIntervals - 1) { // Ensure last interval ends exactly at max
                end = max;
            }
            intervals.add(df.format(start) + "-" + df.format(end));
        }
        return intervals;
    }

    protected void setMinMax(Object min, Object max) {
        this.min = min;
        this.max = max;
    }
}
