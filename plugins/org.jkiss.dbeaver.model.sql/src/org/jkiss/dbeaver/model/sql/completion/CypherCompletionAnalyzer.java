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
package org.jkiss.dbeaver.model.sql.completion;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.DBPEvaluationContext;
import org.jkiss.dbeaver.model.DBPImage;
import org.jkiss.dbeaver.model.DBPKeywordType;
import org.jkiss.dbeaver.model.DBPNamedObject;
import org.jkiss.dbeaver.model.DBPObject;
import org.jkiss.dbeaver.model.DBPQualifiedObject;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.DBValueFormatting;
import org.jkiss.dbeaver.model.exec.DBCExecutionContext;
import org.jkiss.dbeaver.model.impl.DBObjectNameCaseTransformer;
import org.jkiss.dbeaver.model.navigator.DBNNode;
import org.jkiss.dbeaver.model.navigator.DBNUtils;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sql.SQLConstants;
import org.jkiss.dbeaver.model.sql.SQLDialect;
import org.jkiss.dbeaver.model.sql.SQLScriptElement;
import org.jkiss.dbeaver.model.sql.SQLSearchUtils;
import org.jkiss.dbeaver.model.sql.SQLSyntaxManager;
import org.jkiss.dbeaver.model.sql.SQLTableAliasInsertMode;
import org.jkiss.dbeaver.model.sql.SQLUtils;
import org.jkiss.dbeaver.model.sql.parser.SQLRuleManager;
import org.jkiss.dbeaver.model.sql.parser.SQLWordPartDetector;
import org.jkiss.dbeaver.model.sql.parser.tokens.SQLTokenType;
import org.jkiss.dbeaver.model.struct.DBSAlias;
import org.jkiss.dbeaver.model.struct.DBSAttributeBase;
import org.jkiss.dbeaver.model.struct.DBSEntity;
import org.jkiss.dbeaver.model.struct.DBSInstance;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.struct.DBSObjectContainer;
import org.jkiss.dbeaver.model.struct.DBSObjectFilter;
import org.jkiss.dbeaver.model.struct.DBSObjectReference;
import org.jkiss.dbeaver.model.struct.DBSObjectType;
import org.jkiss.dbeaver.model.struct.DBSStructureAssistant;
import org.jkiss.dbeaver.model.text.TextUtils;
import org.jkiss.dbeaver.model.text.parser.TPRuleBasedScanner;
import org.jkiss.dbeaver.model.text.parser.TPToken;
import org.jkiss.dbeaver.model.text.parser.TPTokenAbstract;
import org.jkiss.utils.CommonUtils;
import org.jkiss.utils.Pair;

public class CypherCompletionAnalyzer extends SQLCompletionAnalyzer {

    private static final Log log = Log.getLog(CypherCompletionAnalyzer.class);

    private static final String ALL_COLUMNS_PATTERN = "*";
    private static final String MATCH_ANY_PATTERN = "%";
    public static final int MAX_ATTRIBUTE_VALUE_PROPOSALS = 50;
    public static final int MAX_STRUCT_PROPOSALS = 100;

    private static final String[] MATCH_NEXT_KEYWORD =
            new String[] {"WHERE", "RETURN", "SET", "DETACH", "REMOVE", "DELETE"};

    private final SQLCompletionRequest request;
    private DBRProgressMonitor monitor;

    private final List<SQLCompletionProposalBase> proposals = new ArrayList<>();
    private boolean searchFinished = false;

    public CypherCompletionAnalyzer(SQLCompletionRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public void run(DBRProgressMonitor monitor) throws InvocationTargetException {
        try {
            runAnalyzer(monitor);
        } catch (DBException e) {
            throw new InvocationTargetException(e);
        }
    }

    public List<SQLCompletionProposalBase> getProposals() {
        return proposals;
    }

    public boolean isSearchFinished() {
        return searchFinished;
    }

    public void runAnalyzer(DBRProgressMonitor monitor) throws DBException {
        this.monitor = monitor;
        runAnalyzer();
    }

    private void runAnalyzer() throws DBException {
        String searchPrefix = request.getWordPart();
        request.setQueryType(null);
        SQLWordPartDetector wordDetector = request.getWordDetector();
        SQLSyntaxManager syntaxManager = request.getContext().getSyntaxManager();
        String prevKeyWord = wordDetector.getPrevKeyWord();
        boolean isPrevWordEmpty = CommonUtils.isEmpty(wordDetector.getPrevWords());
        String prevDelimiter = wordDetector.getPrevDelimiter();
        {
            if (!CommonUtils.isEmpty(prevKeyWord)) {
                if (syntaxManager.getDialect().isEntityQueryWord(prevKeyWord)) {
                    // TODO: its an ugly hack. Need a better way
                    if (isTableQueryToken(prevKeyWord)) {
                        if (prevDelimiter.equals("{")) {
                            request.setQueryType(SQLCompletionRequest.QueryType.COLUMN);
                        } else {
                            request.setQueryType(SQLCompletionRequest.QueryType.TABLE);
                        }
                    } else if (!isPrevWordEmpty
                            && ("{".equals(prevDelimiter) || ",".equals(prevDelimiter))) {
                        request.setQueryType(SQLCompletionRequest.QueryType.COLUMN);
                    } else if (!isPrevWordEmpty && "{*".equals(prevDelimiter)) {
                        wordDetector.shiftOffset(
                                -CypherCompletionAnalyzer.ALL_COLUMNS_PATTERN.length());
                        searchPrefix = CypherCompletionAnalyzer.ALL_COLUMNS_PATTERN;
                        request.setQueryType(SQLCompletionRequest.QueryType.COLUMN);
                    } else {
                        if (prevDelimiter.equals(":")
                                || prevDelimiter.equals("(:")
                                || prevDelimiter.equals("[:")) {
                            request.setQueryType(SQLCompletionRequest.QueryType.TABLE);
                        }
                    }
                } else if (syntaxManager.getDialect().isAttributeQueryWord(prevKeyWord)) {
                    if (prevDelimiter.equals(":")
                            || prevDelimiter.equals("(:")
                            || prevDelimiter.equals("[:")) {
                        request.setQueryType(SQLCompletionRequest.QueryType.TABLE);
                    } else {
                        request.setQueryType(SQLCompletionRequest.QueryType.COLUMN);
                        char curChar = ' ';
                        try {
                            curChar =
                                    request.getDocument()
                                            .getChar(wordDetector.getCursorOffset() - 1);
                        } catch (BadLocationException e) {
                            log.debug(e);
                        }
                        if (!request.isSimpleMode()
                                && CommonUtils.isEmpty(request.getWordPart())
                                && prevDelimiter.indexOf(curChar) != -1
                                && prevDelimiter.equals(
                                        CypherCompletionAnalyzer.ALL_COLUMNS_PATTERN)) {
                            wordDetector.shiftOffset(
                                    -CypherCompletionAnalyzer.ALL_COLUMNS_PATTERN.length());
                            searchPrefix = CypherCompletionAnalyzer.ALL_COLUMNS_PATTERN;
                        }
                    }
                }
            }
        }
        request.setWordPart(searchPrefix);

        DBPDataSource dataSource = request.getContext().getDataSource();
        if (dataSource == null) {
            return;
        }
        String wordPart = request.getWordPart();
        boolean emptyWord = wordPart.length() == 0;
        boolean isNumber = !CommonUtils.isEmpty(wordPart) && CommonUtils.isNumber(wordPart);

        SQLCompletionRequest.QueryType queryType = request.getQueryType();
        Map<String, Object> parameters = new LinkedHashMap<>();

        if (queryType != null) {
            if (queryType == SQLCompletionRequest.QueryType.COLUMN
                    && dataSource instanceof DBSObjectContainer) {
                if (searchPrefix != null
                        && syntaxManager
                                .getDialect()
                                .isAttributeQueryWord(request.getWordDetector().getPrevKeyWord())) {
                    makeProposalsFromAlias((DBSObjectContainer) dataSource, searchPrefix);
                }
            }

            // Try to determine which object is queried (if wordPart is not empty)
            // or get list of root database objects
            if (emptyWord || isNumber) {
                // Get root objects
                DBPObject rootObject = null;
                if (queryType == SQLCompletionRequest.QueryType.COLUMN
                        && dataSource instanceof DBSObjectContainer) {
                    // Try to detect current table
                    rootObject = getTableFromAlias((DBSObjectContainer) dataSource, null);
                } else if (dataSource instanceof DBSObjectContainer) {
                    // Try to get from active object
                    DBSObject selectedObject =
                            DBUtils.getActiveInstanceObject(
                                    request.getContext().getExecutionContext());
                    if (selectedObject != null) {
                        makeProposalsFromChildren(selectedObject, null, false, parameters);
                        rootObject = DBUtils.getPublicObject(selectedObject.getParentObject());
                    } else {
                        rootObject = dataSource;
                    }
                }

                if (rootObject != null) {
                    makeProposalsFromChildren(rootObject, null, false, parameters);
                }
            } else {
                DBSObject rootObject = null;
                if (queryType == SQLCompletionRequest.QueryType.COLUMN
                        && dataSource instanceof DBSObjectContainer) {
                    // Part of column name
                    // Try to get from active object
                    DBSObjectContainer sc = (DBSObjectContainer) dataSource;
                    DBSObject selectedObject =
                            DBUtils.getActiveInstanceObject(
                                    request.getContext().getExecutionContext());
                    if (selectedObject instanceof DBSObjectContainer) {
                        sc = (DBSObjectContainer) selectedObject;
                    }
                    SQLDialect sqlDialect = request.getContext().getDataSource().getSQLDialect();
                    String tableAlias = null;
                    if (ALL_COLUMNS_PATTERN.equals(wordPart)) {
                        if (!isPrevWordEmpty) {
                            String prevWord = wordDetector.getPrevWords().get(0);
                            if (prevWord.contains(sqlDialect.getCatalogSeparator())) {
                                int divPos = prevWord.lastIndexOf(sqlDialect.getCatalogSeparator());
                                tableAlias = prevWord.substring(0, divPos);
                            }
                        }
                    }
                    if (tableAlias == null) {
                        int divPos = wordPart.lastIndexOf(syntaxManager.getStructSeparator());
                        tableAlias = divPos == -1 ? null : wordPart.substring(0, divPos);
                    }
                    if (tableAlias == null && !CommonUtils.isEmpty(wordPart)) {
                        // May be an incomplete table alias. Try to find such table
                        rootObject = getTableFromAlias(sc, wordPart);
                        if (rootObject != null) {
                            // Found alias - no proposals
                            searchFinished = true;
                            return;
                        }
                    }
                    rootObject = getTableFromAlias(sc, tableAlias);
                    if (rootObject == null && tableAlias != null) {
                        // Maybe alias ss a table name
                        String[] allNames =
                                SQLUtils.splitFullIdentifier(
                                        tableAlias,
                                        sqlDialect.getCatalogSeparator(),
                                        sqlDialect.getIdentifierQuoteStrings(),
                                        false);
                        rootObject =
                                SQLSearchUtils.findObjectByFQN(
                                        monitor,
                                        sc,
                                        request.getContext().getExecutionContext(),
                                        Arrays.asList(allNames),
                                        !request.isSimpleMode(),
                                        wordDetector);
                    }
                }
                if (rootObject != null) {
                    makeProposalsFromChildren(rootObject, wordPart, false, parameters);
                } else {
                    // Get root object or objects from active database (if any)
                    if (queryType != SQLCompletionRequest.QueryType.COLUMN) {
                        makeDataSourceProposals();
                    }
                }
            }
        }

        if (!emptyWord) {
            makeProposalsFromQueryParts();
        }

        // Final filtering
        if (!searchFinished) {
            List<String> matchedKeywords = Collections.emptyList();
            Set<String> allowedKeywords = null;

            SQLDialect sqlDialect = request.getContext().getDataSource().getSQLDialect();
            if (CommonUtils.isEmpty(prevKeyWord)) {
                allowedKeywords = new HashSet<>();
                Collections.addAll(allowedKeywords, sqlDialect.getQueryKeywords());
                Collections.addAll(allowedKeywords, sqlDialect.getDMLKeywords());
                Collections.addAll(allowedKeywords, sqlDialect.getDDLKeywords());
                Collections.addAll(allowedKeywords, sqlDialect.getExecuteKeywords());
            } else if (sqlDialect.isEntityQueryWord(prevKeyWord)) {
                allowedKeywords = new HashSet<>();

                allowedKeywords.addAll(Arrays.asList(MATCH_NEXT_KEYWORD));
                allowedKeywords.remove(wordDetector.getNextWord());

                if (CommonUtils.isEmpty(request.getWordPart())) {
                    matchedKeywords = new ArrayList<>(allowedKeywords);
                }
            }

            if (matchedKeywords.isEmpty() && !CommonUtils.isEmpty(request.getWordPart())) {
                // Keyword assist
                matchedKeywords =
                        syntaxManager.getDialect().getMatchedKeywords(request.getWordPart());
                if (!request.isSimpleMode()) {
                    // Sort using fuzzy match
                    matchedKeywords.sort(
                            Comparator.comparingInt(
                                    o -> TextUtils.fuzzyScore(o, request.getWordPart())));
                }
            }
            for (String keyWord : matchedKeywords) {
                DBPKeywordType keywordType = syntaxManager.getDialect().getKeywordType(keyWord);
                if (keywordType != null) {
                    if (keywordType == DBPKeywordType.TYPE) {
                        continue;
                    }

                    if (request.getQueryType() == SQLCompletionRequest.QueryType.COLUMN
                            && !(keywordType == DBPKeywordType.FUNCTION
                                    || keywordType == DBPKeywordType.KEYWORD
                                    || keywordType == DBPKeywordType.OTHER)) {
                        continue;
                    }
                    if (allowedKeywords != null && !allowedKeywords.contains(keyWord)) {
                        continue;
                    }
                    proposals.add(
                            CypherCompletionAnalyzer.createCompletionProposal(
                                    request,
                                    keyWord,
                                    keyWord,
                                    keywordType,
                                    null,
                                    false,
                                    null,
                                    Collections.emptyMap()));
                }
            }
        }
        filterProposals(dataSource);
    }

    private void filterProposals(DBPDataSource dataSource) {

        // Remove duplications
        final Set<String> proposalMap = new HashSet<>(proposals.size());
        for (int i = 0; i < proposals.size(); ) {
            SQLCompletionProposalBase proposal = proposals.get(i);
            if (proposalMap.contains(proposal.getDisplayString())) {
                proposals.remove(i);
                continue;
            }
            proposalMap.add(proposal.getDisplayString());
            i++;
        }

        DBSInstance defaultInstance = dataSource == null ? null : dataSource.getDefaultInstance();
        DBCExecutionContext executionContext = request.getContext().getExecutionContext();
        DBSObject selectedObject =
                defaultInstance == null || executionContext == null
                        ? null
                        : DBUtils.getActiveInstanceObject(executionContext);
        boolean hideDups = request.getContext().isHideDuplicates() && selectedObject != null;
        if (hideDups) {
            for (int i = 0; i < proposals.size(); i++) {
                SQLCompletionProposalBase proposal = proposals.get(i);
                for (int j = 0; j < proposals.size(); ) {
                    SQLCompletionProposalBase proposal2 = proposals.get(j);
                    if (i != j
                            && proposal.hasStructObject()
                            && proposal2.hasStructObject()
                            && CommonUtils.equalObjects(
                                    proposal.getObject().getName(), proposal2.getObject().getName())
                            && proposal.getObjectContainer() == selectedObject) {
                        proposals.remove(j);
                    } else {
                        j++;
                    }
                }
            }
        }

        if (hideDups) {
            // Remove duplicates from non-active schema

            if (selectedObject instanceof DBSObjectContainer) {}
        }

        // Apply navigator object filters
        if (dataSource != null) {
            DBPDataSourceContainer dsContainer = dataSource.getContainer();
            Map<DBSObject, Map<Class<?>, List<SQLCompletionProposalBase>>> containerMap =
                    new HashMap<>();
            for (SQLCompletionProposalBase proposal : proposals) {
                DBSObject container = proposal.getObjectContainer();
                DBPNamedObject object = proposal.getObject();
                if (object == null) {
                    continue;
                }
                Map<Class<?>, List<SQLCompletionProposalBase>> typeMap =
                        containerMap.computeIfAbsent(container, k -> new HashMap<>());
                Class<?> objectType =
                        object instanceof DBSObjectReference
                                ? ((DBSObjectReference) object).getObjectClass()
                                : object.getClass();
                List<SQLCompletionProposalBase> list =
                        typeMap.computeIfAbsent(objectType, k -> new ArrayList<>());
                list.add(proposal);
            }
            for (Map.Entry<DBSObject, Map<Class<?>, List<SQLCompletionProposalBase>>> entry :
                    containerMap.entrySet()) {
                for (Map.Entry<Class<?>, List<SQLCompletionProposalBase>> typeEntry :
                        entry.getValue().entrySet()) {
                    DBSObjectFilter filter =
                            dsContainer.getObjectFilter(typeEntry.getKey(), entry.getKey(), true);
                    if (filter != null && filter.isEnabled()) {
                        for (SQLCompletionProposalBase proposal : typeEntry.getValue()) {
                            if (!filter.matches(proposal.getObject().getName())) {
                                proposals.remove(proposal);
                            }
                        }
                    }
                }
            }
        }
    }

    private void makeProposalsFromQueryParts() {
        if (request.getQueryType() == null
                && request.getWordDetector().getPrevKeyWord().equalsIgnoreCase("MATCH")) {
            // Seems to be table alias
            return;
        }
        String wordPart = request.getWordPart();
        // Find all aliases matching current word
        SQLScriptElement activeQuery = request.getActiveQuery();
        if (activeQuery != null
                && !CommonUtils.isEmpty(activeQuery.getText())
                && !CommonUtils.isEmpty(wordPart)) {
            if (wordPart.indexOf(request.getContext().getSyntaxManager().getStructSeparator()) != -1
                    || wordPart.equals(ALL_COLUMNS_PATTERN)) {
                return;
            }
            final Pair<String, String> name = extractTableName(wordPart, true);
            if (name != null) {
                final String tableName = name.getFirst();
                final String tableAlias = name.getSecond();
                if (!hasProposal(proposals, tableName)) {
                    proposals.add(
                            0,
                            CypherCompletionAnalyzer.createCompletionProposal(
                                    request,
                                    tableName,
                                    tableName,
                                    DBPKeywordType.OTHER,
                                    null,
                                    false,
                                    null,
                                    Collections.emptyMap()));
                }
                if (!CommonUtils.isEmpty(tableAlias) && !hasProposal(proposals, tableAlias)) {
                    proposals.add(
                            0,
                            CypherCompletionAnalyzer.createCompletionProposal(
                                    request,
                                    tableAlias,
                                    tableAlias,
                                    DBPKeywordType.OTHER,
                                    null,
                                    false,
                                    null,
                                    Collections.emptyMap()));
                }
            }
        }
    }

    private void makeDataSourceProposals() throws DBException {
        DBPDataSource dataSource = request.getContext().getDataSource();
        final DBSObjectContainer rootContainer =
                DBUtils.getAdapter(DBSObjectContainer.class, dataSource);
        if (rootContainer == null) {
            return;
        }
        DBCExecutionContext executionContext = request.getContext().getExecutionContext();
        if (executionContext == null) {
            return;
        }

        DBSObjectContainer sc = rootContainer;
        DBSObject childObject = sc;
        String[] tokens = request.getWordDetector().splitWordPart();

        // Detect selected object (container).
        // There could be multiple selected objects on different hierarchy levels (e.g. PG)
        DBSObjectContainer[] selectedContainers;
        {
            DBSObject[] selectedObjects = DBUtils.getSelectedObjects(monitor, executionContext);
            selectedContainers = new DBSObjectContainer[selectedObjects.length];
            for (int i = 0; i < selectedObjects.length; i++) {
                selectedContainers[i] =
                        DBUtils.getAdapter(DBSObjectContainer.class, selectedObjects[i]);
            }
        }

        String lastToken = null;
        for (int i = 0; i < tokens.length; i++) {
            final String token = tokens[i];
            if (i == tokens.length - 1 && !request.getWordDetector().getWordPart().endsWith(".")) {
                lastToken = token;
                break;
            }
            if (sc == null) {
                break;
            }
            // Get next structure container
            final String objectName =
                    request.getWordDetector().isQuoted(token)
                            ? request.getWordDetector().removeQuotes(token)
                            : DBObjectNameCaseTransformer.transformName(dataSource, token);
            childObject = objectName == null ? null : sc.getChild(monitor, objectName);
            if (childObject == null && i == 0 && objectName != null) {
                for (DBSObjectContainer selectedContainer : selectedContainers) {
                    if (selectedContainer != null) {
                        // Probably it is from selected object, let's try it
                        childObject = selectedContainer.getChild(monitor, objectName);
                        if (childObject != null) {
                            sc = selectedContainer;
                            break;
                        }
                    }
                }
            }
            if (childObject == null) {
                if (i == 0) {
                    // Assume it's a table alias ?
                    childObject = getTableFromAlias(sc, token);
                    if (childObject == null && !request.isSimpleMode()) {
                        // Search using structure assistant
                        DBSStructureAssistant structureAssistant =
                                DBUtils.getAdapter(DBSStructureAssistant.class, sc);
                        if (structureAssistant != null) {
                            DBSStructureAssistant.ObjectsSearchParams params =
                                    new DBSStructureAssistant.ObjectsSearchParams(
                                            structureAssistant.getAutoCompleteObjectTypes(),
                                            request.getWordDetector().removeQuotes(token));
                            params.setCaseSensitive(request.getWordDetector().isQuoted(token));
                            params.setMaxResults(2);
                            Collection<DBSObjectReference> references =
                                    structureAssistant.findObjectsByMask(
                                            monitor, executionContext, params);
                            if (!references.isEmpty()) {
                                childObject = references.iterator().next().resolveObject(monitor);
                            }
                        }
                    }
                } else {
                    // Path element not found. Damn - can't do anything.
                    return;
                }
            }

            if (childObject instanceof DBSObjectContainer) {
                sc = (DBSObjectContainer) childObject;
            } else {
                sc = null;
            }
        }
        if (childObject == null) {
            return;
        }
        if (lastToken == null) {
            // Get all children objects as proposals
            makeProposalsFromChildren(childObject, null, false, Collections.emptyMap());
        } else {
            // Get matched children
            makeProposalsFromChildren(childObject, lastToken, false, Collections.emptyMap());
            if (tokens.length == 1) {
                // Get children from selected object
            }
            if (tokens.length == 1) {
                // Try in active object
                for (DBSObjectContainer selectedContainer : selectedContainers) {
                    if (selectedContainer != null && selectedContainer != childObject) {
                        makeProposalsFromChildren(
                                selectedContainer, lastToken, true, Collections.emptyMap());
                    }
                }

                if (proposals.isEmpty() && !request.isSimpleMode()) {
                    // At last - try to find child tables by pattern
                    DBSStructureAssistant<?> structureAssistant = null;
                    for (DBSObject object = childObject;
                            object != null;
                            object = object.getParentObject()) {
                        structureAssistant =
                                DBUtils.getAdapter(DBSStructureAssistant.class, object);
                        if (structureAssistant != null) {
                            break;
                        }
                    }
                    if (structureAssistant != null) {
                        makeProposalsFromAssistant(
                                structureAssistant, sc, null, lastToken, Collections.emptyMap());
                    }
                }
            }
        }
    }

    private void makeProposalsFromAlias(DBSObjectContainer sc, String searchPrefix) {
        List<String> alias = extractAlias();

        if (alias == null) {
            return;
        }

        for (String name : alias) {
            if (!searchPrefix.isEmpty() && !name.contains(searchPrefix)) {
                continue;
            }
            proposals.add(
                    createCompletionProposal(request, name, name, DBPKeywordType.OTHER, "Alias"));
        }
    }

    @Nullable
    private DBSObject getTableFromAlias(DBSObjectContainer sc, @Nullable String token) {
        if (token == null) {
            token = "";
        } else if (token.equals(ALL_COLUMNS_PATTERN)) {
            return null;
        }

        final DBPDataSource dataSource = request.getContext().getDataSource();
        if (dataSource == null) {
            return null;
        }

        final SQLDialect sqlDialect = dataSource.getSQLDialect();
        final String catalogSeparator = sqlDialect.getCatalogSeparator();

        while (token.endsWith(catalogSeparator)) {
            token = token.substring(0, token.length() - 1);
        }

        final Pair<String, String> name = extractTableName(token, false);
        if (name != null && CommonUtils.isNotEmpty(name.getFirst())) {
            final String[][] quoteStrings = sqlDialect.getIdentifierQuoteStrings();
            final String[] allNames =
                    SQLUtils.splitFullIdentifier(
                            name.getFirst(), catalogSeparator, quoteStrings, false);
            DBSObject object =
                    SQLSearchUtils.findObjectByFQN(
                            monitor,
                            sc,
                            request.getContext().getExecutionContext(),
                            Arrays.asList(allNames),
                            !request.isSimpleMode(),
                            request.getWordDetector());
            return object;
        }

        return null;
    }

    private enum InlineState {
        UNMATCHED,
        TABLE_NAME,
        TABLE_DOT,
        ALIAS_AS,
        ALIAS_NAME,
        MATCHED
    };

    private List<String> extractAlias() {
        final SQLScriptElement activeQuery = request.getActiveQuery();
        if (activeQuery == null) {
            return null;
        }
        final IDocument document = request.getDocument();
        final SQLRuleManager ruleManager = request.getContext().getRuleManager();
        final TPRuleBasedScanner scanner = new TPRuleBasedScanner();
        scanner.setRules(ruleManager.getAllRules());
        scanner.setRange(document, activeQuery.getOffset(), activeQuery.getLength());

        List<String> aliasList = null;
        String prvValue = "";

        try {
            while (true) {
                final TPToken tok = scanner.nextToken();
                if (tok.isEOF()) {
                    break;
                }
                if (!(tok instanceof TPTokenAbstract) || tok.isWhitespace()) {
                    continue;
                }

                final String value =
                        document.get(scanner.getTokenOffset(), scanner.getTokenLength());

                if (":".equals(value)) {
                    if (aliasList == null) {
                        aliasList = new ArrayList<String>();
                    }
                    if (!isInSpecialString(prvValue)) {
                        aliasList.add(prvValue);
                    }
                }

                prvValue = value;
            }
        } catch (BadLocationException e) {
            log.debug(e);
        }

        return aliasList;
    }

    @Nullable
    private Pair<String, String> extractTableName(
            @Nullable String tableAlias, boolean allowPartialMatch) {
        final SQLScriptElement activeQuery = request.getActiveQuery();
        if (activeQuery == null) {
            return null;
        }
        final IDocument document = request.getDocument();
        final SQLRuleManager ruleManager = request.getContext().getRuleManager();
        final TPRuleBasedScanner scanner = new TPRuleBasedScanner();
        scanner.setRules(ruleManager.getAllRules());
        scanner.setRange(document, activeQuery.getOffset(), activeQuery.getLength());

        try {
            InlineState state = InlineState.UNMATCHED;
            String matchedTableName = null;
            String matchedTableAlias = null;

            SQLWordPartDetector wordDetector = request.getWordDetector();

            String prvValue = "";
            boolean lastSearch = false; // for

            if (wordDetector != null
                    && wordDetector.getPrevDelimiter() != null
                    && wordDetector.getPrevDelimiter().equals("{")) {
                lastSearch = true;
            }

            while (true) {
                final TPToken tok = scanner.nextToken();
                if (tok.isEOF()) {
                    break;
                }
                if (!(tok instanceof TPTokenAbstract) || tok.isWhitespace()) {
                    continue;
                }

                final String value =
                        document.get(scanner.getTokenOffset(), scanner.getTokenLength());

                // Last Table Search
                if (lastSearch
                        && wordDetector.getStartOffset() - 1 == scanner.getTokenOffset()
                        && value.equals(wordDetector.getPrevDelimiter())) {
                    matchedTableName = prvValue;
                    state = InlineState.MATCHED;
                    continue;
                }

                if (state == InlineState.UNMATCHED
                        && ":".equals(value)
                        && !prvValue.isEmpty()
                        && !lastSearch) {
                    state = InlineState.ALIAS_AS;
                    matchedTableAlias = prvValue;
                    continue;
                }

                if (state == InlineState.ALIAS_AS && isNamePartToken(tok)) {
                    matchedTableName = CommonUtils.notEmpty(matchedTableName) + value;
                    state = InlineState.MATCHED;
                    continue;
                }

                prvValue = value;

                if (state == InlineState.MATCHED) {
                    final boolean fullMatch =
                            CommonUtils.isEmpty(tableAlias) || tableAlias.equals(matchedTableAlias);
                    final boolean partialMatch =
                            fullMatch
                                    || (allowPartialMatch
                                            && CommonUtils.startsWithIgnoreCase(
                                                    matchedTableAlias, tableAlias));
                    if (!fullMatch && !partialMatch) {
                        // The presented alias does not fully or partially match the matched token,
                        // reset
                        state = InlineState.UNMATCHED;
                        matchedTableName = null;
                        matchedTableAlias = null;
                    } else {
                        return new Pair<>(matchedTableName, matchedTableAlias);
                    }
                }
            }
            if (!CommonUtils.isEmpty(matchedTableName)
                    && (CommonUtils.isEmpty(tableAlias)
                            || CommonUtils.equalObjects(tableAlias, matchedTableAlias))) {
                return new Pair<>(matchedTableName, matchedTableAlias);
            }
        } catch (BadLocationException e) {
            log.debug(e);
        }

        return null;
    }

    private static boolean isNamePartToken(TPToken tok) {
        return tok.getData() == SQLTokenType.T_QUOTED
                || tok.getData() == SQLTokenType.T_KEYWORD
                || tok.getData() == SQLTokenType.T_OTHER;
    }

    private static boolean isTableQueryToken(String value) {
        return value.equalsIgnoreCase("MATCH") || value.equalsIgnoreCase("CREATE");
    }

    private void makeProposalsFromChildren(
            DBPObject parent,
            @Nullable String startPart,
            boolean addFirst,
            Map<String, Object> params)
            throws DBException {

        if (request.getQueryType() == SQLCompletionRequest.QueryType.EXEC) {
            return;
        }
        if (parent instanceof DBSAlias) {
            DBSObject realParent = ((DBSAlias) parent).getTargetObject(monitor);
            if (realParent == null) {
                log.debug("Can't get synonym target object");
            } else {
                parent = realParent;
            }
        }
        SQLWordPartDetector wordDetector = request.getWordDetector();
        if (startPart != null) {
            startPart = wordDetector.removeQuotes(startPart).toUpperCase(Locale.ENGLISH);
            int divPos =
                    startPart.lastIndexOf(
                            request.getContext().getSyntaxManager().getStructSeparator());
            if (divPos != -1) {
                startPart = startPart.substring(divPos + 1);
            }
        }

        DBPDataSource dataSource = request.getContext().getDataSource();
        Collection<? extends DBSObject> children = null;
        if (parent instanceof DBSObjectContainer) {
            children = ((DBSObjectContainer) parent).getChildren(monitor);
        } else if (parent instanceof DBSEntity) {
            children = ((DBSEntity) parent).getAttributes(monitor);
        }
        if (children != null && !children.isEmpty()) {
            // boolean isJoin =
            // SQLConstants.KEYWORD_JOIN.equals(request.wordDetector.getPrevKeyWord());

            List<DBSObject> matchedObjects = new ArrayList<>();
            final Map<String, Integer> scoredMatches = new HashMap<>();
            boolean simpleMode = request.isSimpleMode();
            boolean allObjects = !simpleMode && ALL_COLUMNS_PATTERN.equals(startPart);
            String objPrefix = null;
            if (allObjects) {
                if (!CommonUtils.isEmpty(wordDetector.getPrevWords())) {
                    String prevWord = wordDetector.getPrevWords().get(0);
                    if (prevWord.length() > 0
                            && prevWord.charAt(prevWord.length() - 1)
                                    == request.getContext()
                                            .getSyntaxManager()
                                            .getStructSeparator()) {
                        objPrefix = prevWord;
                    }
                }
            }
            StringBuilder combinedMatch = new StringBuilder();
            for (DBSObject child : children) {
                if (DBUtils.isHiddenObject(child)) {
                    // Skip hidden
                    continue;
                }
                if (DBUtils.isVirtualObject(child)) {
                    makeProposalsFromChildren(child, startPart, addFirst, Collections.emptyMap());
                    continue;
                }
                if (allObjects) {
                    if (combinedMatch.length() > 0) {
                        combinedMatch.append(", ");
                        if (objPrefix != null) combinedMatch.append(objPrefix);
                    }
                    combinedMatch.append(DBUtils.getQuotedIdentifier(child));
                } else {
                    if (dataSource != null && !request.getContext().isSearchInsideNames()) {
                        // startsWith
                        if (CommonUtils.isEmpty(startPart)
                                || CommonUtils.startsWithIgnoreCase(child.getName(), startPart)) {
                            matchedObjects.add(child);
                        }
                    } else {
                        // Use fuzzy search for contains
                        int score =
                                CommonUtils.isEmpty(startPart)
                                        ? 1
                                        : TextUtils.fuzzyScore(child.getName(), startPart);
                        if (score > 0) {
                            String prevPrevdelimiter = wordDetector.getPrevPrevDelimiter();
                            String prevdelimiter = wordDetector.getPrevDelimiter();
                            if ((prevPrevdelimiter != null
                                            && prevPrevdelimiter.equals("(")
                                            && !prevdelimiter.equals(")-[:"))
                                    || (prevdelimiter != null
                                            && (prevdelimiter.equals("(:")
                                                    || prevdelimiter.equals("]->(:")))) {
                                if (child.getClass().getName().endsWith("Table")) {
                                    matchedObjects.add(child);
                                    scoredMatches.put(child.getName(), score);
                                }
                            } else if ((prevPrevdelimiter != null
                                            && prevPrevdelimiter.equals("[")
                                            && !prevdelimiter.equals("]->(:"))
                                    || (prevdelimiter != null
                                            && (prevdelimiter.equals("[:")
                                                    || prevdelimiter.equals(")-[:")))) {
                                if (child.getClass().getName().endsWith("View") ||
                                        child.getClass().getName().endsWith("Edge")) {
                                    matchedObjects.add(child);
                                    scoredMatches.put(child.getName(), score);
                                }
                            } else {
                                matchedObjects.add(child);
                                scoredMatches.put(child.getName(), score);
                            }
                        }
                    }
                }
            }
            if (combinedMatch.length() > 0) {
                String replaceString = combinedMatch.toString();

                proposals.add(
                        createCompletionProposal(
                                request,
                                replaceString,
                                replaceString,
                                DBPKeywordType.OTHER,
                                "All objects"));
            } else if (!matchedObjects.isEmpty()) {
                if (startPart == null || scoredMatches.isEmpty()) {
                    if (dataSource != null && request.getContext().isSortAlphabetically()) {
                        matchedObjects.sort(
                                (o1, o2) -> {
                                    if (o1 instanceof DBSAttributeBase
                                            && o2 instanceof DBSAttributeBase) {
                                        return DBUtils.orderComparator()
                                                .compare(
                                                        (DBSAttributeBase) o1,
                                                        (DBSAttributeBase) o2);
                                    }
                                    return DBUtils.nameComparatorIgnoreCase().compare(o1, o2);
                                });
                    }
                } else {
                    matchedObjects.sort(
                            (o1, o2) -> {
                                int score1 = scoredMatches.get(o1.getName());
                                int score2 = scoredMatches.get(o2.getName());
                                if (score1 == score2) {
                                    if (o1 instanceof DBSAttributeBase
                                            && o2 instanceof DBSAttributeBase) {
                                        return DBUtils.orderComparator()
                                                .compare(
                                                        (DBSAttributeBase) o1,
                                                        (DBSAttributeBase) o2);
                                    }
                                    return DBUtils.nameComparatorIgnoreCase().compare(o1, o2);
                                }
                                return score2 - score1;
                            });
                }
                List<SQLCompletionProposalBase> childProposals =
                        new ArrayList<>(matchedObjects.size());
                for (DBSObject child : matchedObjects) {
                    SQLCompletionProposalBase proposal =
                            makeProposalsFromObject(
                                    child, !(parent instanceof DBPDataSource), params);
                    if (!scoredMatches.isEmpty()) {
                        int proposalScore = scoredMatches.get(child.getName());
                        proposal.setProposalScore(proposalScore);
                    }

                    childProposals.add(proposal);
                }
                if (addFirst) {
                    // Add proposals in the beginning (because the most strict identifiers have to
                    // be first)
                    proposals.addAll(0, childProposals);
                } else {
                    proposals.addAll(childProposals);
                }
            }
        }
    }

    private void makeProposalsFromAssistant(
            DBSStructureAssistant assistant,
            @Nullable DBSObjectContainer rootSC,
            DBSObjectType[] objectTypes,
            String objectName,
            @NotNull Map<String, Object> params)
            throws DBException {

        DBSStructureAssistant.ObjectsSearchParams assistantParams =
                new DBSStructureAssistant.ObjectsSearchParams(
                        objectTypes == null ? assistant.getAutoCompleteObjectTypes() : objectTypes,
                        makeObjectNameMask(objectName, rootSC));
        assistantParams.setParentObject(rootSC);
        assistantParams.setCaseSensitive(request.getWordDetector().isQuoted(objectName));
        assistantParams.setGlobalSearch(request.getContext().isSearchGlobally());
        assistantParams.setMaxResults(MAX_STRUCT_PROPOSALS);
        Collection<DBSObjectReference> references =
                assistant.findObjectsByMask(
                        monitor, request.getContext().getExecutionContext(), assistantParams);
        for (DBSObjectReference reference : references) {
            proposals.add(
                    makeProposalsFromObject(
                            reference,
                            !(rootSC instanceof DBPDataSource),
                            reference.getObjectType().getImage(),
                            params));
        }
    }

    private String makeObjectNameMask(String objectName, @Nullable DBSObjectContainer rootSC) {
        SQLWordPartDetector wordDetector = request.getWordDetector();
        if (wordDetector.containsSeparator(objectName)) {
            String[] strings = wordDetector.splitIdentifier(objectName);
            if (rootSC != null) {
                boolean endsOnStructureSeparator =
                        objectName.charAt(objectName.length() - 1)
                                == wordDetector.getStructSeparator();
                if (isParentNameInPatternNameArray(
                        strings, rootSC, wordDetector, endsOnStructureSeparator)) {
                    if (endsOnStructureSeparator) {
                        objectName = "";
                    } else {
                        objectName = wordDetector.removeQuotes(strings[strings.length - 1]);
                    }
                }
            }
        } else {
            objectName = wordDetector.removeQuotes(objectName);
        }
        if (request.getContext().isSearchInsideNames()) {
            return MATCH_ANY_PATTERN + objectName + MATCH_ANY_PATTERN;
        } else {
            return objectName + MATCH_ANY_PATTERN;
        }
    }

    private boolean isParentNameInPatternNameArray(
            String[] strings,
            @NotNull DBSObjectContainer rootSC,
            SQLWordPartDetector wordDetector,
            boolean endsOnStructureSeparator) {
        int indexOfParent;
        if (endsOnStructureSeparator || strings.length < 2) {
            indexOfParent = strings.length - 1;
        } else {
            indexOfParent = strings.length - 2;
        }
        return rootSC.getName().equals(wordDetector.removeQuotes(strings[indexOfParent]));
    }

    private SQLCompletionProposalBase makeProposalsFromObject(
            DBSObject object, boolean useShortName, Map<String, Object> params) {
        DBNNode node = DBNUtils.getNodeByObject(monitor, object, false);

        DBPImage objectIcon = node == null ? null : node.getNodeIconDefault();
        if (objectIcon == null) {
            objectIcon = DBValueFormatting.getObjectImage(object);
        }
        return makeProposalsFromObject(object, useShortName, objectIcon, params);
    }

    private SQLCompletionProposalBase makeProposalsFromObject(
            DBPNamedObject object,
            boolean useShortName,
            @Nullable DBPImage objectIcon,
            @NotNull Map<String, Object> params) {
        String alias = null;
        SQLTableAliasInsertMode aliasMode = SQLTableAliasInsertMode.NONE;
        String prevWord = request.getWordDetector().getPrevKeyWord();

        if ("MATCH".equals(prevWord)) {
            if (object instanceof DBSEntity) {
                aliasMode =
                        SQLTableAliasInsertMode.fromPreferences(
                                ((DBSEntity) object)
                                        .getDataSource()
                                        .getContainer()
                                        .getPreferenceStore());
            }
        }
        String objectName =
                useShortName
                        ? object.getName()
                        : DBUtils.getObjectFullName(object, DBPEvaluationContext.DML);

        boolean isSingleObject = true;
        String replaceString = null;
        DBPDataSource dataSource = request.getContext().getDataSource();
        if (dataSource != null) {
            // If we replace short name with referenced object
            // and current active schema (catalog) is not this object's container then
            // replace with full qualified name
            if (!request.getContext().isUseShortNames() && object instanceof DBSObjectReference) {
                if (request.getWordDetector()
                                .getFullWord()
                                .indexOf(
                                        request.getContext()
                                                .getSyntaxManager()
                                                .getStructSeparator())
                        == -1) {
                    DBSObjectReference structObject = (DBSObjectReference) object;
                    if (structObject.getContainer() != null) {
                        DBSObject selectedObject =
                                DBUtils.getActiveInstanceObject(
                                        request.getContext().getExecutionContext());
                        if (selectedObject != structObject.getContainer()) {
                            replaceString =
                                    structObject.getFullyQualifiedName(DBPEvaluationContext.DML);
                            isSingleObject = false;
                        }
                    }
                }
            }
            if (replaceString == null) {
                if (request.getContext().isUseFQNames() && object instanceof DBPQualifiedObject) {
                    replaceString =
                            ((DBPQualifiedObject) object)
                                    .getFullyQualifiedName(DBPEvaluationContext.DML);
                } else {
                    replaceString = DBUtils.getQuotedIdentifier(dataSource, object.getName());
                }
            }
        } else {
            replaceString = DBUtils.getObjectShortName(object);
        }
        if (!CommonUtils.isEmpty(alias)) {
            if (aliasMode == SQLTableAliasInsertMode.EXTENDED) {
                replaceString += " " + convertKeywordCase(request, "as", false);
            }
            replaceString += " " + alias;
        }
        return createCompletionProposal(
                request,
                replaceString,
                objectName,
                DBPKeywordType.OTHER,
                objectIcon,
                isSingleObject,
                object,
                params);
    }

    private static boolean isInSpecialString(String str) {
        if (!str.matches("^[0-9|a-z|A-Z]*$")) {
            return true;
        }
        return false;
    }
}
