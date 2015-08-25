package nc.vo.mdm.frame;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import nc.jdbc.framework.processor.BaseProcessor;

/**
 * @since 2012-03-29
 */
public class MenuVOProcessor  extends BaseProcessor {

	private static final long serialVersionUID = 2855074237096449013L;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object processResultSet(ResultSet rs) throws SQLException {
		ArrayList alRet = new ArrayList();
		if (rs != null) {
			while (rs.next()) {
				// NC60 fun_code => funcode
				//String strNC60 ="select cfunid,parent_id,fun_name,fun_code from sm_funcregister";// " where fun_code like 'AN%' ";//AjaxNC and it's sub function
				MenuVO vo = new MenuVO();
				vo.setMenuCode( rs.getString("cfunid") );
				vo.setMenuParentCode( rs.getString("parent_id") );
				vo.setMenuName( rs.getString("fun_name") );
				vo.setMenuCode( rs.getString("fun_code") );
				alRet.add(vo);
			}
		}
		return alRet;
	}
}
