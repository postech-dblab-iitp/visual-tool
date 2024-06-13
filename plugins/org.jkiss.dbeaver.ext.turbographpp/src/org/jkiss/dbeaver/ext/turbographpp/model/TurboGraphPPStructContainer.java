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

import java.util.List;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObjectContainer;

public interface TurboGraphPPStructContainer extends DBSObjectContainer {

    @NotNull
    @Override
    TurboGraphPPDataSource getDataSource();

    TurboGraphPPStructContainer getObject();

    TableCache getTableCache();

    List<? extends TurboGraphPPTable> getPhysicalNode(DBRProgressMonitor monitor)
            throws DBException;

    List<? extends TurboGraphPPTableBase> getTables(DBRProgressMonitor monitor) throws DBException;

    TurboGraphPPTableBase getTable(DBRProgressMonitor monitor, String name) throws DBException;

    List<? extends TurboGraphPPView> getPhysicalEdge(DBRProgressMonitor monitor) throws DBException;
}
