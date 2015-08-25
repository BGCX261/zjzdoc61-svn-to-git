package nc.ui.mdm.base.mvc;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import nc.ui.pub.beans.tree.IFilterTreeCellRender;
import nc.vo.mdm.frame.DocVO;

public class BaseTreeRender extends DefaultTreeCellRenderer implements IFilterTreeCellRender {

	private static final long serialVersionUID = -4538450538143752890L;

	@Override
	public void setFiltertext(String filtertext) {

	}

	@Override
	public Component getTreeCellRendererComponent(JTree jtree, Object obj, boolean flag, boolean flag1, boolean flag2, int i, boolean flag3) {

		super.getTreeCellRendererComponent(jtree, obj, flag, flag1, flag2, i, flag3);

		if (obj != null && (obj instanceof DefaultMutableTreeNode)) {
			Object userObj = ((DefaultMutableTreeNode) obj).getUserObject();
			if (userObj != null && userObj instanceof DocVO) {
				DocVO vo = (DocVO) userObj;
				String strCode = (String)vo.getAttributeValue("vcode");
				String strName = (String)vo.getAttributeValue("vname");
				String strText = "" + (strCode!=null?strCode:"") + " " + (strName!=null?strName:"");
				setText( strText );
			}
		}
		return this;
	}
}
