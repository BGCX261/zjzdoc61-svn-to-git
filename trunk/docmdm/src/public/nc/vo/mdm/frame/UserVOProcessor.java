package nc.vo.mdm.frame;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import nc.jdbc.framework.processor.BaseProcessor;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.sm.UserVO;

/**
 * @since 2012-03-29
 */
public class UserVOProcessor  extends BaseProcessor {

	private static final long serialVersionUID = 2855074237096449013L;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object processResultSet(ResultSet rs) throws SQLException {
		// String strNC60 ="cuserid,disabledate,dr,enablestate,islocked,pk_group,pk_org,pwdlevelcode,pwdparam
		// ,user_code,user_name,user_note,user_password,user_type from sm_user";
		ArrayList alRet = new ArrayList();
		if (rs != null) {
			while (rs.next()) {
				UserVO vo = new UserVO();
				vo.setPrimaryKey(rs.getString("cuserid"));
				
				String disabledate = rs.getString("disabledate");
				vo.setDisabledate( disabledate==null ?null:new UFDate(disabledate));
				
				Integer dr = rs.getInt("dr");
				vo.setDr( dr==null ?null: dr);
				
				Integer enablestate = rs.getInt("enablestate");
				vo.setEnablestate(enablestate==null ?null:enablestate);
				
				String islocked = rs.getString("islocked");
				vo.setIsLocked( islocked==null ?null:new UFBoolean(islocked));
				
				String pk_group = rs.getString("pk_group");
				vo.setPk_group( pk_group==null ?null:pk_group );
				
				String pk_org = rs.getString("pk_org");
				vo.setPk_org( pk_org==null ?null:pk_org);
				
				String pwdlevelcode = rs.getString("pwdlevelcode");
				vo.setPwdlevelcode( pwdlevelcode==null ?null:pwdlevelcode );
				
				String pwdparam = rs.getString("pwdparam");
				vo.setPwdparam( pwdparam==null ?null:pwdparam );
				
				// ,user_code,user_name,user_note,user_password,user_type from sm_user";
				String user_code = rs.getString("user_code");
				vo.setUser_code( user_code==null ?null:user_code );
				
				String user_name = rs.getString("user_name");
				vo.setUser_name( user_name==null ?null:user_name );
				
				String user_note = rs.getString("user_note");
				vo.setUser_note( user_note==null ?null:user_note );
				
				String user_password = rs.getString("user_password");
				vo.setUser_password( user_password==null ?null:user_password );
				
				Integer user_type = rs.getInt("user_type");
				vo.setUser_type( user_type==null ?null:user_type );
				alRet.add(vo);
			}
		}
		return alRet;
	}
}
