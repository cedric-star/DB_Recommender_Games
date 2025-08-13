package servlets;

import jakarta.servlet.http.HttpServlet;
import utility.QueryReader;
import utility.PasswordProcessor;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;


public class JdbcQueryBean extends HttpServlet {

	private Connection connection;
	private Statement statement;

	private String result = null;
	
	public int userid=0;

	private static final String driver = "oracle.jdbc.driver.OracleDriver";
	private static final String dbUrl = "jdbc:oracle:thin:@regenstein.hs-harz.de:1521:DBHSH01";
	private static final String login = "blank_future";
	private static final String password = "future123";

	private PreparedStatement ps_nutzerEinfuegen = null;
	private PreparedStatement ps_initRatings = null;
	private PreparedStatement ps_getUserPasswort = null;
	private PreparedStatement ps_getUserSalt = null;
	private PreparedStatement ps_getUserid = null;
	private PreparedStatement ps_getRatingList = null;
	private PreparedStatement ps_updateRatings = null;
	private PreparedStatement ps_getRatingCount = null;
	private PreparedStatement ps_getSimilarUsers = null;
	private PreparedStatement ps_getProductRecommendations = null;
	private PreparedStatement ps_getBeliebteProdukte = null;
	private PreparedStatement ps_getUnratedProducts = null;
	private PreparedStatement ps_getLikedProducts = null;
	private PreparedStatement ps_getContentBasedRecs = null;
	private PreparedStatement ps_initRatingsNewProducts = null;

	boolean logged = false;

	public JdbcQueryBean() {
		this.establishDBConnection();
	}

	public void establishDBConnection() {

		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(dbUrl, login, password);
			statement = connection.createStatement();
			initPreparedStatement();
		} catch (java.lang.ClassNotFoundException cnfe) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(cnfe.getMessage());
			connection = null;
		} catch (SQLException se) {
			System.err.print("SQLException: ");
			System.err.println(se.getMessage());
			connection = null;
		} catch (Exception ex) {
			System.err.print("Other Error while connecting to the database : ");
			System.err.println(ex.getMessage());
			connection = null;
		}
	}
	
	private void initPreparedStatement() {
		try {
			ps_nutzerEinfuegen = connection.prepareStatement(QueryReader.getQuery("insertUser"));
			ps_initRatings = connection.prepareStatement(QueryReader.getQuery("initRatings"));
			ps_getUserPasswort = connection.prepareStatement(QueryReader.getQuery("getUserPassword"));
			ps_getUserSalt = connection.prepareStatement(QueryReader.getQuery("getUserSalt"));
			ps_getUserid = connection.prepareStatement(QueryReader.getQuery("getUserID"));
			ps_getRatingList = connection.prepareStatement(QueryReader.getQuery("getRatingList"));
			ps_updateRatings = connection.prepareStatement(QueryReader.getQuery("updateRatings"));
			ps_getRatingCount = connection.prepareStatement(QueryReader.getQuery("getRatingCount"));
			ps_getSimilarUsers = connection.prepareStatement(QueryReader.getQuery("getSimilarUsers"));
			ps_getProductRecommendations = connection.prepareStatement(QueryReader.getQuery("getProductRecommendations"));
			ps_getBeliebteProdukte = connection.prepareStatement(QueryReader.getQuery("getPopularProducts"));
			ps_getLikedProducts = connection.prepareStatement(QueryReader.getQuery("getLikedProducts"));
			ps_getUnratedProducts = connection.prepareStatement(QueryReader.getQuery("getUnratedProducts"));
			ps_getContentBasedRecs = connection.prepareStatement(QueryReader.getQuery("getContentBasedRecs"));
			ps_initRatingsNewProducts = connection.prepareStatement(QueryReader.getQuery("initRatingsNewProducts"));
		} catch (SQLException e) {
			System.err.println("Error occured during fetching of SQL Statements: ");
			System.err.println(e.getMessage());
		}
	}

	public String getResult() {
		return result;
	}

	/**
	 * Executes a query on the blank_future database. The results are given as a resultset.
	 * The resultset can be read in a while-loop (resultset.next()). The pointer starts before the first
	 * row of data and then goes thru the rest. The resultset can contain zero or more results.
	 * @param query Query die auszuführen ist
	 * @return Resultset as described above
	 */
	public ResultSet executeQuery(String query){
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(query);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
		return rs;
	}

	public void register(String user, String pass) throws InvalidKeySpecException, NoSuchAlgorithmException {

		result = null;
		byte[] salt = PasswordProcessor.getRandomSalt();
		String hashedPW = PasswordProcessor.getHashedPW(pass, salt);

		try {
			ps_nutzerEinfuegen.setString(1, user);
			ps_nutzerEinfuegen.setString(2, hashedPW);
			ps_nutzerEinfuegen.setString(3, PasswordProcessor.getHashHex(salt));
			ps_nutzerEinfuegen.executeQuery();

			ps_initRatings.executeQuery();

		} catch (SQLException e) {
			result = "<P> SQL error: <PRE> " + e + " </PRE> </P>\n";
			System.err.println(e.getMessage());
		} catch (Exception ignored) {
			result = "<P> Error: <PRE> " + ignored + " </PRE> </P>\n";
			System.err.println(ignored.getMessage());
		}
	}

	public String getLogin() {

		if (logged) {
			return "<center><p>Login erfolgreich</p><br><a href=\"index.jsp\">weiter</a></center>";
		} else {
			return "<center><p>Login Fehlgeschlagen</p><br><a href=\"index.jsp\">weiter</a></center>";
		}

	}

	public boolean login(String user, String pass, LoginBean loginBean) {

		boolean logged = baseLogin(user, pass);
		int userID = 0;

        try {
			ps_getUserid.setString(1, user);
			ResultSet rs = ps_getUserid.executeQuery();
			while (rs.next()) {
				userID = Integer.parseInt(rs.getString(1));
			}
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (logged && userID != 0) {
			try {
				ps_initRatingsNewProducts.setInt(1, userID);
				ps_initRatingsNewProducts.setInt(2, userID);
				ps_initRatingsNewProducts.executeQuery();
			} catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
		loginBean.logged = logged;
		return logged;
	}

	public boolean baseLogin(String user, String pass) {

		try {
			ps_getUserPasswort.setString(1, user);
			ResultSet rs = ps_getUserPasswort.executeQuery();
			String dbPass = "";
			while (rs.next()) {
				dbPass = rs.getString(1);
			}

			ps_getUserSalt.setString(1, user);
			ResultSet rs2 = ps_getUserSalt.executeQuery();
			String dbSalt = "";
			while (rs2.next()) {
				dbSalt = rs2.getString(1);
			}

			String proofPW = PasswordProcessor.getHashedPW(pass, PasswordProcessor.hexStringToByteArray(dbSalt));

			if (dbPass.equals(proofPW)) {
				logged = true;
				ps_getUserid.setString(1, user);
				rs = ps_getUserid.executeQuery();
				while (rs.next()) {
					userid = Integer.parseInt(rs.getString(1));
				}
			} else {
				logged=false;
			}
		} catch (SQLException e) {
			result = "<P> SQL error: <PRE> " + e + " </PRE> </P>\n";
			System.out.println(e);
		} catch (Exception ignored) {
			result = "<P> Error: <PRE> " + ignored + " </PRE> </P>\n";
			System.out.println(ignored);
		}
		return logged;
	}

	public int executeGetUserID(String username) throws SQLException {
		ResultSet rs = null;
		try {
			ps_getUserid.setString(1, username);
			rs = ps_getUserid.executeQuery();
		} catch (SQLException e) {
            throw new RuntimeException(e);
        }

		int id = 0;
		while (rs.next()) {
			id = Integer.parseInt(rs.getString(1));
		}
		return id;
    }

	public ResultSet executeGetRatingList(int userid, int zufall) {
		ResultSet results = null;
		try {
			ps_getRatingList.setInt(1, userid);
			ps_getRatingList.setInt(2, zufall);
			results = ps_getRatingList.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	public void executeUpdateRatings(String[] ratings, String[] productids, int userid) {
		try {
			for(int i = 0; i<ratings.length;i++) {
				ps_updateRatings.setString(1, ratings[i]);
				ps_updateRatings.setInt(2, userid);
				ps_updateRatings.setString(3, productids[i]);
				ps_updateRatings.executeQuery();
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public ResultSet executeGetRatingCount(int userid) {
		ResultSet rs = null;
		try {
			ps_getRatingCount.setInt(1, userid);
			rs = ps_getRatingCount.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public ResultSet executeGetBeliebteProdukte() {
		ResultSet rs = null;
		try {
			rs = ps_getBeliebteProdukte.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	
	public ResultSet executeGetSimilarUsers(String userid, double mindestAehnlichkeit) {
		ResultSet rs = null;
		try {
			ps_getSimilarUsers.setString(1, userid);
			rs = ps_getSimilarUsers.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public ResultSet executeGetProductRecommendations (int userID, int recCount, String userList, int minRating) {
		ResultSet rs = null;
		System.out.println("User list: " + userList);
		System.out.println("UserID: " + userID);
		System.out.println("Minimum rating: " + minRating);
		System.out.println("Recommendation count: " + recCount);

		String getProductRecommendations = "WITH PRODUCT_AVERAGE_GT AS (" +
				"SELECT * FROM (" +
				"SELECT avg(RATING) AvgRating, PRODUCTID FROM (" +
				"SELECT * FROM CCJ_RATING_GENERAL WHERE USERID in (" + userList + ") AND NOT PRODUCTID in (" +
				"SELECT PRODUCTID FROM CCJ_RATING_GENERAL WHERE USERID = " + userID + " AND RATING <> 0)) GROUP BY PRODUCTID) where AvgRating > " + minRating + ") " +
				"SELECT * FROM (" +
				"SELECT PRODUCTID, PRODUCTNAME, dense_rank () over (order by AvgRating desc) rank, round (AvgRating, 2) Rating, CREATOR FROM (" +
				"SELECT avgs.PRODUCTID, prods.PRODUCTNAME, AvgRating, prods.CREATOR FROM PRODUCT_AVERAGE_GT avgs, CCJ_PRODUCTS prods where avgs.PRODUCTID = prods.PRODUCTID)) WHERE rank <= " + recCount + " ORDER BY rank ASC";

        try {
            rs = statement.executeQuery(getProductRecommendations);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

		return rs;
	}

	public ResultSet executeGetContentBasedRecs(int userID, int minRating) {
		ResultSet results = null;
		try {
			ps_getContentBasedRecs.setInt(1, userID);
			ps_getContentBasedRecs.setInt(2, minRating);
			ps_getContentBasedRecs.setInt(3, userID);
			results = ps_getContentBasedRecs.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

}
