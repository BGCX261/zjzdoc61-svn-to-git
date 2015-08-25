package nc.ui.mdm.base.mvc;

import java.util.Map;

import nc.pub.mdm.frame.BaseService;
import nc.pub.mdm.frame.BaseTimeMapFactory;
import nc.pub.mdm.frame.tool.LogTool;
import nc.ui.mdm.base.ext.IBaseUI;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.editor.value.AbstractComponentValueAdapter;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.BusinessException;

public class BaseValueManager extends AbstractComponentValueAdapter {

	private IBaseUI ui = null;

	public IBaseUI getUi() {
		return ui;
	}

	@Override
	public Object getValue() {
		BillCardPanel bcp = (BillCardPanel) getComponent();
		DocVO vo = new DocVO();
		BillItem[] items = bcp.getBillData().getHeadTailItems();
		for (int i = 0; i < items.length; i++) {
			if ("pk_treetype".equals(items[i].getKey())) {
				unitDataSave(vo, items[i]);
			} else {
				Object value = items[i].converType(items[i].getValueObject());
				vo.setAttributeValue(items[i].getKey(), value);
			}
		}
		return vo;
	}

	public void setUi(IBaseUI ui) {
		this.ui = ui;
	}

	@Override
	public void setValue(Object object) {
		if (object == null) {
			((BillCardPanel) getComponent()).getBillData().clearViewData();

		} else if (object instanceof DocVO) {
			DocVO vo = (DocVO) object;

			// 因为DASFacade.newInstanceWithContainedObject()方法生成的元数据对象NCObject不能适配DocVO，
			// 所以无法使用setBillValueObjectByMetaData()方法
			// ((BillCardPanel) getComponent()).getBillData().setBillValueObjectByMetaData(vo);

			// TODO: what about body vo
			((BillCardPanel) getComponent()).getBillData().setHeaderValueVO(vo);
			((BillCardPanel) getComponent()).getBillData().updateValue();

			if ("docmdm_unit".equals(vo.getTableCode())) {
				unitDataShow(vo);
			}
		}
	}

	protected void unitDataSave(DocVO vo, BillItem item) {
		// 单位档案，表体数据
		UIRefPane pref = (UIRefPane) item.getComponent();
		String[] pks = pref.getRefPKs();
		String pksValue = "";
		for (int j = 0; j < pks.length; j++) {
			pksValue = pksValue + pks[j] + ",";
		}
		if (pksValue.length() > 0) {
			pksValue = pksValue.substring(0, pksValue.length() - 1);
		}
		// Object value = items[i].converType(items[i].getValueObject());
		vo.setAttributeValue(item.getKey(), pksValue);

		// Body VOs
		DocVO[] bVOs = (DocVO[]) ((BillCardPanel) getComponent()).getBillData().getBodyValueVOs("treetypes", DocVO.class.getName());
		if (bVOs != null && bVOs.length > 0) {
			for (int i = 0; i < bVOs.length; i++) {
				bVOs[i].setParentKeyField("pk_unit");
				bVOs[i].setTableCode("docmdm_unitsub");
				bVOs[i].setPrimaryKeyField("pk_unitsub");
			}

			vo.setAttributeValue(DocVO.KEY_SUB_VOS, bVOs);
		}

	}

	@SuppressWarnings("unchecked")
	protected void unitDataShow(DocVO vo) {
		BillCardPanel bcp = (BillCardPanel) getComponent();
		String strTreeTypes = (String) vo.getAttributeValue("pk_treetype");
		if (strTreeTypes != null) {
			String[] pks = strTreeTypes.split(",");
			if (pks != null && pks.length > 0) {

				BillItem item = bcp.getBillData().getHeadItem("pk_treetype");
				UIRefPane pref = (UIRefPane) item.getComponent();
				// Vector vec = pref.getRefModel().matchPkData(pks);
				pref.getRefModel().matchPkData(pks);
				String[] codes = pref.getRefModel().getRefCodeValues();
				pref.setValueObj(codes);
				// String[] pks = pref.getRefPKs();
			}
		}

		String pkUnit = vo.getPrimaryKey();
		String strWhere = " where pk_unit='" + pkUnit + "'";
		try {
			DocVO[] subVOs = BaseService.queryMainDataByWhere("docmdm_unitsub", "pk_unitsub", strWhere);
			((BillCardPanel) getComponent()).getBillData().setBodyValueVO(subVOs);
			Map<String, DocVO> ttMap = (Map<String, DocVO>) BaseTimeMapFactory.getMap("docmdm_un_treetype");
			if (ttMap.size() < 1) {
				DocVO[] ttVOs = BaseService.queryMainDataByWhere("docmdm_un_treetype", "pk_treetype", null);
				for (int i = 0; i < ttVOs.length; i++) {
					ttMap.put(ttVOs[i].getPrimaryKey(), ttVOs[i]);
				}
			}
			if (subVOs != null && subVOs.length > 0) {
				for (int row = 0; row < subVOs.length; row++) {
					DocVO ttVO = ttMap.get(subVOs[row].getAttributeValue("pk_treetype"));
					DefaultConstEnum value = new DefaultConstEnum(ttVO.getPrimaryKey(), (String) ttVO.getAttributeValue("vname"));
					((BillCardPanel) getComponent()).getBillModel().setValueAt(value, row, "pk_treetype");
					
					String ppk = (String)subVOs[row].getAttributeValue("pk_treeparent");
					if( ppk!=null ){
						Map<String, DocVO> unitMap = (Map<String, DocVO>) BaseTimeMapFactory.getMap("docmdm_unit");
						DocVO pvo = unitMap.get(ppk);
						if( pvo==null ){
							pvo = BaseService.queryMainDataByPK("docmdm_unit", "pk_unit", ppk);
							unitMap.put(ppk, pvo);
						}
						
						DefaultConstEnum pvalue = new DefaultConstEnum(pvo.getPrimaryKey(), (String) pvo.getAttributeValue("vname"));
						((BillCardPanel) getComponent()).getBillModel().setValueAt(pvalue, row, "pk_treeparent");
					}
		
				}
			}

		} catch (BusinessException e) {
			LogTool.error(e);
		}

	}
}