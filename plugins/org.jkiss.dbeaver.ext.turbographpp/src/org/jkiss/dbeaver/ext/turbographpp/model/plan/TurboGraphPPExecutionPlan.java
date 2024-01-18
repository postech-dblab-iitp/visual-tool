package org.jkiss.dbeaver.ext.turbographpp.model.plan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPDataSource;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.exec.plan.DBCPlanNode;
import org.jkiss.dbeaver.model.impl.plan.AbstractExecutionPlan;
import org.jkiss.utils.CommonUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TurboGraphPPExecutionPlan extends AbstractExecutionPlan {

    protected TurboGraphPPDataSource dataSource;
    protected String query;
    protected String plan;

    private List<TurboGraphPPPlanNodePlain> rootNodes = null;

    private static final Gson gson = new Gson();

    public TurboGraphPPExecutionPlan(JDBCSession session, String query) throws DBCException {
        this.dataSource = (TurboGraphPPDataSource) session.getDataSource();
        this.query = query;

        try {

            TurboGraphPPStatementProxy proxy = new TurboGraphPPStatementProxy(session.getOriginal().createStatement());

            plan = proxy.getQueryplan(query);
            String jsonPlan;

            // delete first("plan :[") and last("]")
            int idx = 0;
            idx = plan.indexOf("[");
            jsonPlan = plan.substring(idx + 1);
            idx = jsonPlan.lastIndexOf("]");
            jsonPlan = jsonPlan.substring(0, idx);

            List<TurboGraphPPPlanNodePlain> nodes = new ArrayList<>();

            JsonObject planObject = gson.fromJson(jsonPlan, JsonObject.class);
            JsonObject queryBlock = planObject.getAsJsonObject("Plan");

            TurboGraphPPPlanNodePlain rootNode = new TurboGraphPPPlanNodePlain(null, "match", queryBlock);

            if (CommonUtils.isEmpty(rootNode.getNested()) && rootNode.getProperty("message") != null) {
                throw new DBCException("Can't explain plan: " + rootNode.getProperty("message"));
            }
            nodes.add(rootNode);

            rootNodes = nodes;

        } catch (SQLException e) {
            throw new DBCException(e, session.getExecutionContext());
        }
    }

    @Override
    public String getQueryString() {
        return query;
    }

    @Override
    public String getPlanQueryString() {
        return plan;
    }

    @Override
    public List<? extends DBCPlanNode> getPlanNodes(Map<String, Object> options) {
        return rootNodes;
    }

}
