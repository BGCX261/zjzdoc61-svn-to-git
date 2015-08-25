package nc.bs.ajaxnc.tools;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

import nc.bs.ajaxnc.dao.BasePageDao;
import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.frame.tool.Toolkit;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.BeanHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

public class WebTool {

	
	public static String getValue(DocVO vo, String strCode) {
		Object objRet = getValueObject(vo, strCode);
		return getValueDefault(objRet, "&nbsp");
	}

	public static boolean isRightAlign(Object objValue) {
		return (objValue != null && (objValue instanceof UFDouble || objValue instanceof Double || objValue instanceof Integer));
	}

	public static String getValueDefault(Object objRet, String strDefault) {
		String strRet = strDefault;
		if (!Toolkit.isNull(objRet)) {
			strRet = objRet.toString().trim();
		}
		return strRet;
	}

	public static String getValueForInput(DocVO vo, String strCode) {
		Object objRet = getValueObject(vo, strCode);
		return getValueDefault(objRet, "");
	}

	public static Object getValueObject(DocVO vo, String strCode) {
		Object objRet = vo.getAttributeValue(strCode);
		if (Toolkit.isNull(objRet)) {
			int iTemp = strCode.indexOf(".");
			if (iTemp >= 0 && (iTemp + 1) < strCode.length()) {
				return getValueObject(vo, strCode.substring(iTemp + 1));
			}
		}
		if (!Toolkit.isNull(objRet)) {
			if (objRet instanceof UFDouble) {
				objRet = ((UFDouble) objRet).setScale(2, UFDouble.ROUND_HALF_UP);
			}else if( objRet instanceof BigDecimal){
				objRet = ( new UFDouble(objRet.toString())).setScale(2, UFDouble.ROUND_HALF_UP);
			}
		}
		return objRet;
	}

	public static DocVO makeHashVO(CircularlyAccessibleValueObject vo, String[] attrNames) {
		DocVO retVO = new DocVO();
		if (attrNames == null || attrNames.length < 1) {
			attrNames = vo.getAttributeNames();
		}
		for (int j = 0; j < attrNames.length; j++) {
			Object objValue = null;
			try {
				objValue = vo.getAttributeValue(attrNames[j]);
			} catch (Exception e) {
				LogTool.error(e);
				continue;
			}
			// String strHtmlValue = null;
			// if (Toolkit.isNull(object)) {
			// // strHtmlValue = "&nbsp";
			// strHtmlValue = "";
			// } else {
			// strHtmlValue = object.toString().trim();
			// }
			retVO.setAttributeValue(attrNames[j], objValue);
		}

		try {
			retVO.setPrimaryKey(vo.getPrimaryKey());
		} catch (BusinessException e) {
			LogTool.error(e);
		}

		return retVO;
	}

	public static DocVO[] makeHashVO(List<SuperVO> listVOs, String[] attrNames) {
		DocVO[] retVOs = null;
		if (listVOs != null && listVOs.size() > 0) {
			SuperVO[] vos = new SuperVO[listVOs.size()];
			listVOs.toArray(vos);
			retVOs = makeHashVO(vos, attrNames);
		}
		return retVOs;
	}

	public static DocVO[] makeHashVO(SuperVO[] vos, String[] attrNames) {
		DocVO[] retVOs = null;
		if (vos != null && vos.length > 0) {
			retVOs = new DocVO[vos.length];
			for (int i = 0; i < retVOs.length; i++) {
				if (attrNames == null || attrNames.length < 1) {
					attrNames = vos[0].getAttributeNames();
				}
				retVOs[i] = makeHashVO(vos[i], attrNames);
			}
		}
		return retVOs;
	}

	@SuppressWarnings("rawtypes")
	public static SuperVO makeSuperVO(DocVO vo, String subName, String[] attrNames) {
		SuperVO retVO = null;
		try {
			retVO = (SuperVO) (Class.forName(subName)).newInstance();
			if (attrNames == null || attrNames.length < 1) {
				attrNames = vo.getAttributeNames();
			}
			for (int j = 0; j < attrNames.length; j++) {
				Object objValue = null;
				try {
					objValue = vo.getAttributeValue(attrNames[j]);
				} catch (Exception e) {
					LogTool.error(e);
					continue;
				}
				if (objValue != null) {
					Method method = BeanHelper.getMethod(retVO, attrNames[j].toLowerCase());
					Class[] paramClz = null;
					if (method != null)
						paramClz = method.getParameterTypes();
					if (paramClz != null && paramClz.length > 0) {
						if (paramClz[0].equals(UFDate.class)) {
							objValue = new UFDate(objValue.toString());

						} else if (paramClz[0].equals(UFDateTime.class)) {
							objValue = new UFDateTime(objValue.toString());

						} else if (paramClz[0].equals(UFDouble.class)) {
							objValue = new UFDouble(objValue.toString());

						} else if (paramClz[0].equals(UFBoolean.class)) {
							objValue = new UFBoolean(objValue.toString());

						} else if (paramClz[0].equals(Integer.class)) {
							objValue = new Integer(objValue.toString().trim().length() == 0 ? "0" : objValue.toString().trim());
						}

						retVO.setAttributeValue(attrNames[j].toLowerCase(), objValue);
					}
				}
			}
			retVO.setPrimaryKey(vo.getPrimaryKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVO;
	}

	@SuppressWarnings("rawtypes")
	public static SuperVO[] makeSuperVO(DocVO[] vos, String subName, String[] attrNames) {
		SuperVO[] retVOs = null;
		if (vos != null && vos.length > 0) {
			Class clz = null;
			try {
				clz = Class.forName(subName);
			} catch (ClassNotFoundException e) {
				LogTool.error(e);
			}
			retVOs = (SuperVO[])Array.newInstance(clz, vos.length);
			for (int i = 0; i < retVOs.length; i++) {
				if (attrNames == null || attrNames.length < 1) {
					attrNames = vos[0].getAttributeNames();
				}
				retVOs[i] = makeSuperVO(vos[i], subName, attrNames);
			}
		}
		return retVOs;
	}

	public static String getShowNameUser(String cuserid){
		String strRet = "";
		if(cuserid!=null){
			try {
				DocVO[] hash = BasePageDao.query("select user_name from sm_user where cuserid=?", new Object[] { cuserid });
				if (hash != null && hash.length > 0){
					strRet = (String) hash[0].getAttributeValue("USER_NAME");
				}
			} catch (Exception e) {
				LogTool.error(e);
			}
		}
		return strRet;
	}

	public static String getShowNameDept(String pk_deptdoc){
		String strRet = "";
		if(pk_deptdoc!=null){
//			try {
////				Deptd dept = CacheTool.getVoDept(pk_deptdoc);
////				strRet = dept.getDeptname();
//			} catch (BusinessException e) {
//				LogTool.error(e);
//			}
		}
		return strRet;
	}
	
	

	public static String getShowNamePsndoc(String pk_psndoc){
		String strRet = "";
		if(pk_psndoc!=null){
//			try {
//				PsndocVO vo = CacheTool.getVoPsndoc(pk_psndoc);
//				strRet = vo.getPsnname();
//			} catch (BusinessException e) {
//				LogTool.error(e);
//			}
		}
		return strRet;
	}
	

	public static String getShowNameCostsubj(String pk_costsubj){
		String strRet = "";
		if(pk_costsubj!=null){
//			try {
//				CostsubjVO vo = CacheTool.getVoCostsubj(pk_costsubj);
//				strRet = vo.getCostname();
//			} catch (BusinessException e) {
//				LogTool.error(e);
//			}
		}
		return strRet;
	}
}
