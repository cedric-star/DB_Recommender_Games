package utility;

import servlets.JdbcQueryBean;
import servlets.LoginBean;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import javax.servlet.http.*;

public class DevTest {

    public static void main(String[] args) {
        System.out.println("Hello Obama");
        //passTest();
        //PreparedStatement stm = c.prepareStatement(Queries.getQuery("update_query"));
        //registerTest();
        loginTest();
    }

    public static void registerTest() {
        String user = "Testbama2";
        String pw = "jannes2";

        JdbcQueryBean jdbcQueryBean = new JdbcQueryBean();
        try {
            jdbcQueryBean.register(user, pw);
            System.out.println("Successfully inserted " + user + " with " + pw);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loginTest() {
        String user = "Testbama2";
        String pw = "jannes2";
        JdbcQueryBean jdbcQueryBean = new JdbcQueryBean();

        boolean loginResult = jdbcQueryBean.baseLogin(user, pw);
        if (loginResult) {
            System.out.println("Successfully logged in");
        } else {
            System.out.println("Failed to log in");
        }

    }

    public static void passTest() {
        String myPass = "MySex";
        System.out.println(myPass);
        try {
            System.out.println(PasswordProcessor.getHashedPW(myPass, PasswordProcessor.getRandomSalt()));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void queryTest() {
        String myQuery = "";
        try {
            myQuery = QueryReader.getQuery("insertUser");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(myQuery);
    }

}
