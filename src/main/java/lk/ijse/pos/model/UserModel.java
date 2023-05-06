package lk.ijse.pos.model;

import lk.ijse.pos.dto.UserDto;
import lk.ijse.pos.util.CrudUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserModel {
    public static boolean createUser(UserDto user) throws SQLException, ClassNotFoundException {
        /*Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user VALUES (?,?)");
        preparedStatement.setString(1,user.getUsername());
        preparedStatement.setString(2, BCrypt.hashpw(user.getPassword(),BCrypt.gensalt()));
        return preparedStatement.executeUpdate()>0;*/
        return CrudUtil.execute("INSERT INTO user VALUES (?,?,?,?)",user.getUsername(),BCrypt.hashpw(user.getPassword(),BCrypt.gensalt()),user.getUserEmail(),user.getUserType());
    }
    public static boolean logIn(UserDto user) throws SQLException, ClassNotFoundException {
        /*Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            if (resultSet.getString(1).equals(user.getUsername())&&BCrypt.checkpw(user.getPassword(),resultSet.getString(2))){
                return true;
            }
        }
        return false;*/
        ResultSet resultSet = CrudUtil.execute("SELECT * FROM user");
        while (resultSet.next()){
            if (resultSet.getString(1).equals(user.getUsername()) && BCrypt.checkpw(user.getPassword(),resultSet.getString(2))){
                return true;
            }
        }
        return false;
    }

    public static String getUserType(String userName) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT userType FROM user WHERE username = ?",userName);
        if (resultSet.next()){
            return resultSet.getString(1);
        }
        return "Default";
    }

    public static String getEmail(String userName) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT userEmail FROM user WHERE username = ?",userName);
        if (resultSet.next()){
            return resultSet.getString(1);
        }
        return "hasindueshandesilva@gmail.com";
    }

    public static Boolean updatePassword(String userName, String pswrd) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("UPDATE user SET password = ? WHERE username = ?",BCrypt.hashpw(pswrd,BCrypt.gensalt()),userName);
    }
}
