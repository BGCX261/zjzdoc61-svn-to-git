package nc.ui.mdm.ext;

import java.awt.event.ActionEvent;
import java.io.File;

import nc.funcnode.ui.action.AbstractNCAction;
import nc.pub.mdm.excel.DocParser;
import nc.pub.mdm.frame.BaseService;
import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.proxy.BaseUserObject;
import nc.ui.mdm.base.SingleBodyUI;
import nc.ui.mdm.base.UITool;
import nc.ui.mdm.base.ext.BaseAction;
import nc.ui.mdm.base.ext.IActionGroup;
import nc.ui.mdm.base.ext.IBaseUI;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillModel;
import nc.vo.mdm.frame.DocAggVO;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.VOStatus;

public class Button79020301 implements IActionGroup {
	private IBaseUI ui = null;

	public Button79020301(IBaseUI ui) {
		this.ui = ui;
	}

	@Override
	public AbstractNCAction[] getEditAction() {
		return null;
	}

	@Override
	public AbstractNCAction[] getViewAction() {
		BaseAction importAction = new BaseAction("importdoc", "数据导入", ui) {
			private static final long serialVersionUID = -81297934731096090L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// super.actionPerformed(e);
				SingleBodyUI sui = (SingleBodyUI) ui;
				doImport(sui);
			}
		};
		return new AbstractNCAction[] { importAction };
	}

	public void doImport(SingleBodyUI sui) {
		int row = sui.getTempletEditor().getBillListPanel().getBodyTable().getSelectedRow();
		BillModel bm = sui.getTempletEditor().getBillListPanel().getBodyBillModel();
		DocVO selectedVO = (DocVO)bm.getBodyValueRowVO(row, DocVO.class.getName());
		if (selectedVO != null ) {
			selectedVO.setTableCode( sui.getTableCode() );
			selectedVO.setPrimaryKeyField( sui.getTablePK() );
			
			String strField = (String) selectedVO.getAttributeValue("vfields");
			String strTable = (String) selectedVO.getAttributeValue("vtable");
			String strPk = (String) selectedVO.getAttributeValue("vpk");
			String strParentPK = (String) selectedVO.getAttributeValue("vpkparent");

			if (strTable != null && strPk != null && strField != null) {
				// 文件选择对话框
				File selectedFile = UITool.getSeletedFile(this.getClass());
				if( selectedFile==null ){
					return; // 没选择任何文件
				}
				
				String[] fields = strField.split(",");
				// Excel解析器
				DocParser dp = new DocParser(selectedFile, fields, strTable, strPk, strParentPK);
				try {
					DocVO[] vos = dp.getVOs();
					selectedVO.setAttributeValue( DocVO.KEY_IMPORT_VOS, vos);
					selectedVO.setStatus(VOStatus.UPDATED);
					
					DocAggVO aggVO = new DocAggVO();
					aggVO.setParentVO(selectedVO);
					BaseUserObject userObj = new BaseUserObject();
					// 执行导入
					AggregatedValueObject aggRet = BaseService.getService().saveBD(aggVO, userObj);
					selectedVO = (DocVO)aggRet.getParentVO();
					bm.setBodyRowVO(selectedVO, row);
					
					// 导入成功
					MessageDialog.showHintDlg(sui, "操作结果", "数据导入成功！");
					
				} catch (Exception e) {
					LogTool.error(e);
				}
			}
		}
	}
}
