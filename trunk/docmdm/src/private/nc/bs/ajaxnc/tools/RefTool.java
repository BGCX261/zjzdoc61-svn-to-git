package nc.bs.ajaxnc.tools;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nc.bs.ajaxnc.dao.BasePageDao;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Logger;
import nc.pub.mdm.frame.BaseTimeMapFactory;
import nc.pub.mdm.frame.tool.Toolkit;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefPubUtil;
import nc.ui.bd.ref.impl.RefModelHandlerForServer;
import nc.vo.mdm.frame.DocVO;
import nc.vo.sm.UserVO;

/**
 * @author zhouhaimao
 * @since 2012-03-29
 */
public class RefTool {

	public static AbstractRefModel getServerRef(String strRefName, HttpServletRequest request) {
		UserVO userVO = RequestTool.getLoginUser(request);
		String strPKCorp = RequestTool.getLoginCorpPK(request);
		AbstractRefModel refModel = getServerRef(strRefName, userVO, strPKCorp);
		return refModel;
	}

	
	@SuppressWarnings("unchecked")
	public static AbstractRefModel getServerRef(String strRefName, UserVO userVO, String strPkCorp) {
		boolean isSelfClass = false;
		if( strRefName!=null && strRefName.trim().indexOf("<")==0){
			strRefName = strRefName.trim();
			strRefName = strRefName.substring(1,strRefName.length()-1);
			isSelfClass = true;
		}
		Map<String, AbstractRefModel> tempMap = (Map<String, AbstractRefModel>) BaseTimeMapFactory.getMap(AbstractRefModel.class.getName());
		String strKey = strRefName ;
		if( userVO!=null ){
			strKey += ("_" + userVO.getPrimaryKey());
		}
		if( strPkCorp!=null ){
			strKey += ( "_" + strPkCorp);
		}
		
		AbstractRefModel refModel = tempMap.get(strKey);
		if (refModel == null) {

			if( userVO!=null ){
				InvocationInfoProxy.getInstance().setUserId(userVO.getUser_code());
			}
			if( strPkCorp!=null ){
				InvocationInfoProxy.getInstance().setGroupId(strPkCorp);
			}

			if( isSelfClass ){
				refModel = RefPubUtil.getRefModeByClassName(strRefName);
			}else{
				refModel = RefPubUtil.getRefModel(strRefName);
			}
			tempMap.put(strKey, refModel);
		}
		return refModel;
	}

	@SuppressWarnings("unchecked")
	protected static RefModelHandlerForServer getServerHandler(String strRefName) {
		Map<String, RefModelHandlerForServer> tempMap = (Map<String, RefModelHandlerForServer>) BaseTimeMapFactory.getMap(RefModelHandlerForServer.class.getName());
		RefModelHandlerForServer srvRef = tempMap.get(strRefName);
		if (srvRef == null) {
			AbstractRefModel refModel = RefPubUtil.getRefModel(strRefName);
			srvRef = new RefModelHandlerForServer(refModel);
			tempMap.put(strRefName, srvRef);
		}
		return srvRef;
	}
	
//	@SuppressWarnings("unchecked")
//	public static String queryRefShowValue(String strRefName, String strRefPK, UserVO userVO, String pk_corp) {
//		String strRetValue = null;
//		
//		if( Toolkit.isNull(strRefName)){
//			return "";
//		}
//		
//		int index =  strRefName.indexOf(",");
//		if( index>0 ){
//			strRefName = strRefName.substring(0,index);
//		}
//
//		AbstractRefModel refModel = null;
//		if (Toolkit.isNull(strRefPK)) {
//			return "";
//		}
//		if ("".equals(strRefName)) {
//			return strRefPK;
//		}
//
//		Map<String, String> refShowValueMap = BaseTimeMapFactory.getMap("nc.crcc.refvalue.cache.RefShowValueMap");
//		String strKey = strRefName + "_" + strRefPK;
//		strRetValue = refShowValueMap.get(strKey);
//
//		if (Toolkit.isNull(strRetValue)) {
//
//			refModel = getServerRef(strRefName, userVO, pk_corp);
//			if (refModel == null) {
//				//DefaultDefdocRefModel docModel = (DefaultDe) (RefPubUtil.getRefModeByClassName("nc.ui.bd.def.DefaultDefdocRefModel"));
//				//docModel.setPkdefdef(strRefName);
//				//refModel = docModel;
//			}
//			String strSQL = refModel.getMatchSql(new String[] { strRefPK });
//			String strNameField = refModel.getRefNameField();
//			try {
//				DocVO[] vos = BasePageDao.queryHashVO(strSQL, strSQL.contains("?") ? new Object[] { strRefPK } : null, 0, 0);
//				if (vos != null && vos.length > 0) {
//					strRetValue = WebTool.getValueForInput(vos[0], strNameField);
//				}
//			} catch (BusinessException e) {
//				Logger.error(e.getMessage(), e.getCause());
//			}
//			if (Toolkit.isNull(strRetValue)) {
//				strRetValue = "";
//			}
//
//			refShowValueMap.put(strKey, strRetValue);
//		}
//		return strRetValue;
//	}


	public static String getShowNameWithRefCode(String strRefName, String strRefCode, String pkcorp) {
		String strRetValue = null;
	
		AbstractRefModel refModel = null;
		if (Toolkit.isNull(strRefCode)) {
			return "";
		}
		refModel = RefTool.getServerRef(strRefName, null, null);
		if (refModel != null) {
			// String oldwhere=refModel.getWherePart();
			String strNameField = refModel.getRefNameField();
			String strCodeField = refModel.getRefCodeField();
			String strPKField = refModel.getPkFieldCode();
			String newwhere = " (" + strNameField + "='" + strRefCode + "' or " + strCodeField + "='" + strRefCode + "') and pk_corp='" + pkcorp + "' ";
			// refModel.setWherePart((oldwhere==null||"".equals(oldwhere)?newwhere:" and "+newwhere));
			refModel.setWherePart(newwhere);
			String strSQL = refModel.getRefSql();
	
			try {
				DocVO[] vos = BasePageDao.query(strSQL, strSQL.contains("?") ? new Object[] { strRefCode } : null);
				if (vos != null && vos.length > 0) {
					strRetValue = WebTool.getValueForInput(vos[0], strPKField);
					strRetValue += "_" + WebTool.getValueForInput(vos[0], strNameField);
	
				}
			} catch (Exception e) {
				Logger.error(e.getMessage(), e.getCause());
			}
			if (Toolkit.isNull(strRetValue)) {
				strRetValue = "";
			}
		}
		return strRetValue;
	}


	@SuppressWarnings({ "unchecked" })
	public static String getShowNameWithRefPK(String strRefName, String strRefPK, UserVO userVO, String strPkcorp) {
		String strRetValue = null;
		
		int index =  strRefName.indexOf(",");
		if( index>0 ){
			strRefName = strRefName.substring(0,index);
		}
	
		AbstractRefModel refModel = null;
		if (Toolkit.isNull(strRefPK)) {
			return "";
		}
		if ("摘要".equals(strRefName)) {
			return strRefPK;
		}
	
		Map<String, String> refShowValueMap = BaseTimeMapFactory.getMap("nc.crcc.refvalue.cache.RefShowValueMap");
		String strKey = strRefName + "_" + strRefPK;
		strRetValue = refShowValueMap.get(strKey);
	
		if (Toolkit.isNull(strRetValue)) {
	
			refModel = RefTool.getServerRef(strRefName, null, null);
			if (refModel == null) {
//				DefaultDefdocRefModel docModel = (DefaultDefdocRefModel) (RefPubUtil.getRefModeByClassName("nc.ui.bd.def.DefaultDefdocRefModel"));
//				docModel.setPkdefdef(strRefName);
//				refModel = docModel;
			}
			String strSQL = refModel.getMatchSql(new String[] { strRefPK });
			String strNameField = refModel.getRefNameField();
			try {
				Object[] params = strSQL.contains("?") ? new Object[] { strRefPK } : null;
				DocVO[] vos = BasePageDao.query(strSQL, params);
				if (vos != null && vos.length > 0) {
					strRetValue = WebTool.getValueForInput(vos[0], strNameField);
				}
			} catch (Exception e) {
				Logger.error(e.getMessage(), e.getCause());
			}
			if (Toolkit.isNull(strRetValue)) {
				strRetValue = "";
			}
	
			refShowValueMap.put(strKey, strRetValue);
		}
		return strRetValue;
	}

}
