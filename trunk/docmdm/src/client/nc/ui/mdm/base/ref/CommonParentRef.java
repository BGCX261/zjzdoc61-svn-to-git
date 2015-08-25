package nc.ui.mdm.base.ref;

import java.util.Vector;

import nc.ui.bd.ref.AbstractRefTreeModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.editor.BillForm;

public class CommonParentRef extends AbstractRefTreeModel {
	
	private BillForm billForm = null;
	
	public CommonParentRef(){
		
	}
	
	@Override
	public void setTableName(String newTableName) {
		super.setTableName(newTableName);
		if( "docmdm_un_treetype".equals(newTableName) ){ // 单位口径参照
			//setData(vData)
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Vector getData() {
		Vector ret = super.getData();
		return ret;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Vector getSelectedData() {
		Vector ret = super.getSelectedData();
//		if( getBillForm()!=null ){
//			// 处理【单位口径】
//			BillItem it = getBillForm().getBillCardPanel().getHeadItem("pk_unit");
//			if( it!=null && it.getValueObject()!=null ){
//				String strPK = it.getValueObject().toString().trim();
//				try {
//					DocVO[] subVOs = BaseService.queryMainDataByWhere("docmdm_unitsub", " where pk_unit='"+strPK+"'");
//					if( subVOs!=null && subVOs.length>0 ){
//						
//					}else{
//						return ret;
//					}
//				} catch (BusinessException e) {
//					LogTool.error(e);
//				}
//				
//			}
//		}
		return ret;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void setSelectedData(Vector vecSelectedData) {
		super.setSelectedData(vecSelectedData);
	}

	public BillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(BillForm billForm) {
		this.billForm = billForm;
		BillItem it = getBillForm().getBillCardPanel().getHeadItem("pk_treetype");
		if( it!=null){
			UIRefPane rp = (UIRefPane)it.getComponent();
			if( rp!=null ){
				rp.getRefUIConfig().setMutilSelected(true);
			}
		}
	}
}
