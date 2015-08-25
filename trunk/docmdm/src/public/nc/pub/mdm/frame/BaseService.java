package nc.pub.mdm.frame;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.ddc.IBizObjStorage;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.itf.uif.pub.IUifService;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IColumn;
import nc.md.model.IForeignKey;
import nc.md.model.ITable;
import nc.md.model.MetaDataException;
import nc.md.model.impl.MDBean;
import nc.mddb.model.impl.Column;
import nc.mddb.model.impl.ForeignKey;
import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.frame.tool.Toolkit;
import nc.vo.mdm.frame.DocVO;
import nc.vo.mdm.frame.DocVOProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ddc.datadict.DatadictNode;
import nc.vo.pub.ddc.datadict.TableDef;
import nc.vo.sm.UserVO;

/**
 * NC61后台服务代理
 * 
 * @author 周海茂
 * @since 2012-03-29
 */
public class BaseService {

	public static String dsName = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static Map<Class, Map> clazzMap = BaseTimeMapFactory.getMap(HashMap.class.getName());

	public static Object excuteQuery(String strSQL, Object[] params, ResultSetProcessor processor) throws BusinessException {
		Object retObj = null;
		SQLParameter parameter = makeParam(params);
		if (RuntimeEnv.getInstance().isThreadRunningInServer()) {
			retObj = new BaseDAO(getDefaultDataSource()).executeQuery(strSQL, parameter, processor);
		} else {
			getServiceParam().executeQuery(strSQL, parameter, processor);
		}
		return retObj;
	}

	public static int executeUpdate(String strSQL) throws BusinessException {
		int iRows = 0;
		if (RuntimeEnv.getInstance().isThreadRunningInServer()) {
			iRows = new BaseDAO(getDefaultDataSource()).executeUpdate(strSQL);
		}
		return iRows;
	}

	public static String getDefaultDataSource() {
		return dsName;
	}

	public static IUifService getService() {
		setThreadDsName(getDefaultDataSource());
		IUifService service = (IUifService) NCLocator.getInstance().lookup(IUifService.class.getName());
		return service;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IUAPQueryBS getServiceParam() {
		IUAPQueryBS impl = null;
		if (RuntimeEnv.getInstance().isThreadRunningInServer()) {
			try {
				//
				// public static BaseDAO getBaseDao(String strDataSource) {
				// return new BaseDAO(strDataSource);
				// }

				Class daoClz = Class.forName("nc.bs.dao.BaseDAO");
				Constructor creater = daoClz.getConstructor(String.class);
				String strDS = getDefaultDataSource();
				impl = (IUAPQueryBS) creater.newInstance(strDS);
			} catch (Exception e) {
				LogTool.error(e);
			}
		} else {
			impl = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		}
		return impl;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getSuperVoMap(Class voClz) {
		Map mapTemp = BaseTimeMapFactory.getMap(voClz.getName());
		if (mapTemp.size() == 0) {
			try {
				if (SuperVO.class.isAssignableFrom(voClz)) {
					SuperVO[] vos = queryByCondition(voClz, null);
					for (int i = 0; i < vos.length; i++) {
						mapTemp.put(vos[i].getPrimaryKey(), vos[i]);
					}
				}
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
		return mapTemp;
	}

	public static String getTableCodeField(String strTableName, String strDataSource) {
		ITable table = getTableMD(strTableName);
		if (table != null) {
			for (IColumn col : table.getColumns()) {
				String strName = col.getDisplayName();
				if (strName.endsWith("编码")) {
					return col.getName();
				} else if (col.getName().equalsIgnoreCase("vcode")) {
					return col.getName();
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static TableDef getTableDef(String strTableName, String strDataSource) {

		Map<String, TableDef> map = BaseTimeMapFactory.getMap(TableDef.class.getName());
		String key = strTableName + strDataSource;
		TableDef def = map.get(key);
		if (def == null) {
			String tableName = strTableName;
			DatadictNode dn = new DatadictNode();
			dn.setGUID(tableName);

			IBizObjStorage iBiz = (IBizObjStorage) NCLocator.getInstance().lookup("nc.itf.uap.ddc.IBizObjStorage");
			try {
				def = (TableDef) iBiz.loadObject(strDataSource, "nc.bs.pub.ddc.datadict.DatadictStorage", dn);
				map.put(key, def);
			} catch (BusinessException e) {
				LogTool.error(e);
			}
		}
		return def;
	}

	public static MDBean getTableBean(String strFullName){
		// String strFullName = tabVOs[i].getMetadataclass();
		MDBean bean = null;
		try {
			bean = (MDBean)MDBaseQueryFacade.getInstance().getBusinessEntityByFullName( strFullName );
		} catch (MetaDataException e) {
			LogTool.error(e);
		}
		return bean;
	}
	
	@SuppressWarnings("unchecked")
	public static ITable getTableMD(String strTableCode) {
		Map<String, ITable> map = BaseTimeMapFactory.getMap(ITable.class.getName());
		ITable ret = map.get(strTableCode);
		if (ret == null) {
			try {
				ret = MDBaseQueryFacade.getInstance().getTableByID(strTableCode);
			} catch (MetaDataException e) {
				LogTool.error(e);
			}
			map.put(strTableCode, ret);
		}
		return ret;
	}

	public static String getTableNameField(String strTableCode, String strDataSource) {
		ITable table = getTableMD(strTableCode);
		if (table != null) {
			for (IColumn col : table.getColumns()) {
				String strName = col.getDisplayName();
				if (strName.endsWith("名称")) {
					return col.getName();
				} else if (col.getName().equalsIgnoreCase("vname")) {
					return col.getName();
				}
			}
		}
		//
		// TableDef td = getTableDef(strTableName, strDataSource);
		// if (td != null) {
		// FieldDefList fdList = td.getFieldDefs();
		// if (fdList != null) {
		// int iCount = fdList.getCount();
		// for (int i = 0; i < iCount; i++) {
		// FieldDef fd = fdList.getFieldDef(i);
		// String strName = fd.getDisplayName();
		// if (strName.endsWith("名称")) {
		// return fd.getID();
		// } else if (fd.getID().equalsIgnoreCase("vname")) {
		// return fd.getID();
		// }
		// }
		// }
		// }
		return null;
	}

	public static String getTablePKField(String strTableCode, String strDataSource) {
		String strPKField = null;
		ITable table = getTableMD(strTableCode);
		if (table != null) {
			strPKField = table.getPrimaryKeyName();
		} else {
			TableDef td = getTableDef(strTableCode, strDataSource);
			return td == null ? null : td.getPKID();
		}
		return strPKField;
	}

	public static String getTableName(String strTableCode, String strDataSource) {
		ITable table = getTableMD(strTableCode);
		if (table != null) {
			return table.getDisplayName();
		} else {
			TableDef td = getTableDef(strTableCode, strDataSource);
			return td == null ? null : td.getTableName();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SuperVO getVoByPK(Class voClz, String strPK) throws BusinessException {
		Map mapTemp = BaseTimeMapFactory.getMap(voClz.getName());
		SuperVO vo = (SuperVO) mapTemp.get(strPK);
		if (vo == null) {
			vo = queryByPK(voClz, strPK);
			mapTemp.put(strPK, vo);
		}
		return vo;

	}

	public static UserVO getVoUser(String pk_smuser) throws BusinessException {
		return (UserVO) getVoByPK(UserVO.class, pk_smuser);
	}

	@SuppressWarnings("rawtypes")
	public static UserVO getVoUserByCode(String userCode) throws BusinessException {
		Map mapTemp = BaseTimeMapFactory.getMap("UserByCode_" + UserVO.class.getName());
		UserVO user = (UserVO) mapTemp.get(userCode);
		if (user == null) {
			IUserManageQuery query = (IUserManageQuery) NCLocator.getInstance().lookup(IUserManageQuery.class.getName());
			user = query.findUserByCode(userCode, null);
		}
		return user;
	}

	public static String insert(SuperVO vo) throws BusinessException {
		return getService().insert(vo);
	}

	public static SQLParameter makeParam(Object[] params) {
		SQLParameter param = null;
		if (params != null) {
			param = new SQLParameter();
			for (int i = 0; i < params.length; i++) {
				param.addParam(params[i]);
			}
		}
		return param;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SuperVO[] queryByCondition(Class voClass, String strWhere) throws BusinessException {
		Collection c = getServiceParam().retrieveByClause(voClass, strWhere);
		SuperVO[] vos = (SuperVO[]) Array.newInstance(voClass, c.size());
		c.toArray(vos);
		return vos;
		// return getService().queryByCondition(voClass, strWhere);
	}

	@SuppressWarnings("rawtypes")
	public static List queryByParam(Class voClass, String strWhereSQL, Object[] params) throws BusinessException {
		SQLParameter parameter = makeParam(params);
		return (List) getServiceParam().retrieveByClause(voClass, strWhereSQL, null, parameter);

	}

	public static Object queryByParam(String strSQL, Object[] params, ResultSetProcessor rsProc) throws BusinessException {
		SQLParameter parameter = makeParam(params);
		return getServiceParam().executeQuery(strSQL, parameter, rsProc);
	}

	@SuppressWarnings("rawtypes")
	public static SuperVO queryByPK(Class voClz, String strPK) throws BusinessException {
		return (SuperVO) getServiceParam().retrieveByPK(voClz, strPK);
		// return getService().queryByPrimaryKey(voClz, strPK);
	}

	public static DocVO[] queryMainData(String strTableName) throws BusinessException {
		String strSQL = "select * from " + strTableName + " where isnull(dr,0)=0";
		return queryMainData(strSQL, strTableName, null, null);
	}

	@SuppressWarnings("unchecked")
	public static DocVO[] queryMainData(String strSQL, String strTableName, String strPKField, Object[] params) throws BusinessException {
		ArrayList<DocVO> list = (ArrayList<DocVO>) queryByParam(strSQL, params, new DocVOProcessor(strTableName, strPKField));
		DocVO[] vos = null;
		if (list.size() > 0) {
			vos = new DocVO[list.size()];
			list.toArray(vos);
		}
		return vos;
	}

	public static DocVO queryMainDataByPK(String strTableName, String strPKField, String strPK) throws BusinessException {
		String strSQL = "select * from " + strTableName + " where " + strPKField + "=?";
		DocVO[] vos = queryMainData(strSQL, strTableName, strPKField, new Object[] { strPK });
		if (vos != null && vos.length > 0) {
			return vos[0];
		}
		return null;
	}

	public static DocVO[] queryMainDataByWhere(String strTableName, String strPKField, String strWhere) throws BusinessException {
		String strSQL = "select * from " + strTableName;
		if (!Toolkit.isNull(strWhere)) {
			if (strWhere.trim().toLowerCase().startsWith("where")) {
				strSQL = strSQL + " " + strWhere.trim();
			} else {
				strSQL = strSQL + " where " + strWhere.trim();
			}
		}
		DocVO[] vos = queryMainData(strSQL, strTableName, strPKField, null);

		return vos;

	}

	public synchronized static void setDefaultDataSource(String dsName) {
		BaseService.dsName = dsName;
	}

	public synchronized static void setThreadDsName(String strThreadDS) {
		if (strThreadDS == null) {
			strThreadDS = InvocationInfoProxy.getInstance().getUserDataSource();
			if (strThreadDS == null || strThreadDS.trim().length() == 0) {
				strThreadDS = getDefaultDataSource();
			}
		}
		InvocationInfoProxy.getInstance().setUserDataSource(strThreadDS);
	}

	/**
	 * @param vo
	 * @throws BusinessExceptionr
	 */
	public static void update(SuperVO vo) throws BusinessException {
		getService().update(vo);
	}

	/**
	 * @param vo
	 * @throws BusinessException
	 */
	public static void update(SuperVO[] vos) throws BusinessException {
		getService().updateAry(vos);
	}
	

	public static DocVO[] queryMainDataByWhere(String tab,String whereStr) throws BusinessException{
		ITable metadata = BaseService.getTableMD(tab);
		String pkFld=metadata.getPrimaryKeyName();
		String fkFld=getParentFldName(metadata);
		ArrayList<DocVO> rlt=getMainDataBySql(tab,pkFld,fkFld,whereStr);
		return rlt.toArray(new DocVO[0]);
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<DocVO> getMainDataBySql (String tab,String pkFld,String fkFld,String whereStr) throws BusinessException{
		String flds=tab+".vcode,"+tab+".vname";
		String tabStr=tab;
		
		if(fkFld!=null&&!"".equals(fkFld)){
			flds=flds+",tab2.vcode as parentcode ";
			tabStr+=" left join "+tab+" tab2 on tab2."+pkFld+"="+tab+"."+fkFld;
		}
		
		String sql="select "+flds+" from "+tabStr;
		if(whereStr==null||"".equals(whereStr)){
			whereStr=" 1=1 ";
		}else{
			if( whereStr.trim().toLowerCase().startsWith("where")){
				sql+=(" "+whereStr);
			}else{
				sql+=(" where "+whereStr);
			}
		}
		
		return (ArrayList<DocVO>) queryByParam(sql, null,new BeanListProcessor(DocVO.class));
	}
	
	@SuppressWarnings("unchecked")
	public static DocVO[] queryMainDataByParent(String tab,String parentcode) throws BusinessException{
		ITable metadata = BaseService.getTableMD(tab);		
		String fkFld=getParentFldName(metadata);
		String pkFld=metadata.getPrimaryKeyName();
		if(fkFld!=null&&!"".equals(fkFld)){
			String sql2="select a.vcode,a.vname,b.vcode as parentcode"
					+" from "
					+" (select * from "+tab+" m "
					+" start with m."+fkFld+" in (select s."+pkFld
						+" from "+tab+" s where s.vcode='"+parentcode+"')"
					+" connect by prior m."+pkFld+"=m."+fkFld+") a"
  					+" inner join "+tab+" b on a."+fkFld+"=b."+pkFld+"";
			ArrayList<DocVO> rlt= (ArrayList<DocVO>) queryByParam(sql2, null,new BeanListProcessor(DocVO.class));
			String whereStr=tab+".vcode='"+parentcode+"'";
			rlt.addAll(getMainDataBySql(tab,pkFld,fkFld,whereStr));
			return rlt.toArray(new DocVO[0]);
		}
		return null;
	}
	
	public static String getParentFldName(ITable metadata){
		List<IForeignKey> fkList=metadata.getForeignKeies();
		Map<String,Column> map=new HashMap<String,Column>();
		if(fkList!=null&&fkList.size()>0){
			List<IColumn> colList=metadata.getColumns();
			for(int k=0;k<colList.size();k++){
				map.put(colList.get(k).getID(), (Column) colList.get(k));
			}
			for(int i=0;i<fkList.size();i++){
				ForeignKey fk=(ForeignKey) fkList.get(i);
				String sc=fk.getStartColumnID();
				String[] scAry=sc.split("@@@");
				String st=fk.getStartTableID();
				String ec=fk.getEndColumnID();
				String[] ecAry=ec.split("@@");
				String et=fk.getEndTableID();
				if(scAry!=null&&scAry.length==2&&ecAry!=null&&ecAry.length==2){
					if(scAry[0].equals(st)&&ecAry[0].equals(et)&&st.equals(et)){
						if(ecAry[1].equalsIgnoreCase("PK")&&
								metadata.getPrimaryKeyName().equals(map.get(ec).getName())){
							return map.get(sc).getName();
						}
					}
				}
			}
		}
		return null;
	}

}
