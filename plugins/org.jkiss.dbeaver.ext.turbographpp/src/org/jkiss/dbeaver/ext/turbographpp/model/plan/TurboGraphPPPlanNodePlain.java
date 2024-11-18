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
package org.jkiss.dbeaver.ext.turbographpp.model.plan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.model.impl.plan.AbstractExecutionPlanNode;
import org.jkiss.dbeaver.model.meta.Property;

public class TurboGraphPPPlanNodePlain extends AbstractExecutionPlanNode {

    private TurboGraphPPPlanNodePlain parent;
    private String type;
    private String name;
    private String plan;
    private int blankCount;
    private Map<String, String> nodeProps = new LinkedHashMap<>();
    private List<TurboGraphPPPlanNodePlain> nested = new ArrayList<>();

    public TurboGraphPPPlanNodePlain(TurboGraphPPPlanNodePlain parent, String type, String plan) {
        this.parent = parent;
        this.type = type;
        this.name = "";
        this.plan = plan;

        try {
            parsePlan(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TurboGraphPPPlanNodePlain(
            TurboGraphPPPlanNodePlain parent, Map<String, String> attributes) {
        this.parent = parent;
        this.nodeProps.putAll(attributes);
    }

    public Map<String, String> getNodeProps() {
        return nodeProps;
    }

    private void parsePlan(String objName) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(plan));
        String subPlans = "";
        List<String> parentPlans = new ArrayList<>();
        boolean isFirst = true;
        int lineCount = 0;
        String line;

        while ((line = reader.readLine()) != null) {
            if (isFirst) {
                blankCount = getBlankCount(line);
                addNodeProps(line);
                isFirst = false;
            } else {
                lineCount = getBlankCount(line);
                if (blankCount == lineCount) {
                    parentPlans.add(line);
                } else {
                    int size = parentPlans.size();
                    if (size > 0) {
                        String plan = parentPlans.get(size - 1) + System.lineSeparator() + line;
                        parentPlans.remove(size - 1);
                        parentPlans.add(plan);
                    } else {
                        subPlans = subPlans + line + System.lineSeparator();
                    }
                }
            }
        }
        if (!subPlans.isEmpty()) {
            addThisNested("", subPlans);
        }

        Iterator<String> it = parentPlans.iterator();
        while (it.hasNext()) {
            addParentNested("", it.next());
        }
    }

    @NotNull
    private void addNodeProps(String subplan) {
        if (subplan != null && !subplan.equals("") && subplan.indexOf("(") > 0) {
            name = subplan.substring(0, subplan.indexOf("(")).trim();
            String value = subplan.substring(subplan.indexOf("("), subplan.length());
            value = value.replace("(", "");
            String[] values = value.split("\\)");

            String[] itemsValue = values[0].split(",");
            
            if (itemsValue.length != 2) {
                nodeProps.put("extra", value.replace(")", ""));
                type = type + name;
                return;
            }
            
            for (int i = 0; i < itemsValue.length; i++) {
                String[] items = itemsValue[i].split(":");
                if (items.length == 2) {
                    nodeProps.put(items[0].trim(), items[1].trim());
                }
            }

            String extra = "";
            for (int i = 1; i < values.length; i++) {
                extra = extra + values[i];
            }
            nodeProps.put("extra", extra);
        } else {
            name = subplan.trim();
        }

        type = type + name;
    }

    private void addThisNested(String name, String plan) {
        if (nested == null) {
            nested = new ArrayList<>();
        }
        if (!plan.isEmpty()) {
            nested.add(new TurboGraphPPPlanNodePlain(this, name, plan));
        }
    }

    private void addParentNested(String name, String plan) {
        if (parent.nested == null) {
            parent.nested = new ArrayList<>();
        }
        if (!plan.isEmpty()) {
            this.parent.nested.add(new TurboGraphPPPlanNodePlain(parent, name, plan));
        }
    }

    @Property(order = 0, viewable = true)
    @Override
    public String getNodeType() {
        return type = type.replace("->", "").trim();
    }

    @Property(order = 14, viewable = false)
    @Override
    public String getNodeName() {
        return "";
    }

    @Property(order = 11, viewable = true)
    public String getRow() {
        String value = nodeProps.get("rows");
        return value == null ? null : value;
    }

    @Property(order = 12, viewable = true)
    public String getTime() {
        String value = nodeProps.get("time");
        return value == null ? null : value;
    }

    @Property(order = 13, viewable = true)
    public String getExtra() {
        String value = nodeProps.get("extra");
        return value == null ? null : value;
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
        return plan == null ? nodeProps.toString() : plan.toString();
    }

    private static int getBlankCount(String str) {
        StringBuilder sb = new StringBuilder(str);
        int i = 0;
        while (i < sb.length() && sb.charAt(i) == ' ') {
            i++;
        }
        return i;
    }
}
