package nc.pub.mdm.frame.tool;

import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.md.model.IBusinessEntity;
import nc.pub.mdm.frame.BaseService;
import nc.pub.mdm.frame.BaseTimeMapFactory;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillOperaterEnvVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletVO;

/**
 * 单据模版工具，主要提供单据模版对应的表名、主键字段<br>
 * 之所以在public端，是为了以后jsp调用！
 * 
 * @author 周海茂
 * @since 2012-09-12
 * 
 */
public class TempletTool {

	public static String getTableCode(BillTempletVO tvo) {
		String strTableCode = null;
		IBusinessEntity ibe = tvo.getHeadVO().getBillMetaDataBusinessEntity();
		if (ibe != null) {
			strTableCode = ibe.getTable().getID();

		} else {
			BillTabVO[] tabVOs = tvo.getHeadVO().getStructvo().getBillTabVOs();
			if (tabVOs != null && tabVOs.length > 0) {
				if (tabVOs.length == 1) {
					// return tabVOs[0].getTabcode();
				}
				strTableCode = tabVOs[0].getTabcode();
			}
		}
		return strTableCode;
	}

	public static String getTableName(BillTempletVO tvo) {
		BillTabVO[] tabVOs = tvo.getHeadVO().getStructvo().getBillTabVOs();
		if (tabVOs != null && tabVOs.length > 0) {
			if (tabVOs.length == 1) {
				// return tabVOs[0].getTabcode();
			}
			return tabVOs[0].getTabname();
		}
		return null;
	}

	public static String getTablePkField(BillTempletVO tvo) {
		String strPkField = null;
		IBusinessEntity ibe = tvo.getHeadVO().getBillMetaDataBusinessEntity();
		if (ibe != null) {
			strPkField = ibe.getTable().getPrimaryKeyName();

		} else {
			strPkField = BaseService.getTablePKField(getTableCode(tvo), null);
		}
		return strPkField;
	}

	public static String getTempletCode(BillTempletVO tvo) {
		return tvo.getHeadVO().getPkBillTypeCode();
	}

	public static String getTempletName(BillTempletVO tvo) {
		return tvo.getHeadVO().getBillTempletCaption();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static BillTempletVO queryTemplet(String strBillType, String strOperator, String strOrg, String strTempletPK, String strBusiType, String nodeKey, String orgtType) {

		BillOperaterEnvVO envvo = new BillOperaterEnvVO();
		envvo.setBilltype(strBillType);
		envvo.setOperator(strOperator);
		envvo.setCorp(strOrg);
		envvo.setBilltemplateid(strTempletPK);
		envvo.setOrgtype(orgtType);
		envvo.setBusitype(strBusiType);
		envvo.setNodekey(nodeKey);
		
		String strKey = envvo.getCEKey();
		Map tmap = BaseTimeMapFactory.getMap(BillTempletVO.class.getName());
		BillTempletVO vo = (BillTempletVO) tmap.get(strKey);
		if( vo==null ){
			try {
				IBillTemplateQry iBillTemplateQry = (IBillTemplateQry) NCLocator.getInstance().lookup(IBillTemplateQry.class.getName());
				vo = iBillTemplateQry.findBillTempletData(envvo);
				tmap.put(strKey, vo);
			} catch (BusinessException e) {
				LogTool.error(e);
			}
		}
		return vo;
	}

	public static BillTempletVO queryTempletByCode(String templetCode) {
		return queryTemplet(templetCode, null, null, null, null, null, null);
	}

	public static BillTempletVO queryTempletByPK(String strTempletPK) {
//		Map tmap = BaseTimeMapFactory.getMap(BillTempletVO.class.getName());
//		BillTempletVO vo = (BillTempletVO) tmap.get(strTempletPK);
//		if (vo == null) {
//			try {
//				IBillTemplateQry iBillTemplateQry = (IBillTemplateQry) NCLocator.getInstance().lookup(IBillTemplateQry.class.getName());
//				vo = iBillTemplateQry.findCardTempletData(strTempletPK);
//				tmap.put(strTempletPK, vo);
//			} catch (BusinessException e) {
//				LogTool.error(e);
//			}
//		}
		return queryTemplet(null, null, null, strTempletPK, null, null, null);

	}

}
