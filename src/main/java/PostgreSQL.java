import java.sql.Connection;
import java.sql.DriverManager;

public class PostgreSQL {
    private String PSQL_IP;
    private String PSQL_PORT;

    public PostgreSQL(String PSQL_IP, String PSQL_PORT) {
        this.PSQL_PORT = PSQL_PORT;
        this.PSQL_IP = PSQL_IP;
    }

    public void connectToDB() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://" + PSQL_IP + ":" + PSQL_PORT + "/watsondb",
                            "postgres", "123");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("** Opened PostgreSQL. **");
    }
}
