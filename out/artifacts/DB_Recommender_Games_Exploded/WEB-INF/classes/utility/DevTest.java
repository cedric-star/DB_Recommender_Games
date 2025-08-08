package utility;

import servlets.BewertungBean;
import servlets.JdbcQueryBean;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;

public class DevTest {

    public static void main(String[] args) {
        System.out.println("Hello Obama");
        String user = "Pomni";
        String pass = "pom";
        int userID = getUserID(user);

        JdbcQueryBean bean = new JdbcQueryBean();
        bean.executeGetProductRecommendations(userID,5, "25, 26", 2);

    }

    public static void similarUsersTest(int userID) {
        JdbcQueryBean jdbcQueryBean = new JdbcQueryBean();
        ResultSet rs = null;
        try {
            rs = jdbcQueryBean.executeGetSimilarUsers(String.valueOf(userID), 2);
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (SQLDataException e) {
            e.printStackTrace();
            System.out.println("Eww.");
        } catch (SQLException e) {
            System.out.println("Unable to get similar users.");
        } catch (Exception e) {
            System.out.println("Other exception kind.");
        }

        BewertungBean bean = new BewertungBean();
        //bean.showEmpfehlungen(jdbcQueryBean, 10);

    }

    public static void registerTest(String user, String pass) {

        JdbcQueryBean jdbcQueryBean = new JdbcQueryBean();
        try {
            jdbcQueryBean.register(user, pass);
            System.out.println("Successfully inserted " + user + " with " + pass);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loginTest(String user, String pass) {
        JdbcQueryBean jdbcQueryBean = new JdbcQueryBean();

        boolean loginResult = jdbcQueryBean.baseLogin(user, pass);
        if (loginResult) {
            System.out.println("Successfully logged in");
        } else {
            System.out.println("Failed to log in");
        }
    }

    public static void showRatingsTest(String userName, int user, int myRandom) throws SQLException {
        JdbcQueryBean jdbcQueryBean = new JdbcQueryBean();

        ResultSet results = jdbcQueryBean.executeGetRatingList(user, myRandom);

        while(results.next()) {
            System.out.println(userName + " has rated " + results.getString(2) + " (" + results.getString(5) + ") with rating: " + results.getString(3));
        }
    }

    public static void ratingCountTest(int id, String name) throws SQLException {
        JdbcQueryBean jdbcQueryBean = new JdbcQueryBean();

        ResultSet rs = jdbcQueryBean.executeGetRatingCount(id);

        int count = 0;
        while(rs.next()) {
            count = rs.getInt(1);
        }

        System.out.println("User " + name + " has rated a total of " + count + " products.");
    }

    public static void rateProductTest(String[] ratings, String[] prodIDs, int userID) {
        JdbcQueryBean jdbcQueryBean = new JdbcQueryBean();

        jdbcQueryBean.executeUpdateRatings(ratings, prodIDs, userID);
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

    private static int getUserID(String user) {
        JdbcQueryBean jdbcQueryBean = new JdbcQueryBean();

        try {
            return jdbcQueryBean.executeGetUserID(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
