package org.jkiss.dbeaver.ext.turbographpp.graph.chart;

import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

    protected DBPDataSource dataSource;
    protected GraphChart graphChart;
    protected String infoLabel;
    protected String infoProperty;
    private Object min = 0;
    private Object max = 0;

    private LinkedHashMap<String, Object> stepData = new LinkedHashMap<>();

    private List<String> retVars = new ArrayList<>();

    private final int MAX_STEP = 10;

    private final String STEP_RANGE_SEPARATOR = "~";

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
        this.dataSource = datasource;
        this.graphChart = chart;
        this.infoLabel = label;
        this.infoProperty = property;
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

            calcStep(min, max);

            int index = 0;

            for (String key : stepData.keySet()) {
                int step = 1;
                index = key.lastIndexOf(STEP_RANGE_SEPARATOR);
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
        // System.out.println(query);
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
            // System.out.println("not support");
            DBWorkbench.getPlatformUI().showError("Chart Error", "not support Type");
            return false;
        }

        // min = floor(retMin);
        // max = ceil(retMax);

        setMinMax(retMin, retMax);
        return true;
    }

    private long ceil(Object val) {
        String temp = String.valueOf(val);
        int index = temp.indexOf(".");

        if (index > 0) {
            long ret = Long.valueOf(temp.substring(0, index));
            if (ret < 0) {
                ret--;
            } else {
                ret++;
            }
            return ret;
        }

        return Long.valueOf(temp);
    }

    private long floor(Object val) {
        String temp = String.valueOf(val);
        int index = temp.indexOf(".");
        if (index > 0) {
            long ret = Long.valueOf(temp.substring(0, index));
            if (ret < 0) {
                ret++;
            } else {
                ret--;
            }
            return ret;
        }

        return Long.valueOf(temp);
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

        if (!dataSource.getInfo().getDriverName().contains("Neo4j")) {
            if (fromVal.indexOf("-") == 0) {
                fromVal = String.valueOf("0" + fromVal);
            }
            if (toVal.indexOf("-") == 0) {
                toVal = String.valueOf("0" + toVal);
            }
        }

        fromOperator = " >= ";
        toOperator = " <= ";

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("MATCH (n:" + infoLabel + ")");
        queryBuilder.append(" WHERE n." + infoProperty + fromOperator + fromVal);
        queryBuilder.append(" AND n." + infoProperty + toOperator + toVal);
        queryBuilder.append(" RETURN COUNT(*) AS node_count");

        String query = queryBuilder.toString();
        // System.out.println(query);
        try (JDBCSession session =
                DBUtils.openMetaSession(monitor, dataSource, "Get Min, Max Value")) {
            JDBCPreparedStatement dbStat = session.prepareStatement(query);
            try (JDBCResultSet dbResult = dbStat.executeQuery()) {
                while (dbResult.next()) {
                    count = JDBCUtils.safeGetLong(dbResult, 1);
                    // System.out.println("getNumofStepInfo count : " + count);
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

        if (!dataSource.getInfo().getDriverName().contains("Neo4j")) {
            return true;
        }

        for (int i = 0; i < SUPPORT_MIN_MAX_TYPE_STRING_NEO4J.length; i++) {
            if (SUPPORT_MIN_MAX_TYPE_STRING_NEO4J[i].equals(simpleTypeName)) {
                return true;
            }
        }
        return false;
    }

    protected void calcStep(Object minVal, Object maxVal) {
        String simpleType = minVal.getClass().getSimpleName();
        String minString = String.valueOf(minVal);
        String maxString = String.valueOf(maxVal);
        if (simpleType.equals("Long") || simpleType.equals("Integer")) {
            long min = Long.valueOf(minString);
            long max = Long.valueOf(maxString);
            calcStep(min, max);
        } else if (simpleType.equals("Double") || simpleType.equals("BigDecimal")) {
            double min = Double.valueOf(minString);
            double max = Double.valueOf(maxString);
            calcStep(min, max);
        } else if (simpleType.equals("Date")) {
            Date min = Date.valueOf(minString);
            Date max = Date.valueOf(maxString);
            calcStep(min, max);
        }
    }

    protected void calcStep(Date min, Date max) {
        long minTime = min.getTime();
        long maxTime = max.getTime();
        long step = (maxTime - minTime) / 10;
        final long day = 1000 * 60 * 60 * 24;

        if (step > 0) {
            for (int i = 0; i < 10; i++) {
                long start = minTime + i * step;
                long end = start + step;
                if (i > 0) {
                    start += day;
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String startStr = format.format(new Date(start));
                String endStr = format.format(new Date(end));
                stepData.put(startStr + STEP_RANGE_SEPARATOR + endStr, 0);
            }
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String startStr = format.format(new Date(minTime));
            stepData.put(startStr, 0);
        }
    }

    protected void calcStep(double min, double max) {
        double maxStep;
        if (max - min < MAX_STEP) {
            maxStep = max - min;
        } else {
            maxStep = MAX_STEP;
        }
        double step = (max - min) / maxStep;
        DecimalFormat df = new DecimalFormat("0.00");
        for (int i = 0; i < maxStep; i++) {
            double start = min + i * step;
            double end = start + step;
            if (i > 0) {
                start += 0.01;
            }

            stepData.put(df.format(start) + STEP_RANGE_SEPARATOR + df.format(end), 0);
        }
    }

    protected void calcStep(long min, long max) {
        long maxStep;
        if (max - min < MAX_STEP) {
            maxStep = max - min;
        } else {
            maxStep = MAX_STEP;
        }
        long step = (max - min) / maxStep;
        for (long i = 0; i < maxStep; i++) {
            long start = min + i * step;
            long end = start + step;
            if (i > 0) {
                start += 1;
            }
            stepData.put(start + STEP_RANGE_SEPARATOR + end, 0);
        }
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
