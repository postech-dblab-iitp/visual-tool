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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.DBPIdentifierCase;
import org.jkiss.dbeaver.model.DBPKeywordType;
import org.jkiss.dbeaver.model.impl.sql.BasicSQLDialect;
import org.jkiss.dbeaver.model.sql.SQLDialect;
import org.jkiss.dbeaver.model.sql.SQLStateType;

public class TurboPPSQLDialect extends BasicSQLDialect {

    public static final String TURBOGRAPHPP_DIALECT_ID = "turbographpp";

    private static final String[] CYPHER_KEYWORDS =
            new String[] {
                "ASC",
                "AND",
                "AS",
                "CREATE",
                "COUNT",
                "CALL",
                "COLLECT",
                "DELETE",
                "DISTINCT",
                "DESC",
                "EXISTS",
                "FOREACH",
                "LIMIT",
                "LOAD CSV",
                "MATCH",
                "MERGE",
                "NOT",
                "OPTIONAL MATCH",
                "OPTIONAL",
                "OR",
                "ORDER BY",
                "RETURN",
                "REMOVE",
                "SKIP",
                "SET",
                "UNWIND",
                "UNION",
                "USE",
                "WHERE",
                "WITH"
            };

    public static final String[] CYPHER_NEO4J_FUNCTION = {
        "collect", "count", "toInteger", "isEmpty", "shortestPath", "sum", "avg"
        // "FLOOR",
        // "LOWER",
        // "MAX",
        // "MIN",
        // "SQRT",
        // "SUBSTRING",
        // "TRIM",
        // "UPPER",
    };

    private static final String[] DDL_KEYWORDS =
            new String[] {"CREATE", "DELETE", "REMOVE", "SET", "MERGE"};

    private static final String[] QUERY_KEYWORDS =
            new String[] {"MATCH", "OPTIONAL MATCH", "MERGE"};

    private static final String[] DML_KEYWORDS = new String[] {"MATCH", "CREATE", "MERGE"};

    private static final String[] EXEC_KEYWORDS =
            new String[] {"CALL", "EXISTS", "COUNT", "COLLECT"};

    private static final String[] TABLE_KEYWORDS =
            new String[] {"MATCH", "OPTIONAL MATCH", "MERGE", "CREATE"};

    private static final String[] COLUMN_KEYWORDS =
            new String[] {"WHERE", "RETURN", "AND", "OR", "SET", "REMOVE"};

    private static final String[][] QUOTE_STRINGS = {};
    //    private static final String[][] QUOTE_STRINGS = {
    //            {"'", "'"},
    //            {"\"", "\""}
    //    };

    public TurboPPSQLDialect() {
        loadKeyword();
    }

    @Override
    public String getDialectId() {
        return TURBOGRAPHPP_DIALECT_ID;
    }

    @NotNull
    @Override
    public String getDialectName() {
        return "TurboGraph++";
    }

    @Nullable
    @Override
    public String[][] getIdentifierQuoteStrings() {
        return QUOTE_STRINGS;
    }

    @Override
    public String getQuotedString(String string) {
        return super.getQuotedString(string);
    }

    @NotNull
    @Override
    public SQLStateType getSQLStateType() {
        return SQLStateType.UNKNOWN;
    }

    @Override
    public boolean isStandardSQL() {
        return false;
    }

    @Override
    public String[] getDDLKeywords() {
        return DDL_KEYWORDS;
    }

    @Override
    public String[] getQueryKeywords() {
        return QUERY_KEYWORDS;
    }

    @Override
    public String[] getExecuteKeywords() {
        return EXEC_KEYWORDS;
    }

    @Override
    public String[] getDMLKeywords() {
        return DML_KEYWORDS;
    }

    private void loadKeyword() {
        Set<String> all = new HashSet<>();
        Collections.addAll(all, CYPHER_KEYWORDS);
        Collections.addAll(functions, CYPHER_NEO4J_FUNCTION);
        Collections.addAll(tableQueryWords, TABLE_KEYWORDS);
        Collections.addAll(columnQueryWords, COLUMN_KEYWORDS);

        for (String kw : all) {
            addSQLKeyword(kw);
            setKeywordIndent(kw, 1);
        }

        for (String kw : tableQueryWords) {
            setKeywordIndent(kw, 1);
        }

        for (String kw : columnQueryWords) {
            setKeywordIndent(kw, 1);
        }

        addKeywords(functions, DBPKeywordType.FUNCTION);
    }

    @Override
    public int getSQLType() {
        return SQLDialect.GQL_CYPHER;
    }

    @Override
    public boolean isDelimiterAfterQuery() {
        return true;
    }

    @Override
    public DBPIdentifierCase storesUnquotedCase() {
        return DBPIdentifierCase.MIXED;
    }
}
