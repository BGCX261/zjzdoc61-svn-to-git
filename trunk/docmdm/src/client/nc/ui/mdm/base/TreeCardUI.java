package nc.ui.mdm.base;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import nc.bs.uap.appasset.UAPAppAssetInitDataObject;
import nc.funcnode.ui.AbstractFunclet;
import nc.funcnode.ui.FuncletInitData;
import nc.md.model.impl.MDBean;
import nc.pub.mdm.frame.BaseService;
import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.frame.tool.TempletTool;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.mdm.base.ext.BaseActionGroup;
import nc.ui.mdm.base.ext.IBaseUI;
import nc.ui.mdm.base.ext.IEditListener;
import nc.ui.mdm.base.mvc.BaseClientService;
import nc.ui.mdm.base.mvc.BaseTreeAdaptor;
import nc.ui.mdm.base.mvc.BaseTreeCardModel;
import nc.ui.mdm.base.mvc.BaseTreeHandle;
import nc.ui.mdm.base.mvc.BaseTreeRender;
import nc.ui.mdm.base.mvc.BaseTreeStrategy;
import nc.ui.mdm.base.mvc.BaseValueManager;
import nc.ui.mdm.base.ref.CommonParentRef;
import nc.ui.mdm.base.ref.CommonRef;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.CommonConfirmDialogUtils;
import nc.ui.uif2.components.TreePanel;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.editor.TemplateContainer;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.vo.bd.access.tree.BDTreeCreator;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.uif2.LoginContext;

/**
 * NC61 主数据"自身树"基础UI类<br>
 * 
 * @author 周海茂
 * @since 2012-09-13
 * @see SingleBodyUI
 * 
 */
public class TreeCardUI extends AbstractFunclet implements IBaseUI, TreeSelectionListener {

	private static final long serialVersionUID = -7022236168205997648L;

	private BaseActionGroup actionGroup = null;

	private BillForm billForm = null;

	private LoginContext loginCtx = null;

	private BaseTreeCardModel model = null;

	private UIState status = UIState.NOT_EDIT;

	private String tableCode = null;

	private String tablePK = null;

	private BillTempletVO templetVO = null;

	private TreePanel treePanel = null;

	@Override
	public void doAdd() {
		setStatus(UIState.ADD);
		getTreePanel().getModel().setUiState(UIState.ADD);
		Object obj = getModel().getSelectedData();
		if (obj != null && obj instanceof DocVO) {
			DocVO vo = (DocVO) obj;
			getBillForm().getBillCardPanel().setHeadItem("pk_parent", vo.getPrimaryKey());
		}
	}

	@Override
	public void doCancel() {
		getModel().setUiState(UIState.NOT_EDIT);
		setStatus(UIState.NOT_EDIT);
	}

	@Override
	public void doDelete() {
		Object obj = getTreePanel().getModel().getSelectedData();
		if (obj == null) {
			return;
		}

		if (UIDialog.ID_YES == CommonConfirmDialogUtils.showConfirmDeleteDialog(getModel().getContext().getEntranceUI())) {
			try {
				getModel().delete();
				ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getDelSuccessInfo(), getModel().getContext());
			} catch (Exception e) {
				LogTool.error(e);
			}
			// showSuccessInfo();
		}
	}

	@Override
	public void doEdit() {
		Object obj = getTreePanel().getModel().getSelectedData();
		if (obj == null) {
			return;
		}
		setStatus(UIState.EDIT);
		getTreePanel().getModel().setUiState(UIState.EDIT);
	}

	@Override
	public void doRefresh() {
		initData(null);
	}

	@Override
	public void doSave() throws Exception {
		DocVO vo = (DocVO) getBillForm().getValue();
		if (vo != null) {
			vo.setTableCode(getTableCode());
			vo.setPrimaryKeyField(getTablePK());

			if (getStatus() == UIState.ADD) {
				Object returnObj = getModel().add(vo);
				getModel().setUiState(UIState.NOT_EDIT);
				if (getModel() instanceof HierachicalDataAppModel) {
					((HierachicalDataAppModel) getModel()).setSelectedData(returnObj);
				}

			} else if (getStatus() == UIState.EDIT) {
				getModel().update(vo);
			}
		}
		setStatus(UIState.NOT_EDIT);
		getModel().setUiState(UIState.NOT_EDIT);
	}

	public BaseActionGroup getActionGroup() {
		if (actionGroup == null) {
			actionGroup = new BaseActionGroup(this);
		}
		return actionGroup;
	}

	@SuppressWarnings("unchecked")
	public BillForm getBillForm() {
		if (billForm == null) {
			billForm = new BillForm();

			TemplateContainer tc = new TemplateContainer();
			tc.setContext(getLoginCtx());

			billForm.setTemplateContainer(tc);

			billForm.setModel(getModel());

			BaseValueManager bvm = new BaseValueManager();
			bvm.setComponent(getBillForm().getBillCardPanel());
			billForm.setComponentValueManager(bvm);
			bvm.setUi( this );

			billForm.initUI();

			IEditListener lst = null;
			String strCode = getLoginCtx().getNodeCode();
			try {
				String strClz = MDM_EXT_EVT + strCode;
				Class<IEditListener> clz = (Class<IEditListener>) Class.forName(strClz);
				if (clz != null) {
					lst = clz.newInstance();
					lst.setBaseUI(this);
					BillItem[] items = getBillForm().getBillCardPanel().getBillData().getHeadTailItems();
					if (items != null) {
						for (int i = 0; i < items.length; i++) {
							items[i].getItemEditor().addBillEditListener(lst);
						}
					}
				}
			} catch (Exception e) {
				// do nothing
			}
		}
		return billForm;
	}

	public LoginContext getLoginCtx() {
		if (loginCtx == null) {
			loginCtx = UITool.makeLoginContext(this);
		}
		return loginCtx;
	}

	public BaseTreeCardModel getModel() {
		if (model == null) {
			model = new BaseTreeCardModel();
			model.setContext(getLoginCtx());

			BaseClientService srv = new BaseClientService();
			srv.setLoginContext(getLoginCtx());
			model.setService(srv);

			BaseTreeAdaptor adp = new BaseTreeAdaptor();
			model.setBusinessObjectAdapterFactory(adp);
		}
		return model;
	}

	public UIState getStatus() {
		return status;
	}

	public String getTableCode() {
		if (tableCode == null) {
			tableCode = TempletTool.getTableCode(getTempletVO());
		}
		return tableCode;
	}

	public String getTablePK() {
		if (tablePK == null) {
			tablePK = TempletTool.getTablePkField(getTempletVO());
		}
		return tablePK;
	}

	public BillTempletVO getTempletVO() {
		if (templetVO == null) {
			String strCode = getLoginCtx().getNodeCode();
			templetVO = TempletTool.queryTempletByCode(strCode);//UITool.getTemplet(strCode);
		}
		return templetVO;
	}

	public TreePanel getTreePanel() {
		if (treePanel == null) {
			treePanel = new TreePanel();
			treePanel.setModel(getModel());
			treePanel.getTree().setSelectionRow(0);

			BaseTreeRender render = new BaseTreeRender();
			treePanel.setTreeCellRenderer(render);

			treePanel.getTree().removeTreeSelectionListener(this);
			treePanel.getTree().addTreeSelectionListener(this);
		}
		return treePanel;
	}

	@Override
	public void init() {
		initView();
		setStatus(UIState.NOT_EDIT);
		// ininData invoked by parent
	}

	@Override
	public void initData(FuncletInitData data) {

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(getFuncletContext().getFuncName());
		DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
		BaseTreeStrategy strategy = new BaseTreeStrategy();
		BaseTreeHandle handle = new BaseTreeHandle();
		DocVO[] vos = null;
		try {
			String strCodeField = BaseService.getTableCodeField(getTableCode(), null);
			String strWhere = "isnull(dr,0)=0";
			if (strCodeField != null) {
				strWhere += " order by " + strCodeField;
			}
			vos = BaseService.queryMainDataByWhere(getTableCode(), getTablePK(), strWhere);
			if (vos != null && vos.length > 0) {
				for (int i = 0; i < vos.length; i++) {
					vos[i].setParentKeyField("pk_parent");
				}
			}
			// vos = (DocVO[]) getModel().getService().queryByDataVisibilitySetting(getLoginCtx());
		} catch (Exception e) {
			LogTool.error(e);
		}
		DefaultTreeModel newTreeModel = BDTreeCreator.expandTree(treeModel, vos, strategy, handle);
		getModel().setTree(newTreeModel);
		// getTreePanel().setTree( )

		if (data != null) {
			Object initData = data.getInitData();
			if (!(initData instanceof UAPAppAssetInitDataObject)) {
				return;
			}
			UAPAppAssetInitDataObject initDataObj = (UAPAppAssetInitDataObject) initData;
			DefaultMutableTreeNode node = getModel().findNodeByBusinessObjectId(initDataObj.getDataId());
			getModel().setSelectedNode(node);
		}

	}

	protected void initRef() {

		BillItem bi = getBillForm().getBillCardPanel().getHeadItem("pk_parent");
		if (bi != null) {
			Object obj = bi.getComponent();
			if (obj instanceof UIRefPane) {
				UIRefPane ref = (UIRefPane) obj;
				CommonParentRef pref = new CommonParentRef();
				pref.setRefNodeName(getFuncletContext().getFuncName());
				pref.setTableName(getTableCode());
				pref.setPkFieldCode(getTablePK());
				pref.setFieldCode(new String[] { "vcode", "vname" });
				pref.setFieldName(new String[] { "编码", "名称" });
				pref.setHiddenFieldCode(new String[] { getTablePK(), "pk_parent" });
				pref.setRefCodeField("vcode");
				pref.setRefNameField("vname");
				pref.setWherePart(" isnull(dr,0)=0");
				pref.setOrderPart("vcode");
				pref.setFatherField("pk_parent");
				pref.setChildField(getTablePK());
				ref.setRefModel(pref);
			}
		}

		BillTabVO[] tabVOs = getTempletVO().getHeadVO().getStructvo().getBillTabVOs();
		for (int i = 0; i < tabVOs.length; i++) {
			// BaseService.getTableMD( tabVOs[i].getMetadataclass() );
			MDBean bean = BaseService.getTableBean(tabVOs[i].getMetadataclass());
			if (bean != null) {
				initConfigRef(tabVOs[i].getPos(), tabVOs[i].getTabcode(), bean.getDefaultTableName());
			}
		}
	}

	protected void initConfigRef(int pos, String strTabCode, String strTable) {
		try {
			DocVO[] cfgVOs = (DocVO[]) BaseService.queryMainDataByWhere("docmdm_sys_ref", "pk_sysref", " where vtable='" + strTable + "'");
			if (cfgVOs != null && cfgVOs.length > 0) {
				for (int i = 0; i < cfgVOs.length; i++) {
					DocVO cfgVO = cfgVOs[i];
					// String strItemTable = (String)rvo.getAttributeValue("vtable");
					String strItemKey = (String) cfgVO.getAttributeValue("vfield");
					BillCardPanel bp = getBillForm().getBillCardPanel();
					BillItem item = null;
					if (BillItem.BODY == pos) {
						item = bp.getBodyItem(strTabCode, strItemKey);
					} else if (BillItem.HEAD == pos) {
						item = bp.getHeadItem(strItemKey);
					} else if (BillItem.TAIL == pos) {
						item = bp.getTailItem(strItemKey);
					}

					if (item == null) {
						continue;
					}

					Object obj = item.getComponent();
					if (obj instanceof UIRefPane) {
						UIRefPane ref = (UIRefPane) obj;
						initRefPane(ref, cfgVO, item.getName());
					}
				}
			}

		} catch (BusinessException e) {
			LogTool.error(e);
		}
	}

	protected void initRefPane(UIRefPane ref, DocVO cfgVO, String strRootName) {
		AbstractRefModel refModel = null;
		String code = (String) cfgVO.getAttributeValue("vref_code");
		String name = (String) cfgVO.getAttributeValue("vref_name");
		String table = (String) cfgVO.getAttributeValue("vref_table");
		String pk = (String) cfgVO.getAttributeValue("vref_pk");

		if (cfgVO.getAttributeValue("vref_parent") != null) {
			String parent = (String) cfgVO.getAttributeValue("vref_parent");
			CommonParentRef pref = new CommonParentRef();
			pref.setFatherField(parent);
			pref.setChildField(pk);
			refModel = pref;
			refModel.setHiddenFieldCode(new String[] { pk, parent });

			if ("docmdm_un_treetype".equals(table)) { // 单位口径参照
				pref.setBillForm(getBillForm());
			}

		} else {
			refModel = new CommonRef();
			refModel.setHiddenFieldCode(new String[] { pk });
		}

		refModel.setRefNodeName(strRootName);
		refModel.setTableName(table);
		refModel.setPkFieldCode(pk);
		refModel.setFieldCode(new String[] { code, name });
		refModel.setFieldName(new String[] { "编码", "名称" });
		refModel.setRefCodeField(code);
		refModel.setRefNameField(name);
		refModel.setWherePart("isnull(dr,0)=0");
		refModel.setOrderPart(code);

		ref.setRefModel(refModel);
	}

	private void initView() {
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		// @see DefaultContainerComponentStrategy.processSplitPane()
		sp.setDividerSize(2);
		sp.setResizeWeight(0.5f); // @see SPNode.dividerLocation
		sp.setDividerLocation(300);
		sp.setLeftComponent(getTreePanel());
		sp.setRightComponent(getBillForm());
		setLayout(new BorderLayout());
		add(sp, BorderLayout.CENTER);

		initRef();
	}

	public void setActionGroup(BaseActionGroup actionGroup) {
		this.actionGroup = actionGroup;
	}

	public void setBillForm(BillForm billForm) {
		this.billForm = billForm;
	}

	public void setLoginCtx(LoginContext loginCtx) {
		this.loginCtx = loginCtx;
	}

	public void setModel(BaseTreeCardModel model) {
		this.model = model;
	}

	@Override
	public void setRowEditable(int iRow) {
		// TODO Auto-generated method stub

	}

	public void setStatus(UIState status) {
		this.status = status;

		Action[] currAction = null;
		if (status == UIState.NOT_EDIT) {
			currAction = getActionGroup().getViewAction();

		} else if (status == UIState.ADD) {
			currAction = getActionGroup().getEditAction();

		} else if (status == UIState.EDIT) {
			currAction = getActionGroup().getEditAction();
		}
		setMenuActions(currAction);
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}

	public void setTablePK(String tablePK) {
		this.tablePK = tablePK;
	}

	public void setTempletVO(BillTempletVO templetVO) {
		this.templetVO = templetVO;
	}

	public void setTreePanel(TreePanel treePanel) {
		this.treePanel = treePanel;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath path = e.getNewLeadSelectionPath();
		if (path == null) {
			getModel().setSelectedData(null);
			// filterHandler.getFilterTreeModel().setSelectedObject(null);
		} else {
			Object selectedObj = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			getModel().setSelectedData(selectedObj);
			// filterHandler.getFilterTreeModel().setSelectedObject(selectedObj);
		}

	}
}
