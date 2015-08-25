package nc.bs.ajaxnc.dao;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import nc.bs.logging.Logger;
import nc.jdbc.framework.ConnectionFactory;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.util.DBUtil;
import nc.pub.mdm.frame.BaseService;
import nc.vo.mdm.frame.DocVO;
import nc.vo.mdm.frame.DocVOProcessor;

/**
 * @author zhouhaimao
 * @since 2011-03-22
 */
public class BasePageDao {
	
	public static int count(String strSQL, Object[] params) throws Exception {
		strSQL = "select count(*) total from (" + strSQL + ")";
		SQLParameter parameter = BaseService.makeParam(params);
		Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(strSQL);
			ps.clearParameters();
			if (parameter != null) {
				DBUtil.setStatementParameter(ps, parameter);
			}
			ps.setFetchSize(1);
			ResultSet rs = ps.executeQuery();
			rs.next();
			int iCount = rs.getInt("total");
			return iCount;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new Exception(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage(), e);
					throw new Exception(e);
				}
			}
		}

	}
	public static Connection getConnection() throws SQLException {
		//String strDsName = InvocationInfoProxy.getInstance().getUserDataSource();
		//if ("design".equals(strDsName)) {
		//	strDsName = BaseService.getDsName();
		//}
		String strDsName = BaseService.getDefaultDataSource();
		return ConnectionFactory.getConnection(strDsName);
	}


	@SuppressWarnings("rawtypes")
	public static List query(Connection conn, BaseProcessor processor, String strSQL, Object[] params, int iPage, int iPageSize) throws Exception {
		Logger.debug("iPage=" + iPage + " iPageSize=" + iPageSize);
		SQLParameter parameter = BaseService.makeParam(params);
		List voList = null;
		try {
			PreparedStatement ps = conn.prepareStatement(strSQL);
			ps.clearParameters();
			if (parameter != null) {
				DBUtil.setStatementParameter(ps, parameter);
			}
			int iMaxRow = iPage * iPageSize;
			ps.setMaxRows(iMaxRow);
			
			int iFirstRow = (iPage - 1) * iPageSize;
			ResultSet rs = ps.executeQuery();
			if (iFirstRow > 0) {
				rs.absolute(iFirstRow);
			}
			voList = (List) processor.handleResultSet(rs);
			
		} catch (Exception e) {
			Logger.error(e.getMessage(),e);
			throw new Exception(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage(),e);
					throw new Exception(e);
				}
			}
		}
		return voList;
	}

	public static DocVO[] query(String strSQL, Object[] params) throws Exception {
		return query(strSQL, params, 0, 0, null, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static DocVO[] query(String strSQL, Object[] params, int iPage, int iPageSize, String strTable, String strPKField) throws Exception {
		BaseProcessor processor = new DocVOProcessor(strTable, strPKField);
		List voList = query( getConnection(), processor, strSQL, params, iPage, iPageSize);
		DocVO[] vos = (DocVO[]) Array.newInstance(DocVO.class, voList.size());
		voList.toArray(vos);
		return vos;
	}
	
}
