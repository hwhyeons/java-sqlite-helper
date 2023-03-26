import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    private static void testInsert() throws Exception{
        SQLiteHelper s = new SQLiteHelper("test.db");
        s.connect();
        s.execute("CREATE TABLE IF NOT EXISTS test_table (id integer primary key, name text, age integer)");
        s.execute("INSERT INTO test_table (name,age) VALUES (\'ABC\',30)");
    }

    private static void testSelect() throws Exception{
        SQLiteHelper s = new SQLiteHelper("test.db");
        s.connect();
        s.execute("CREATE TABLE IF NOT EXISTS test_table (id integer primary key, name text, age integer)");
        s.execute("INSERT INTO test_table (name,age) VALUES (\'ABC\',30)");
        s.execute("INSERT INTO test_table (name,age) VALUES (\'DEF\',20)");
        s.execute("INSERT INTO test_table (name,age) VALUES (\'HIJ\',40)");
        List<List<Object>> list = s.execute("SELECT * FROM test_table");
        for (var l : list) {
            System.out.println(Arrays.toString(l.toArray()));
        }
    }


    public static void executeTest() throws Exception{
        SQLiteHelper s = new SQLiteHelper("test.db");
        s.connect();
        // String.format()
        var list = s.execute(String.format("select * from test_table where age = %d and name = \'%s\'",30,"ABC"));

        // execute()에 가변 인자 전달
        list = s.execute("select * from test_table where age = ? and name = ?",30,"ABC");

        for (var l : list) {
            System.out.println(Arrays.toString(l.toArray()));
        }
    }

    public static void executeManyTest() throws Exception{
        SQLiteHelper s = new SQLiteHelper("test.db");
        s.connect();

        // 2차원 배열
        Object[][] data = new Object[][]{{"A",1},{"B",2}};
        s.executeMany("INSERT INTO test_table (name,age) VALUES (?,?)",data);

        // 2차원 리스트
        List<List<Object>> insertList = new ArrayList<>();
        List<Object> l1 = new ArrayList<>();
        l1.add("C"); l1.add(3);
        List<Object> l2 = new ArrayList<>();
        l2.add("D"); l2.add(4);
        insertList.add(l1); insertList.add(l2);
        s.executeMany("INSERT INTO test_table (name,age) VALUES (?,?)",insertList);


    }
    public static void main(String[] args) throws Exception{
        executeManyTest();
    }
}
