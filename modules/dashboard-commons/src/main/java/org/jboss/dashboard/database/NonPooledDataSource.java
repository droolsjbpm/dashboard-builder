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
package org.jboss.dashboard.database;

import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;

/**
 * A data source implementation that bounds every connection to the underlying thread.
 */
public class NonPooledDataSource implements DataSource {

    private static transient org.slf4j.Logger log = LoggerFactory.getLogger(NonPooledDataSource.class.getName());
    protected PrintWriter printWriter;
    protected int loginTimeOut;

    // Data source properties
    protected String url;
    protected String user;
    protected String password;
    protected String driver;
    protected int isolation;
    protected boolean autoCommit;

    public NonPooledDataSource() {
        this.loginTimeOut = 0;
        this.printWriter = new PrintWriter(System.out);
        this.autoCommit = false;
        this.isolation = Connection.TRANSACTION_SERIALIZABLE;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getIsolation() {
        return isolation;
    }

    public void setIsolation(int isolation) {
        this.isolation = isolation;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    // javax.sql.DataSource implementation

    public int getLoginTimeout() throws SQLException {
        return loginTimeOut;
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        this.loginTimeOut = seconds;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return printWriter;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        this.printWriter = out;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, user, password);
            setAutoCommit(conn, autoCommit);
            setIsolation(conn, isolation);
            return conn;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Logger getParentLogger() {
        return null;
    }

    protected boolean getAutoCommit(Connection conn) {
        try {
            return conn.getAutoCommit();
        } catch (SQLException e) {
            // Ignore problems when trying to get autocommit.
            // In some environments (Presidencia - Informix) when trying to get autocommit an exception is thrown.
            log.debug("Can not get autocommit.", e);
            return true;
        }
    }

    protected void setAutoCommit(Connection conn, boolean autocommit) {
        try {
            if (getAutoCommit(conn) != autocommit) {
                conn.setAutoCommit(autocommit);
            }
        } catch (SQLException e) {
            // Ignore problems when trying to change autocommit.
            // In some environments (Presidencia - Informix) when trying to set autocommit=true an exception is thrown.
            log.debug("Can not set autocommit.", e);
        }
    }

    protected void setIsolation(Connection conn,int isolation) {
        try {
            if (conn.getTransactionIsolation() != isolation) {
                conn.setTransactionIsolation(isolation);
            }
        } catch (SQLException e) {
            log.debug("Can not set connection isolation.", e);
        }
    }
}
