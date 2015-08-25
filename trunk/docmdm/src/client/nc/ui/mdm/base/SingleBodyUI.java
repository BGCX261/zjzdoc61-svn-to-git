package nc.ui.mdm.base;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import nc.funcnode.ui.AbstractFunclet;
import nc.pub.mdm.frame.BaseService;
import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.frame.tool.TempletTool;
import nc.ui.mdm.base.ext.BaseActionGroup;
import nc.ui.mdm.base.ext.IBaseUI;
import nc.ui.mdm.base.mvc.BaseBusinessObjectFatory;
import nc.ui.mdm.base.mvc.BaseClientService;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.RowAttribute;
import nc.ui.pubapp.uif2app.model.BillListManageModel;
import nc.ui.pubapp.uif2app.view.BillListEditor;
import nc.ui.pubapp.uif2app.view.handler.DefaultBillListEventHandler;
import nc.ui.pubapp.uif2app.view.value.BillListPanelValueManager;
import nc.ui.uif2.UIState;
import nc.uif.pub.exception.UifException;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.sm.funcreg.FuncRegisterVO;
import nc.vo.uif2.LoginContext;

/**
 * NC61 主数据单表体基础UI类<br>
 * 
 * @author 周海茂
 * @since 2012-09-13
 * @see ToftPanelAdaptor.initContext()
 * @see FuncletWidgetFactory.createFuncletWidget()
 * 主数据思路：<br>
 * 1、元数据<br>
 * 2、免VO生成<br>
 * 3、免节点注册<br>
 * 4、免节点XML描述配置文件<br>
 * 5、轻量级<br>
 * 6、数据校验、映射<br>
 * 7、数据导入、导出<br>
 * 
 */
public class SingleBodyUI extends AbstractFunclet implements IBaseUI{

	private static final long serialVersionUID = -7768577048542699277L;

	private BaseActionGroup actionGroup = null;

	private int currentRow = -1;
	
	private LoginContext loginCtx = null;
	
	private UIState status = UIState.NOT_EDIT;
	
	private String tableCode = null;

	private String tablePK = null;

	private BillListEditor templetEditor = null;

	private BillTempletVO templetVO = null;

	public SingleBodyUI() {

	}

	@Override
	public String afterIntiedOpenCheck(FuncRegisterVO frVO) {
		// initialize();
		return null;
	}

	public BaseActionGroup getActionGroup() {
		if (actionGroup == null) {
			actionGroup = new BaseActionGroup(this);
		}
		return actionGroup;
	}

	public int getCurrentRow() {
		return currentRow;
	}
	
	public LoginContext getLoginCtx() {
		if (loginCtx == null) {
			loginCtx = UITool.makeLoginContext(this);
		}
		return loginCtx;
	}
	
	public UIState getStatus() {
		return status;
	}

	public String getTableCode() {
		if( tableCode == null ){
			tableCode = TempletTool.getTableCode( getTempletVO() );
		}
		return tableCode;
	}

	public BillModel getTableModel() {
		return getTempletEditor().getBillListPanel().getBodyBillModel();
	}

	public String getTablePK() {
		if( tablePK==null ){
			tablePK = TempletTool.getTablePkField( getTempletVO() );
		}
		return tablePK;
	}

	public BillListEditor getTempletEditor() {
		if (templetEditor == null) {

			templetEditor = new BillListEditor();

			BillListManageModel model = new BillListManageModel();
			// BatchBillTableModel model = new BatchBillTableModel();
			BaseClientService srv = new BaseClientService();
			srv.setLoginContext(getLoginCtx());
			model.setContext(getLoginCtx());
			model.setService( srv );
			BaseBusinessObjectFatory boFactory = new BaseBusinessObjectFatory();
			model.setBusinessObjectAdapterFactory(boFactory);
			
			templetEditor.setModel(model);

			DefaultBillListEventHandler eventHandler = new DefaultBillListEventHandler();
			eventHandler.setEditor(templetEditor);
			templetEditor.setEventHandler(eventHandler);
			
			BillListPanelValueManager vm = new BillListPanelValueManager();
			vm.setModel(model);
			templetEditor.setComponentValueManager(vm);

			templetEditor.initUI();
		}
		return templetEditor;
	}

	public BillTempletVO getTempletVO() {
		if( templetVO==null ){
			String strCode = getLoginCtx().getNodeCode();
			templetVO = TempletTool.queryTempletByCode(strCode);//UITool.getTemplet(strCode);
		}
		return templetVO;
	}
	
	@SuppressWarnings("rawtypes")
	public Class getVOClass() {
		return null;
	}

	@Override
	public void init() {
		initView();
		initData();
	}

	private void initData(){
		getTempletEditor().getBillListPanel().getBodyBillModel().clearBodyData();
		try {
			BillItem bt = getTempletEditor().getBillListPanel().getBodyItem("vcode");
			String strWhere = "isnull(dr,0)=0";
			if( bt!=null ){
				strWhere += " order by vcode";
			}else{
				UITable table = getTempletEditor().getBillListPanel().getBodyTable();
				strWhere+=" order by "+getFirstColumn(table);
			}
			DocVO[] vos = BaseService.queryMainDataByWhere( getTableCode(), getTablePK() , strWhere);
			//getTempletEditor().getModel().initModel(vos);
			getTempletEditor().getBillListPanel().setBodyValueVO(vos);
			getTempletEditor().getBillListPanel().getBodyBillModel().execLoadFormula();
		} catch (BusinessException e) {
			LogTool.error(e);
		}
		
		BillModel bm = getTempletEditor().getBillListPanel().getBodyBillModel();
		for (int i = 0; i < bm.getRowCount(); i++) {
			getTempletEditor().getBillListPanel().getBodyBillModel().getRowAttribute(i).setEdit(false);
		}
		getTempletEditor().getBillListPanel().getBodyBillModel().updateValue();
	}
	
	public String getFirstColumn(UITable table){
//		table.getTableHeader().getColumnModel().getColumnCount();
//		table.getTableHeader().getColumnModel().getColumn(1);
		BillTempletBodyVO[] bodyvos=getTempletVO().getBodyVO();
		Map<String,BillTempletBodyVO> map=new HashMap<String,BillTempletBodyVO>();
		List<BillTempletBodyVO> list=new ArrayList<BillTempletBodyVO>();
		for(BillTempletBodyVO item:bodyvos){
			list.add(item);
			if(!item.getShowflag()&&!item.getListshowflag()){//隐藏列
				map.put(item.getItemkey(), item);
			}
		}
		class BillTempletbComp implements Comparator<BillTempletBodyVO>{
			@Override
			public int compare(BillTempletBodyVO arg0,
					BillTempletBodyVO arg1) {//按显示顺序排序
				return arg0.getShoworder().compareTo(arg1.getShoworder());
			}
		}
		Collections.sort(list, new BillTempletbComp());
		for(BillTempletBodyVO bvo:list){
			if(map.get(bvo.getItemkey())==null){//第一个显示的列，为排序字段
				return bvo.getItemkey();
			}
		}
		return null;
//		IBusinessEntity ibe =  getTempletVO().getHeadVO().getBillMetaDataBusinessEntity();
//		List<IAttribute> atts=ibe.getAttributes();
//		for(IAttribute att:atts){
//			if(map.get(att.getName())==null){
//				strWhere += " order by "+att.getName();
//				break;
//			}
//		}
	}
	
	private void initView() {
		setLayout(new BorderLayout());
		add(getTempletEditor(), BorderLayout.CENTER);
		setStatus( UIState.NOT_EDIT );
	}
	
	public void setActionGroup(BaseActionGroup actionGroup) {
		this.actionGroup = actionGroup;
	}

	public void setCurrentRow(int currentRow) {
		this.currentRow = currentRow;
	}

	public void setLoginCtx(LoginContext loginCtx) {
		this.loginCtx = loginCtx;
	}

	public void setStatus(UIState status) {
		this.status = status;
		
		Action[] currAction = null;
		if( status == UIState.NOT_EDIT ){
			currAction = getActionGroup().getViewAction();
			
		}else if( status == UIState.ADD ){
			currAction = getActionGroup().getEditAction();
			
		}else if( status == UIState.EDIT ){
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

	public void setTempletEditor(BillListEditor templetEditor) {
		this.templetEditor = templetEditor;
	}

	public void setTempletVO(BillTempletVO templetVO) {
		this.templetVO = templetVO;
	}

	/**
	 * @see nc.ui.pubapp.uif2app.view.BillListEditor.onAdd()
	 */
	public void doAdd() {
		getTempletEditor().updateListPanelValue();
		getTempletEditor().getBillListPanel().getBodyBillModel().addLine();
		getTempletEditor().getBillListPanel().setEnabled(true);
		setStatus(UIState.ADD);
		int iRow = getTempletEditor().getBillListPanel().getBodyBillModel().getRowCount();
		iRow--;
		setCurrentRow(iRow);
		setRowEditable(iRow);
		
//		DocVO nvo = new DocVO();
//		nvo.setTableCode(getTableCode());
//		nvo.setPrimaryKeyField( getTablePK() );
		
	}
	
	public void setRowEditable(int iRow){
		BillListPanel blp = getTempletEditor().getBillListPanel();
		BillModel bm = blp.getBodyBillModel();
		int rowCount = bm.getRowCount();
		//bm.setRowEditState(true);
		RowAttribute ra = bm.getRowAttribute(iRow);
		if( ra!=null ) {
			ra.setEdit(true);
		}
		
		int index = 0;
		int[] rows = new int[rowCount - 1];
		for (int i = 0; i < rowCount; i++) {
			if (i == iRow) {
				continue;
			}
			rows[index++] = i;
		}
		bm.setNotEditAllowedRows(rows);
	}

	public void doCancel() {
		getTempletEditor().onCancel();
		setStatus(UIState.NOT_EDIT);
		initData();
	}

	public void doDelete() {
		BillListPanel blp = getTempletEditor().getBillListPanel();
		int iRow = blp.getBodyTable().getSelectedRow();
		
		String strPK = (String)blp.getBodyBillModel().getValueAt(iRow,  getTablePK());
		DocVO vo = new DocVO();
		vo.setTableCode( getTableCode() );
		vo.setPrimaryKeyField( getTablePK() );
		vo.setPrimaryKey(strPK);
		try {
			BaseService.getService().delete(vo);
			blp.getBodyBillModel().removeRow(iRow);
		} catch (UifException e) {
			LogTool.error(e);
		}
		
	}

	public void doEdit() {
		BillListPanel blp = getTempletEditor().getBillListPanel();
		int iRow = blp.getBodyTable().getSelectedRow();
		setRowEditable(iRow);
		getTempletEditor().getBillListPanel().setEnabled(true);
		
		setStatus(UIState.EDIT);
	}

	public void doRefresh() {
		initData();
	}

	public void doSave() throws Exception {
		// getTempletEditor().getComponentValueManager().getValue();
		UITable table = getTempletEditor().getBillListPanel().getBodyTable();
		if( table.getCellEditor()!=null ){
			table.getCellEditor().stopCellEditing();
		}

		BillModel model = getTempletEditor().getBillListPanel().getBodyBillModel();
		Map<String, Object>[] maps = model.getBodyChangeValueByMetaData();// getBodyValueByMetaData();
		DocVO[] vos = null;
		if (maps != null && maps.length > 0) {
			vos = new DocVO[maps.length];
			for (int i = 0; i < maps.length; i++) {
				vos[i] = new DocVO();
				vos[i].setTableCode(getTableCode());
				vos[i].setPrimaryKeyField(getTablePK());

				Iterator<String> it = maps[i].keySet().iterator();
				while (it.hasNext()) {
					String strFieldCode = it.next();
					Object objFieldValue = maps[i].get(strFieldCode);
					vos[i].setAttributeValue(strFieldCode, objFieldValue);
				}
			}
		}

		// 操作逻辑上，vos数组只有一条数据，所以暂时不考虑统一事务问题
		if (getStatus() == UIState.ADD) {
			for (int i = 0; i < vos.length; i++) {
				String pk = BaseService.getService().insert(vos[i]);
				vos[i].setPrimaryKey(pk);
			}
		}else if (getStatus() == UIState.EDIT) {
			// 不能直接调用getTempletEditor().getModel().update()方法，
			// 需要绕过nc.ui.uif2.model.BillManageModel.update(Object object) 里面的BillManageModel.directlyUpdate(Object object)方法
			Object upVOs = null;
			if(vos!=null){
				upVOs=getTempletEditor().getModel().getService().update(vos);
			}if( upVOs!=null ){
				
			}
			// BaseService.getService().update(voCurr);
		}
		if(vos==null){
			MessageDialog.showWarningDlg(this, "警告", "数据没有变化，不需要保存！");
		}
		setStatus(UIState.NOT_EDIT);
		getTempletEditor().getBillListPanel().setEnabled(false);

	}

}
