package nc.ui.mdm.base.mvc;

import javax.swing.tree.DefaultMutableTreeNode;

import nc.vo.jcom.tree.ITreeNodeToHandle;
import nc.vo.mdm.frame.DocVO;

public class BaseTreeHandle implements ITreeNodeToHandle {

	@Override
	public Object getHandleFromTreeNode(DefaultMutableTreeNode treeNode) {
		Object obj = treeNode.getUserObject();
		if(obj!=null && obj instanceof DocVO){
			return ((DocVO)obj).getPrimaryKey();
		}
		return null;
	}

}
