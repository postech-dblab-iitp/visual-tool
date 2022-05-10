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
package org.jkiss.dbeaver.ext.turbographpp;

import java.util.Collection;
import java.util.List;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.generic.GenericConstants;
import org.jkiss.dbeaver.ext.generic.model.GenericCatalog;
import org.jkiss.dbeaver.ext.generic.model.GenericSchema;
import org.jkiss.dbeaver.ext.turbographpp.model.TableCache;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPDataSource;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPStructContainer;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPTable;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPTableBase;
import org.jkiss.dbeaver.ext.turbographpp.model.meta.TurboGraphPPMetaModel;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.connection.DBPConnectionConfiguration;
import org.jkiss.dbeaver.model.connection.DBPDriver;
import org.jkiss.dbeaver.model.impl.PropertyDescriptor;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCDataSourceProvider;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCURL;
import org.jkiss.dbeaver.model.messages.ModelMessages;
import org.jkiss.dbeaver.model.preferences.DBPPropertyDescriptor;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.utils.CommonUtils;

public class TurboGraphPPDataSourceProvider extends JDBCDataSourceProvider {

    public TurboGraphPPDataSourceProvider() {

    }

    @Override
    public long getFeatures() {
        return FEATURE_NONE;
    }

    @Override
    public String getConnectionURL(DBPDriver driver, DBPConnectionConfiguration connectionInfo) {
        return JDBCURL.generateUrlByTemplate(driver, connectionInfo);
    }

    @NotNull
    public DBPDataSource openDataSource(@NotNull DBRProgressMonitor monitor, @NotNull DBPDataSourceContainer container)
            throws DBException {
        return new TurboGraphPPDataSource(monitor, container, new TurboGraphPPMetaModel());
    };

    @Override
    public DBPPropertyDescriptor[] getConnectionProperties(DBRProgressMonitor monitor, DBPDriver driver,
            DBPConnectionConfiguration connectionInfo) throws DBException {
        DBPPropertyDescriptor[] connectionProperties = super.getConnectionProperties(monitor, driver, connectionInfo);
        if (connectionProperties == null || connectionProperties.length == 0) {
            // Try to get list of supported properties from custom driver config
            String driverParametersString = CommonUtils
                    .toString(driver.getDriverParameter(GenericConstants.PARAM_DRIVER_PROPERTIES));
            if (!driverParametersString.isEmpty()) {
                String[] propList = driverParametersString.split(",");
                connectionProperties = new DBPPropertyDescriptor[propList.length];
                for (int i = 0; i < propList.length; i++) {
                    String propName = propList[i].trim();
                    connectionProperties[i] = new PropertyDescriptor(ModelMessages.model_jdbc_driver_properties,
                            propName, propName, null, String.class, false, null, null, false);
                }
            }
        }
        return connectionProperties;
    }
}
