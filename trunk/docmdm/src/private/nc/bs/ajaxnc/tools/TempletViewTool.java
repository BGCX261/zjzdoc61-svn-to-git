package nc.bs.ajaxnc.tools;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nc.bs.ajaxnc.constant.ILoginConstant;
import nc.bs.ajaxnc.dao.BasePageDao;
import nc.bs.ajaxnc.ncv6.web.TempletView;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.pub.mdm.frame.BaseTimeMapFactory;
import nc.pub.mdm.frame.tool.CacheTool;
import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.frame.tool.TempletTool;
import nc.pub.mdm.frame.tool.Toolkit;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.sm.UserVO;

public class TempletViewTool {

	public static String findNodeTempletId(String billtypeCode) {

		String id = null;
		String strSQL = "select pk_templet from pub_billtemplet where isnull(dr,0)=0 and pk_billtypecode=?";
		try {
			DocVO[] vos = BasePageDao.query(strSQL, new Object[] { billtypeCode });
			if (vos != null && vos.length == 1) {
				id = (String) vos[0].getAttributeValue("pk_templet");
			} else {
				throw new RuntimeException("该单据类型[" + billtypeCode + "]有多个单据模版！");
			}
		} catch (Exception e) {
			LogTool.error(e);
		}
		return id;

	}
	
	public static String findMenuCode(HttpServletRequest request){
		String menuCode = request.getParameter(ILoginConstant.PARAM_MENUCODE);
		if (menuCode == null) {
			menuCode = request.getParameter(ILoginConstant.PARAM_TEMPLET_CODE);
		}
		return menuCode;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static TempletView findTempletView(HttpServletRequest request) {
		Map tmap = BaseTimeMapFactory.getMap(TempletView.class.getName());
		UserVO user = RequestTool.getLoginUser(request);
		String strUserPK = user.getCuserid();

		TempletView tv = null;
		String strBillType = findMenuCode(request);

		BillTempletVO tmpltVO = null;
		String strTempletKey = null;
		try {
			if (!Toolkit.isNull(strBillType)) {

				strTempletKey = "Requset_UserKey_" + strBillType + "_" + strUserPK;
				tv = (TempletView) tmap.get(strTempletKey);
				if (tv == null) {
					tmpltVO = TempletTool.queryTempletByCode(strBillType);//(strBillType, null, null, null, null, null, null);//(strBillType, null, strUserPK, null, null, null);
				}
			} else {
				String strTempletPK = request.getParameter(ILoginConstant.PARAM_TEMPLET_PK);
				if( strTempletPK!=null ){
					tmpltVO = TempletTool.queryTempletByPK(strTempletPK);
					strTempletKey = "Requset_UserKey_" + strTempletPK + "_" + strUserPK;
				}else{
					LogTool.error("Request中的模版编码(主键)信息不足，无法查找模版！");
				}
			}
		} catch (Exception e) {
			LogTool.error(e);
		}
		
		if (tmpltVO != null && strTempletKey != null) {
			tv = new TempletView(tmpltVO);
			tmap.put(strTempletKey, tv);
		}
		return tv;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static TempletView findTempletView(String pk_templet) {
		Map tmap = BaseTimeMapFactory.getMap(TempletView.class.getName());
		TempletView tv = (TempletView) tmap.get(pk_templet);
		if (tv == null) {
			tv = new TempletView(pk_templet);
			tmap.put(pk_templet, tv);
		}
		return tv;
	}

	public static String getKeyofItem(BillTempletBodyVO itemVO) {
		if (itemVO == null) {
			return null;
		}
		return itemVO.getTable_code() + "_" + itemVO.getItemkey();
	}

	public static String getKeyofTab(BillTabVO tabVO) {
		if (tabVO == null) {
			return null;
		}
		return tabVO.getPos() + "_" + tabVO.getTabcode();
	}

	public static String getKeyofTab(BillTempletBodyVO bodyVO) {
		if (bodyVO == null) {
			return null;
		}
		return bodyVO.getPos() + "_" + bodyVO.getTable_code();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static BillTempletVO getTempletVO(String pk_templet) {
		BillTempletVO templetVO = null;
		try {
			Map cache = CacheTool.initCache(BillTempletVO.class, "pk_templet");
			templetVO = (BillTempletVO) cache.get(pk_templet);
			if (templetVO == null) {
				IBillTemplateQry tq = (IBillTemplateQry) NCLocator.getInstance().lookup(IBillTemplateQry.class.getName());
				templetVO = tq.findTempletData(pk_templet);
				cache.put(pk_templet, templetVO);
			}
		} catch (Exception e) {
			LogTool.error(e);
		}
		return templetVO;
	}

	public static boolean isEdit(boolean isEditCardPanel, BillTempletBodyVO itemVO) {
		// cardflag
		// showflag
		// editflag
		// listflag
		// listshowflag
		return (isEditCardPanel && itemVO.getEditflag() && itemVO.getCardflag() && itemVO.getShowflag());
	}

	public static void sortBodyVOsByShowOrder(final BillTempletBodyVO[] bodys) {
		if (bodys == null || bodys.length == 0)
			return;
		final Comparator<BillTempletBodyVO> c = new Comparator<BillTempletBodyVO>() {
			public int compare(final BillTempletBodyVO o1, final BillTempletBodyVO o2) {
				final Integer i1 = o1.getShoworder();
				final Integer i2 = o2.getShoworder();
				return (i1 == null ? 0 : i1.intValue()) - (i2 == null ? 0 : i2.intValue());
			}
		};
		Arrays.sort(bodys, c);
	}

	public static void sortTempletTabByPosIndex(BillTabVO[] vos) {
		if (vos == null || vos.length == 0)
			return;

		// first sort by Tabindex
		final Comparator<BillTabVO> cp = new Comparator<BillTabVO>() {
			public int compare(final BillTabVO tab1, final BillTabVO tab2) {

				final Integer pos1 = tab1.getPos();
				final Integer pos2 = tab2.getPos();
				final Integer index1 = tab1.getTabindex();
				final Integer index2 = tab2.getTabindex();
				int iRet = pos1.compareTo(pos2);
				if (iRet == 0) {
					iRet = index1.compareTo(index2);
				}
				return iRet;
			}
		};
		Arrays.sort(vos, cp);
	}
}
