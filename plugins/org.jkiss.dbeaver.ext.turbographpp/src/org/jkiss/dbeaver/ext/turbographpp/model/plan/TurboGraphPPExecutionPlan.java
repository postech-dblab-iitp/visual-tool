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

public class TurboGraphPPExecutionPlan extends AbstractExecutionPlan {

    protected TurboGraphPPDataSource dataSource;
    protected String query;
    protected String plan;

    private List<TurboGraphPPPlanNodePlain> rootNodes = null;

    public TurboGraphPPExecutionPlan(JDBCSession session, String query) throws DBCException {
        this.dataSource = (TurboGraphPPDataSource) session.getDataSource();
        this.query = query;

        try {

            TurboGraphPPStatementProxy proxy =
                    new TurboGraphPPStatementProxy(session.getOriginal().createStatement());

            plan = proxy.getQueryplan(query);

            String[] plans = plan.split("plan : ");
            List<TurboGraphPPPlanNodePlain> nodes = new ArrayList<>();
            TurboGraphPPPlanNodePlain rootNode;
            for (int i = 0; i < plans.length; i++) {
                if (plans[i] != null && !plans[i].equals("")) {
                    System.out.println("-------------------------------");
                    System.out.println(plans[i]);
                    rootNode = new TurboGraphPPPlanNodePlain(null, "plan : ", plans[i]);
                    if (CommonUtils.isEmpty(rootNode.getNested())
                            && rootNode.getProperty("message") != null) {
                        throw new DBCException(
                                "Can't explain plan: " + rootNode.getProperty("message"));
                    }
                    nodes.add(rootNode);
                }
            }

            if (nodes.size() > 0) {
                rootNodes = nodes;
            }
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
