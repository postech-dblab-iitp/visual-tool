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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

public class CalculateStep {

    public static void calcStep(
            Object minVal, Object maxVal, LinkedHashMap<String, Long> stepData) {
        String simpleType = minVal.getClass().getSimpleName();
        String minString = String.valueOf(minVal);
        String maxString = String.valueOf(maxVal);
        if (simpleType.equals("Long") || simpleType.equals("Integer")) {
            long min = Long.valueOf(minString);
            long max = Long.valueOf(maxString);
            calcStep(min, max, stepData);
        } else if (simpleType.equals("Double") || simpleType.equals("BigDecimal")) {
            double min = Double.valueOf(minString);
            double max = Double.valueOf(maxString);
            BigDecimal bigMin = BigDecimal.valueOf(min);
            BigDecimal bigMax = BigDecimal.valueOf(max);
            calcStep(bigMin, bigMax, stepData);
        } else if (simpleType.equals("Date")) {
            Date min = Date.valueOf(minString);
            Date max = Date.valueOf(maxString);
            calcStep(min, max, stepData);
        }
    }

    protected static void calcStep(Date min, Date max, LinkedHashMap<String, Long> stepData) {
        long minTime = min.getTime();
        long maxTime = max.getTime();
        long step = (maxTime - minTime) / GraphChart.MAX_STEP;
        final long day = 1000 * 60 * 60 * 24;

        if (step > 0) {
            for (int i = 0; i < GraphChart.MAX_STEP; i++) {
                long start = minTime + i * step;
                long end = start + step;
                if (i > 0) {
                    start += day;
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String startStr = format.format(new Date(start));
                String endStr = format.format(new Date(end));
                stepData.put(startStr + GraphChart.STEP_RANGE_SEPARATOR + endStr, (long) 0);
            }
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String startStr = format.format(new Date(minTime));
            stepData.put(startStr, (long) 0);
        }
    }

    protected static void calcStep(
            BigDecimal min, BigDecimal max, LinkedHashMap<String, Long> stepData) {
        DecimalFormat df = new DecimalFormat("0.00");
        BigDecimal maxStep = max.subtract(min);

        if (maxStep.compareTo(BigDecimal.valueOf(0.1)) <= 0) {
            maxStep = BigDecimal.valueOf(1);
        } else {
            maxStep = BigDecimal.valueOf(GraphChart.MAX_STEP);
        }

        int count = maxStep.intValue();

        BigDecimal step = max.subtract(min).divide(maxStep);

        if (step.compareTo(BigDecimal.valueOf(0.01)) < 0) {
            step = BigDecimal.valueOf(0.01);
        }

        for (int i = 0; i < count; i++) {
            BigDecimal start = min.add(BigDecimal.valueOf(i).multiply(step));
            BigDecimal end = start.add(step);

            stepData.put(
                    df.format(start) + GraphChart.STEP_RANGE_SEPARATOR + df.format(end), (long) 0);

            if (end.compareTo(max) >= 0) {
                break;
            }
        }
    }

    protected static void calcStep(long min, long max, LinkedHashMap<String, Long> stepData) {
        long maxStep = max - min;
        if (maxStep >= GraphChart.MAX_STEP) {
            maxStep = GraphChart.MAX_STEP;
        }

        if (max - min == 0) {
            stepData.put("0", (long) 0);
            return;
        }

        long step = (max - min) / maxStep;
        long remainder = (max - min) % maxStep;
        if (remainder > 5) {
            step += 1;
        }

        for (long i = 0; i < maxStep; i++) {
            long start = min + i * step;
            long end = start + step;

            if (maxStep < GraphChart.MAX_STEP) {
                end = start;
            } else {
                if (i > 0) {
                    start += 1;
                }
            }

            if (end > max) {
                end = max;
            }

            stepData.put(start + GraphChart.STEP_RANGE_SEPARATOR + end, (long) 0);
        }
    }
}
