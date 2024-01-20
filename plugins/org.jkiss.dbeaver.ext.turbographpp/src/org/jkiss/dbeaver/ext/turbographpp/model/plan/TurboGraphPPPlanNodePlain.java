package org.jkiss.dbeaver.ext.turbographpp.model.plan;

import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.exec.plan.DBCPlanCostNode;
import org.jkiss.dbeaver.model.impl.PropertyDescriptor;
import org.jkiss.dbeaver.model.impl.plan.AbstractExecutionPlanNode;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.preferences.DBPPropertyDescriptor;
import org.jkiss.dbeaver.model.preferences.DBPPropertySource;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TurboGraphPPPlanNodePlain extends AbstractExecutionPlanNode implements DBCPlanCostNode, DBPPropertySource{

    private TurboGraphPPPlanNodePlain parent;
    private String name;
    private JsonObject object;
    private Map<String, String> nodeProps = new LinkedHashMap<>();
    private List<TurboGraphPPPlanNodePlain> nested = new ArrayList<>();

    public TurboGraphPPPlanNodePlain(TurboGraphPPPlanNodePlain parent, String name, JsonObject object) {
        this.parent = parent;
        this.name = name;
        this.object = object;

        parseObject(name, object);
    }

    public TurboGraphPPPlanNodePlain(TurboGraphPPPlanNodePlain parent,  Map<String, String> attributes) {
        this.parent = parent;
        this.nodeProps.putAll(attributes);
    }

    public Map<String, String> getNodeProps() {
        return nodeProps;
    }

    private void parseObject(String objName, JsonObject object) {
        for (Map.Entry<String, JsonElement> prop : object.entrySet()) {
            String propName = prop.getKey();
            JsonElement value = prop.getValue();
            if (value instanceof JsonPrimitive) {
                if ("Node Type".equals(propName)) {
                    this.name =  String.valueOf(value);
                    nodeProps.put(propName, value.toString());
                } 
            } else if (value instanceof JsonArray) {
                boolean isProp = false;
                int itemIndex = 0;
                for (JsonElement item : (JsonArray) value) {
                    if (item instanceof JsonObject) {
                        itemIndex++;
                        addNested(propName + "#" + itemIndex, (JsonObject) item);
                    } else {
                        isProp = true;
                        break;
                    }
                }
                if (isProp) {
                    nodeProps.put(propName, value.toString());
                }
            } else {
                nodeProps.put(propName, value.getAsString());
            }
        }
    }

    private void addNested(String name, JsonObject value) {
        if (nested == null) {
            nested = new ArrayList<>();
        }
        nested.add(
            new TurboGraphPPPlanNodePlain(this, name, value)
        );
    }

    @Property(order = 0, viewable = true)
    @Override
    public String getNodeType() {
        return name;
    }

    @Property(order = 1, viewable = true)
    @Override
    public String getNodeName() {
//        Object nodeName = nodeProps.get("node_name");
//        return nodeName == null ? null : String.valueOf(nodeName);
        return "";
    }

    @Property(order = 10, viewable = true)
    @Override
    public Number getNodeCost() {
//        Object readCost = nodeProps.get("cost");
//        return CommonUtils.toDouble(readCost);
        return 0;
    }

    @Override
    public Number getNodePercent() {
        return null;
    }

    @Override
    public Number getNodeDuration() {
        return null;
    }

    @Property(order = 11, viewable = true)
    @Override
    public Number getNodeRowCount() {
//        Object rowCount = nodeProps.get("rows");
//        return rowCount == null ? null : CommonUtils.toLong(rowCount);
        return 0;
    }

    @Override
    public TurboGraphPPPlanNodePlain getParent() {
        return parent;
    }

    @Override
    public Collection<TurboGraphPPPlanNodePlain> getNested() {
        return nested;
    }

    public Object getProperty(String name) {
        return nodeProps.get(name);
    }

    @Override
    public String toString() {
        return object == null ? nodeProps.toString() : object.toString();
    }

    //////////////////////////////////////////////////////////
    // Properties

    @Override
    public Object getEditableValue() {
        return this;
    }

    @Override
    public DBPPropertyDescriptor[] getProperties() {
        DBPPropertyDescriptor[] props = new DBPPropertyDescriptor[nodeProps.size()];
        int index = 0;
        for (Map.Entry<String, String> attr : nodeProps.entrySet()) {
            props[index++] = new PropertyDescriptor(
                "Details",
                attr.getKey(),
                attr.getKey(),
                null,
                String.class,
                false,
                null,
                null,
                false);
        }
        return props;
    }

    @Override
    public Object getPropertyValue(@Nullable DBRProgressMonitor monitor, String id) {
        return nodeProps.get(id.toString());
    }

    @Override
    public boolean isPropertySet(String id) {
        return false;
    }

    @Override
    public boolean isPropertyResettable(String id) {
        return false;
    }

    @Override
    public void resetPropertyValue(@Nullable DBRProgressMonitor monitor, String id) {

    }

    @Override
    public void resetPropertyValueToDefault(String id) {

    }

    @Override
    public void setPropertyValue(@Nullable DBRProgressMonitor monitor, String id, Object value) {

    }

}
