package org.jkiss.dbeaver.ext.turbographpp.ui.views;

import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.jkiss.dbeaver.ext.turbographpp.model.plan.TurboGraphPPExecutionPlan;
import org.jkiss.dbeaver.model.exec.plan.DBCPlan;
import org.jkiss.dbeaver.model.sql.SQLQuery;
import org.jkiss.dbeaver.ui.editors.sql.plan.simple.SQLPlanViewProviderSimple;

public class TurboGraphPPPlanViewProvider extends SQLPlanViewProviderSimple {

    
    @Override
    public Viewer createPlanViewer(IWorkbenchPart workbenchPart, Composite parent) {
        TurboGraphPlanText treeViewer = new TurboGraphPlanText(workbenchPart, parent);
        return treeViewer;
    }

    @Override
    public void visualizeQueryPlan(Viewer viewer, SQLQuery query, DBCPlan plan) {        
        query.setText(((TurboGraphPPExecutionPlan) plan).getPlanQueryString());
        fillPlan(query, plan);
        showPlan(viewer, query, plan);
    }

    @Override
    public void contributeActions(Viewer viewer, IContributionManager contributionManager, SQLQuery lastQuery, DBCPlan lastPlan) {
     
    }

    @Override
    protected void showPlan(Viewer viewer, SQLQuery query, DBCPlan plan) {
        TurboGraphPlanText treeViewer = (TurboGraphPlanText) viewer;
        treeViewer.showPlan(query, plan);
    }
}