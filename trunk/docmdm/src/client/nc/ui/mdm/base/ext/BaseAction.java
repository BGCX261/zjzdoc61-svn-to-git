package nc.ui.mdm.base.ext;

import java.awt.event.ActionEvent;

import nc.funcnode.ui.action.AbstractNCAction;
import nc.pub.mdm.frame.tool.LogTool;

public class BaseAction extends AbstractNCAction {

	public static final String ADDNEW = "addnew";
	public static final String CANCEL = "cancel";
	public static final String DELETE = "delete";
	public static final String EDIT = "edit";
	public static final String REFRESH = "refresh";
	public static final String SAVE = "save";
	private static final long serialVersionUID = -2872296134707429695L;
	private IBaseUI ui = null;

	public BaseAction(String code, String name, IBaseUI ui) {
		super(code, name);
		this.ui = ui;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (getCode().equals(ADDNEW)) {
				ui.doAdd();
			} else if (getCode().equals(DELETE)) {
				ui.doDelete();
			} else if (getCode().equals(EDIT)) {
				ui.doEdit();
			} else if (getCode().equals(SAVE)) {
				ui.doSave();
			} else if (getCode().equals(CANCEL)) {
				ui.doCancel();
			} else if (getCode().equals(REFRESH)) {
				ui.doRefresh();
			}
		} catch (Exception exp) {
			LogTool.error(exp);
		}
	}
}
