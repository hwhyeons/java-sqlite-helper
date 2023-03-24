import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class SQLiteHelper {
    private final String dbPath;
    private Connection connection;

    public SQLiteHelper(String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection getConnectionInst() throws NullPointerException, SQLException {
        if (connection == null || connection.isClosed()) {
            throw new NullPointerException("call connect() first");
        }
        return connection;
    }

    public Connection getConnection() throws NullPointerException,SQLException{
        return getConnectionInst();
    }


    /**
     * default : AutoCommit = False
     */
    public void connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        connect(config);
    }

    /**
     * set config
     * @param config
     * @throws Exception
     */
    public void connect(SQLiteConfig config) throws Exception{
        String url = "";
        try {
            url = "jdbc:sqlite:" + this.dbPath;
            this.connection = DriverManager.getConnection(url,config.toProperties());
        } catch (SQLException e) {
            throw e;
        }
    }


    public List<List<Object>> execute(String sql, Object... data) throws Exception {
        Object[][] param = new Object[][]{data};
        return execute(sql,false,null,param);
    }

    public List<List<Object>> executeMany(String sql, List<List<?>> data) throws Exception{
        return execute(sql,true,data,null);
    }

    public List<List<Object>> executeMany(String sql, Object[][] data) throws Exception{
        return execute(sql,true,null,data);
    }


    public void setAutoCommit(boolean isAutoCommit) throws Exception {
        getConnectionInst().setAutoCommit(isAutoCommit);
    }

    public boolean getAutoCommit() throws Exception {
        return getConnectionInst().getAutoCommit();
    }

    public void close() throws SQLException {
        getConnectionInst().close();
    }

    public void commit() throws SQLException {
        getConnectionInst().commit();
    }

    public void rollback() throws SQLException {
        getConnectionInst().rollback();
    }

    private List<List<Object>> execute(String sql, boolean isMany, List<List<?>> dataList, Object[][] dataArray) throws SQLException{
        Connection connection = getConnectionInst();
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        boolean isAutoCommit = connection.getAutoCommit();
        try {
            pstmt = connection.prepareStatement(sql);
            boolean isContainResultSet = false;
            if (dataList != null) {
                for (List<?> rowData : dataList) {
                    for (int i = 0; i < rowData.size(); i++) {
                        pstmt.setObject(i+1,rowData.get(i));
                    }
                    isContainResultSet = pstmt.execute();
                    if (isContainResultSet && isMany) throw new SQLException("You cannot execute SELECT statements in executeMany()");
                }
            } else if (dataArray != null) {
                for (Object[] rowData : dataArray) {
                    for (int i = 0; i < rowData.length; i++) {
                        pstmt.setObject(i + 1, rowData[i]);
                    }
                    isContainResultSet = pstmt.execute();
                    if (isContainResultSet && isMany) throw new SQLException("You cannot execute SELECT statements in executeMany()");
                }
            } else {
                isContainResultSet = pstmt.execute();
            }
            rs = isContainResultSet ? pstmt.getResultSet() : null;
            if (!isAutoCommit) connection.commit();
            if (isContainResultSet) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                List<List<Object>> returnList = new ArrayList<>();
                while (rs.next()) {
                    List<Object> row = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getObject(i));
                    }
                    returnList.add(row);
                }
                return returnList;
            } else {
                return null;
            }
        } catch (SQLException e) {
            if (!isAutoCommit) connection.rollback(); // can not call rollback() when autoCommit
            throw e;
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                if (pstmt != null) pstmt.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<List<Object>> execute(String sql) throws SQLException {
        return execute(sql,false,null,null);
    }

    public List<List<Object>> execute(String sql, List<?> data) throws Exception {
        ArrayList<List<?>> param = new ArrayList<>();
        param.add(data);
        return execute(sql,false, param,null);
    }

    public void createTable(String tableName, boolean createIfNotExist,SQLiteColumn... columns) throws Exception {
        ArrayList<String> sqls = new ArrayList<>();
        sqls.add("CREATE TABLE");
        if (createIfNotExist) sqls.add("IF NOT EXISTS");
        sqls.add(tableName+" (");
        String columnsSQL = SQLiteColumn.makeColumnQuery(columns);
        sqls.add(columnsSQL);
        sqls.add(")");
        String createTableQuery = String.join(" ",sqls);
        execute(createTableQuery);
    }

    public boolean isTableExist(String tableName) throws Exception {
        String sqlFindTableExist = "select count(*) from sqlite_master where Name = '" + tableName + "';";
        List<List<Object>> list = execute(sqlFindTableExist);
        int count = Integer.parseInt(list.get(0).get(0).toString());
        return count != 0;
    }

    public String getColumnType(String table, String key) throws Exception {
        String sql = "select type from (SELECT * FROM pragma_table_info('" + table + "')) where name = '" + key + "'";
        return execute(sql).get(0).get(0).toString();
    }

}
