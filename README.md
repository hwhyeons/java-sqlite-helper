# java-sqlite-helper
  
  Java SQLite
  - 자바 SQLite를 편하게 사용
  - 파이썬 sqlite3와 유사하게 작성
  - 데이터베이스 한번 연결시 connection 객체를 공유
  - 트랜잭션 컨트롤
  - 개발 환경 : Java11 / sqlite-jdbc version: '3.7.2'
  
  
<br/><br/><br/>

사용법
=============
- ### 기본 설정 (Gradle)
추가
```
implementation group: 'org.xerial', name: 'sqlite-jdbc', version: '3.7.2'
``` 
    
<br/><br/>
- ### 테이블 생성 / INSERT

```java
SQLiteHelper s = new SQLiteHelper("test.db");
s.connect(); // 데이터베이스 연결
s.execute("CREATE TABLE IF NOT EXISTS test_table (id integer primary key, name text, age integer)");
s.execute("INSERT INTO test_table (name,age) VALUES (\'ABC\',30)");
```

<br/>

다른 방법

```java
SQLiteHelper s = new SQLiteHelper("test.db");
s.connect(); // 데이터베이스 연결
SQLiteColumn c1 = 
	new SQLiteColumn("c1","integer", SQLiteColumnOption.UNIQUE);
SQLiteColumn c2 = 
	new SQLiteColumn("c2","integer", SQLiteColumnOption.UNIQUE,
SQLiteColumnOption.PRIMARY_KEY,SQLiteColumnOption.AUTO_INCREMENT);
s.createTable("table2",true,c1,c2);
```


<br/><br/>
- ### SELECT
```java
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
```
결과
```
[1, ABC, 30]
[2, DEF, 20]
[3, HIJ, 40]
```
2차원 리스트 형태로 반환
  
자바의 SQLite의 resultSet은 결과가 몇개가 나올지 바로 알 수 없음.  
따라서 동적 크기 자료구조인 ArrayList를 사용해서 resultSet을 반환


<br/><br/>
- ### execute() 방식

- #### 인자 전달 방식 execute()

```java
SQLiteHelper s = new SQLiteHelper("test.db");
s.connect();
// String.format()으로 문자열 설정
var list = s.execute(String.format("select * from test_table where age = %d and name = \'%s\'",30,"ABC"));

// execute()에 가변 인자 전달
list = s.execute("select * from test_table where age = ? and name = ?",30,"ABC");

for (var l : list) {
    System.out.println(Arrays.toString(l.toArray()));
}
```

<br/>

- ### executeMany()

- 다중 쿼리
- select 쿼리는 사용 불가

```java
SQLiteHelper s = new SQLiteHelper("test.db");
s.connect();

// 2차원 배열로 인자 전달
Object[][] data = new Object[][]{{"A",1},{"B",2}};
s.executeMany("INSERT INTO test_table (name,age) VALUES (?,?)",data);

// 2차원 리스트로 인자 전달
List<List<Object>> insertList = new ArrayList<>();
List<Object> l1 = new ArrayList<>();
l1.add("C"); l1.add(3);
List<Object> l2 = new ArrayList<>();
l2.add("D"); l2.add(4);
insertList.add(l1); insertList.add(l2);
s.executeMany("INSERT INTO test_table (name,age) VALUES (?,?)",insertList);
```
executeMany를 수행시, 하나라도 수행 되지 못하는 데이터가 있으면 전부 rollback



<br/><br/>
- ### 트랜잭션
```java
SQLiteHelper s = new SQLiteHelper("test.db");
s.setAutoCommit(false); // AutoCommit을 false로 설정 해야 함 (기본 : true)
try {
    // 아래 두 문장 중에 하나라도 예외가 발생하면, 전부 롤백
    s.execute("update table2 set c1 = ? where id = ?",321,1);
    s.execute("update table2 set c1 = ? where id = ?",654,2);
    s.commit(); // commit
} catch (SQLException e) {
    e.printStackTrace();
    s.rollback(); // rollback
}
```





