package utility;

import java.sql.SQLException;

public class DevTest {

    public static void main(String[] args) {
        String myQuery = "";
        try {
            myQuery = QueryReader.getQuery("insertUser");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(myQuery);

        System.out.println("Hello Obama");

        //PreparedStatement stm = c.prepareStatement(Queries.getQuery("update_query"));
    }

}
