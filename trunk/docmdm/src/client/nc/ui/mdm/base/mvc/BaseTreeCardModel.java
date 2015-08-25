package nc.ui.mdm.base.mvc;

import javax.swing.tree.DefaultMutableTreeNode;

import nc.ui.uif2.model.HierachicalDataAppModel;

public class BaseTreeCardModel extends HierachicalDataAppModel {
	
	@Override
	public void initModel(Object data) {
		super.initModel(data);
		//setTree( );
	}
	
	@Override
	public DefaultMutableTreeNode findParentNodeByBusinessObject(Object object) {
		return super.findParentNodeByBusinessObject(object);
	}
	
	@Override
	public void setSelectedData(Object selection) {
		super.setSelectedData(selection);
		
	}
}
