package nc.ui.mdm.base.mvc;

import javax.swing.tree.DefaultMutableTreeNode;

import nc.vo.bd.access.tree.AbastractTreeCreateStrategy;
import nc.vo.mdm.frame.DocVO;

public class BaseTreeStrategy extends AbastractTreeCreateStrategy {

	@Override
	public boolean isCodeTree() {
		return false;
	}

	public DefaultMutableTreeNode createTreeNode(Object obj) {
		return new DefaultMutableTreeNode(obj);
	}

	public Object getNodeId(Object obj) {
		if( obj instanceof DocVO && obj!=null){
			return ((DocVO)obj).getPrimaryKey();
		}
			
		return null;
	}

	public Object getParentNodeId(Object obj) {
		if( obj instanceof DocVO && obj!=null){
			return ((DocVO)obj).getParentPK();
		}
		return null;
	}

}
