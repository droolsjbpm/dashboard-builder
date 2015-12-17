/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.dashboard.dataset.sql;

import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.CoreServices;
import org.jboss.dashboard.database.hibernate.SQLStatementTrace;
import org.jboss.dashboard.dataset.AbstractDataSet;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.dataset.profiler.DataSetLoadConstraints;
import org.jboss.dashboard.profiler.CodeBlockTrace;
import org.jboss.dashboard.provider.sql.SQLDataLoader;
import org.jboss.dashboard.provider.sql.SQLDataProperty;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.provider.DataFilter;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Set;

/**
 * A data set implementation that holds as a matrix in-memory all the rows returned by a given SQL query executed
 * against a local or remote database.
 */
public class SQLDataSet extends AbstractDataSet {

    /**
     * Logger
     */
    private transient static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SQLDataSet.class);

    /**
     * Where the data comes from.
     */
    protected String dataSource;

    /**
     * The SQL query that retrieves the data from the data source.
     */
    protected String sqlQuery;

    /**
     * The last executed SQL statement
     */
    protected transient SQLStatement lastExecutedStmt;

    public SQLDataSet(DataProvider provider, SQLDataLoader loader) {
        super(provider);
        this.dataSource = loader.getDataSource();
        this.sqlQuery = loader.getSQLQuery();
    }

    public SQLDataSet(DataProvider provider, String dataSource, String sqlQuery) {
        super(provider);
        this.dataSource = dataSource;
        this.sqlQuery = sqlQuery;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getSQLQuery() {
        return sqlQuery;
    }

    public SQLDataProperty createSQLProperty() {
        return new SQLDataProperty();
    }

    public SQLStatement createSQLStatament() throws Exception {
        return new SQLStatement(sqlQuery);
    }

    public Set<String> getPropertiesReferenced() {
        Set<String> results = super.getPropertiesReferenced();
        if (lastExecutedStmt != null) {
            results.addAll(lastExecutedStmt.getFilterPropertyIds());
        }
        return results;
    }

    public void load() throws Exception {
        DataSource targetDS = CoreServices.lookup().getDataSourceManager().getDataSource(dataSource);
        if (targetDS == null) return;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        CodeBlockTrace trace = null;
        try {
            // Get the connection.
            conn = targetDS.getConnection();

            // Execute the query.
            lastExecutedStmt = createSQLStatament();
            trace = new SQLStatementTrace(lastExecutedStmt.getSQLSentence()).begin();
            trace.addRuntimeConstraint(new DataSetLoadConstraints(this));

            log.debug("Load data set from datasource=" + dataSource + " SQL=" + lastExecutedStmt.getSQLSentence());
            stmt = lastExecutedStmt.getPreparedStatement(conn);
            rs = stmt.executeQuery();

            // Get the properties definition.
            ResultSetMetaData meta = rs.getMetaData();
            int propsSize = meta.getColumnCount();
            SQLDataSet.this.setPropertySize(propsSize);
            for (int i = 0; i < propsSize; i++) {
                SQLDataProperty dp = createSQLProperty();
                String propId = StringUtils.isNotBlank(meta.getColumnLabel(i + 1)) ? meta.getColumnLabel(i + 1) : meta.getColumnName(i + 1);
                dp.setPropertyId(propId.toLowerCase());
//                dp.setPropertyId(meta.getColumnName(i + 1).toLowerCase());
                dp.setType(meta.getColumnType(i + 1));
                dp.setTableName(meta.getTableName(i + 1));
                dp.setColumnName(meta.getColumnName(i + 1));
                addProperty(dp, i);
            }

            // Get rows and populate the data set values.
            int index = 0;
            while (rs.next()) {
                Object[] row = new Object[propsSize];
                for (int i = 0; i < propsSize; i++) row[i] = rs.getObject(i + 1);
                SQLDataSet.this.addRowValues(row);

                // Check load constraints (every 10,000 rows)
                if (++index == 10000) {
                    trace.checkRuntimeConstraints();
                    index = 0;
                }
            }

            // Once we got the dataset initialized then calculate the domain for each property.
            for (int i = 0; i < properties.length; i++) {
                SQLDataProperty property = (SQLDataProperty) properties[i];
                property.calculateDomain();
            }
        }
        catch (Exception e) {
            if (lastExecutedStmt != null) {
                log.error("Error in load() SQLDataset. SQL = " + lastExecutedStmt.getSQLSentence(), e);
            }
            throw e;
        }
        finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception e) {
                log.warn("Error closing ResultSet: ", e);
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.warn("Error closing PreparedStatement: ", e);
            }
            if (conn != null) {
                conn.close();
            }
            if (trace != null) {
                trace.end();
            }
        }
    }

    public DataSet filter(DataFilter filter) throws Exception {
        return _filterInDB(filter);
    }

    public DataSet _filterInDB(DataFilter filter) throws Exception {
        // Check if the SQL changes when filtering is requested. If so then reload the full dataset.
        SQLStatement currentStatement = createSQLStatament();
        if (!lastExecutedStmt.equals(currentStatement)) {
            SQLDataSet sqlDataSet = new SQLDataSet(provider, dataSource, sqlQuery);
            sqlDataSet.load();

            // Apply the in-memory filter in order to cover all the filter properties non specified as SQL conditions.
            DataFilter _remainingFilter = (DataFilter) filter.cloneFilter();
            List<String> propIds = currentStatement.getFilterPropertyIds();
            for (String propId : propIds) _remainingFilter.removeProperty(propId);
            DataSet result = sqlDataSet._filterInMemory(_remainingFilter);

            // If the in-memory filter applies then return it.
            if (result != null) return result;
            return sqlDataSet;
        }
        // Apply the in-memory filter by default.
        return super._filterInMemory(filter);
    }
}
