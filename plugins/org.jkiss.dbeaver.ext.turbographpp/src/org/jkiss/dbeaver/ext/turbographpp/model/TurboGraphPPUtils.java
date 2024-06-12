/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2021 DBeaver Corp and others
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
package org.jkiss.dbeaver.ext.turbographpp.model;

import org.jkiss.dbeaver.ext.generic.model.GenericUtils;
import org.jkiss.dbeaver.ext.generic.model.meta.GenericMetaColumn;
import org.jkiss.dbeaver.ext.generic.model.meta.GenericMetaObject;

public class TurboGraphPPUtils extends GenericUtils {

    public static Object getColumn(
            TurboGraphPPDataSource dataSource, String objectType, String columnId) {
        GenericMetaObject object = dataSource.getMetaObject(objectType);
        if (object == null) {
            return columnId;
        }
        GenericMetaColumn column = object.getColumn(columnId);
        if (column == null || !column.isSupported()) {
            return columnId;
        }
        return column.getColumnIdentifier();
    }
}
