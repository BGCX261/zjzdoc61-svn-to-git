package nc.ui.mdm.base.ext;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Vector;

import nc.funcnode.ui.action.AbstractNCAction;
import nc.funcnode.ui.action.SeparatorAction;

public class BaseActionGroup implements IActionGroup {

	public BaseAction addAction = null;
	private BaseAction delAction = null;
	private BaseAction editAction = null;
	private BaseAction saveAction = null;
	private BaseAction cancelAction = null;
	private BaseAction refreshAction = null;
	private SeparatorAction seperator = new SeparatorAction();
	private AbstractNCAction[] viewActions = null;
	private AbstractNCAction[] editActions = null;
	private IActionGroup extActionGroup = null;

	@SuppressWarnings("unchecked")
	public BaseActionGroup(IBaseUI ui) {
		addAction = new BaseAction(BaseAction.ADDNEW, "新增", ui);
		delAction = new BaseAction(BaseAction.DELETE, "删除", ui);
		editAction = new BaseAction(BaseAction.EDIT, "修改", ui);
		saveAction = new BaseAction(BaseAction.SAVE, "保存", ui);
		refreshAction = new BaseAction(BaseAction.REFRESH, "刷新", ui);
		cancelAction = new BaseAction(BaseAction.CANCEL, "取消", ui);

		String strCode = ui.getLoginCtx().getNodeCode();
		try {
			String strClz = IBaseUI.MDM_EXT_BTN + strCode;
			Class<IActionGroup> clz = (Class<IActionGroup>) Class.forName(strClz);
			if (clz != null) {
				Constructor<IActionGroup> cst = clz.getConstructor(IBaseUI.class);
				if (cst != null) {
					extActionGroup = cst.newInstance(ui);
				}
			}
		} catch (Exception e) {
			// do nothing
		}
	}

	public AbstractNCAction[] getViewAction() {
		if (viewActions == null) {
			// viewActions = new AbstractNCAction[] { addAction, delAction, editAction, refreshAction };
			Vector<AbstractNCAction> vecView = new Vector<AbstractNCAction>();
			vecView.add(addAction);
			vecView.add(delAction);
			vecView.add(editAction);
			vecView.add(refreshAction);
			if (extActionGroup != null) {
				AbstractNCAction[] extViews = extActionGroup.getViewAction();
				if (extViews != null && extViews.length > 0) {
					vecView.addAll(Arrays.asList(extViews));
				}
			}

			viewActions = new AbstractNCAction[vecView.size()];
			vecView.toArray(viewActions);
		}
		return viewActions;
	}

	public AbstractNCAction[] getEditAction() {
		if (editActions == null) {
			// editActions = new AbstractNCAction[] { saveAction, seperator, cancelAction };
			Vector<AbstractNCAction> vecEdit = new Vector<AbstractNCAction>();
			vecEdit.add(saveAction);
			vecEdit.add(seperator);
			vecEdit.add(cancelAction);
			if (extActionGroup != null) {
				AbstractNCAction[] extEdits = extActionGroup.getEditAction();
				if (extEdits != null && extEdits.length > 0) {
					vecEdit.addAll(Arrays.asList(extEdits));
				}
			}

			editActions = new AbstractNCAction[vecEdit.size()];
			vecEdit.toArray(editActions);
		}
		return editActions;
	}

}
