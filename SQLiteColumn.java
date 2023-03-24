import java.util.ArrayList;


public class SQLiteColumn {

    public static final SQLiteColumnOption NOT_NULL = SQLiteColumnOption.NOT_NULL;
    public static final SQLiteColumnOption PRIMARY_KEY = SQLiteColumnOption.PRIMARY_KEY;
    public static final SQLiteColumnOption AUTO_INCREMENT = SQLiteColumnOption.AUTO_INCREMENT;
    public static final SQLiteColumnOption UNIQUE = SQLiteColumnOption.UNIQUE;


    private final String columnName;
    private final String columnType;
    private final SQLiteColumnOption[] columnOptions;
    private final String columnSQL;

    public SQLiteColumn(String columnName, String columnType, SQLiteColumnOption... columnOptions) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnOptions = columnOptions;
        this.columnSQL = null;
    }

    public SQLiteColumn(String columnSQL) {
        this.columnSQL = columnSQL;
        this.columnName = null;
        this.columnType = null;
        this.columnOptions = null;
    }

    public static String makeColumnQuery(SQLiteColumn[] columns) {
        ArrayList<String> sqls = new ArrayList<>();
        for (SQLiteColumn column : columns) {
            ArrayList<String> optionQuery = new ArrayList<>();
            optionQuery.add(column.columnName+" "+column.columnType);
            for (SQLiteColumnOption option : column.columnOptions) {
                optionQuery.add(option.getQuery());
            }
            String optionSQL = String.join(" ",optionQuery);
            sqls.add(optionSQL);
        }
        return String.join(",",sqls);
    }

}
