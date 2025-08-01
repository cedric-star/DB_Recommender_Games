package utility;

import servlets.JdbcQueryBean;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DevTest {

    public static void main(String[] args) {
        System.out.println("Hello Obama");
        //passTest();
        //PreparedStatement stm = c.prepareStatement(Queries.getQuery("update_query"));
        String user = "Lizzy";
        String pass = "JMyBeloved";
        //registerTest(user, pass);
        //loginTest(user, pass);
        int userID = getUserID(user);

        try {
            ratingCountTest(userID, user);
            showRatingsTest(user, userID, 50);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String[] ratings = {"2", "9"};
        String[] prodIDs = {"2", "3"};

        rateProductTest(ratings, prodIDs, userID);

        try {
            ratingCountTest(userID, user);
            showRatingsTest(user, userID, 50);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

        ResultSet rs = jdbcQueryBean.executeGetAnzahlBewertungen(id);

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
