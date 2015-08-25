package nc.ui.mdm.base.ext;

import nc.funcnode.ui.action.AbstractNCAction;

public interface IActionGroup {
	public AbstractNCAction[] getEditAction();
	public AbstractNCAction[] getViewAction();
}
