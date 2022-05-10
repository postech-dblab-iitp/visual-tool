package org.jkiss.dbeaver.ext.turbographpp.model;

import org.jkiss.dbeaver.model.DBIcon;
import org.jkiss.dbeaver.model.DBPImage;
import org.jkiss.dbeaver.model.impl.struct.AbstractObjectType;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.struct.DBSObjectType;
import org.jkiss.dbeaver.model.struct.rdb.*;

public class GraphDBObjectType extends AbstractObjectType {
    public static final DBSObjectType TYPE_EDGE = new GraphDBObjectType("Edge", "Edge", DBIcon.TREE_TABLE,
            DBSTable.class);
    public static final DBSObjectType TYPE_PROPERTY = new GraphDBObjectType("EdgeProperty", "EdgeProperty",
            DBIcon.TREE_TABLE, DBSTable.class);

    private GraphDBObjectType(String typeName, String description, DBPImage image,
            Class<? extends DBSObject> objectClass) {
        super(typeName, description, image, objectClass);
    }
}
