public enum SQLiteColumnOption {
    NOT_NULL("NOT NULL"),PRIMARY_KEY("PRIMARY KEY"),AUTO_INCREMENT("AUTOINCREMENT"),UNIQUE("UNIQUE");
    final String query;
    SQLiteColumnOption(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
