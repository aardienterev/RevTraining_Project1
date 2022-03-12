package DAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import Models.Manager;
import services.DBConnection;

public class ManagerDAOImpl implements ManagerDAO {
	private static String createManager = "{? = call addManager(?, ?, ?, ?, ?, ?, ?)}";
	private static String getById = "select manager_id, user_name, user_password, first_name, last_name, email, address, phone_number, date_created from Manager join public.UserAccount on fk_userid = user_id join contact_info on fk_infoid = info_id where manager_id = ?";
	
	@Override
	public boolean addManager(Manager ref) 
	{
		boolean flag = false;
		CallableStatement state = null;
		Connection con = null;
		
		try {
			con = DBConnection.getConnection();
			state = con.prepareCall(createManager);
			state.registerOutParameter(1, Types.INTEGER);
			
			state.setString(2, ref.getUsername());
			state.setString(3, ref.getPassword());
			state.setString(4, ref.getFirstName());
			state.setString(5, ref.getLastName());
			state.setString(6, ref.getEmail());
			state.setString(7, ref.getAddress());
			state.setString(8, ref.getPhoneNum());
			
			if(state.executeUpdate() == 0)
			{
				ref.setId( state.getInt(1) );
				flag = true;
			}	
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			DBConnection.closeConnection(con);
			
			try 
			{
				state.close();
			} catch (NullPointerException|SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return flag;
	}

	@Override
	public Manager getById(int id) 
	{
		Manager ref = null;
		PreparedStatement state = null;
		Connection con = null;
		
		try {
			con = DBConnection.getConnection();
			state = con.prepareStatement(getById);
			
			state.setInt(1, id);
			
			if(state.execute())
			{
				ResultSet set = state.getResultSet();
				if(set.next())
				{
					ref = new Manager(Integer.valueOf(set.getString(1)), set.getString(2), set.getString(3), set.getString(4),
							set.getString(5), set.getString(6), set.getString(7), set.getString(8), set.getDate(9));
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			DBConnection.closeConnection(con);
		}
		
		return ref;
	}

}