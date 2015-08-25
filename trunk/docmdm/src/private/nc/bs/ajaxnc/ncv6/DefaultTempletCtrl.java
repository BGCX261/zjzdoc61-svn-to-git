package nc.bs.ajaxnc.ncv6;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.ajaxnc.base.BaseCtrl;
import nc.bs.ajaxnc.dao.BasePageDao;
import nc.bs.ajaxnc.tools.RequestTool;
import nc.bs.ajaxnc.tools.TempletViewTool;
import nc.pub.mdm.frame.IContent;
import nc.pub.mdm.frame.tool.TempletTool;
import nc.vo.mdm.frame.MenuVO;
import nc.vo.pub.bill.BillTempletVO;

public class DefaultTempletCtrl extends BaseCtrl {
	@Override
	public String getViewDir() {
		return "/bill/templet/";
	}
	
	@Override
	protected String getDefaultMethod() {
		return "onList";
	}

	public String onList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String menuCode = TempletViewTool.findMenuCode(request);
		MenuVO mvo = RequestTool.getMenu(request, menuCode);
		String strClassName = mvo.getMenuClass();
		if( IContent.UICLASS_TREE.equals(strClassName) ){
			return "tree.jsp";
		}else if( IContent.UICLASS_TREE_MANAGE.equals(strClassName)){
			return "manage.jsp";
		}else{
			return "single.jsp";
		}
	}
	
	protected void query(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String menuCode = TempletViewTool.findMenuCode(request);
		BillTempletVO tvo = TempletTool.queryTempletByCode(menuCode);
		String strTable = TempletTool.getTableCode( tvo );
		String strPKField = TempletTool.getTablePkField(tvo);
		
		String strSQL = "select * from "+ strTable + " where isnull(ts,0)=0";
		BasePageDao.query(strSQL, null, 0, 0, strTable, strPKField);
	}

	public String onEdit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String menuCode = TempletViewTool.findMenuCode(request);
		MenuVO mvo = RequestTool.getMenu(request, menuCode);
		String strClassName = mvo.getMenuClass();
		if( IContent.UICLASS_TREE.equals(strClassName) ){
			return "treeEdit.jsp";
		}else if( IContent.UICLASS_TREE_MANAGE.equals(strClassName) ){
			return "manageTreeEdit.jsp";
		}else{
			return "singleEdit.jsp";
		}
	}

	public String onView(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String menuCode = TempletViewTool.findMenuCode(request);
		MenuVO mvo = RequestTool.getMenu(request, menuCode);
		String strClassName = mvo.getMenuClass();
		if( IContent.UICLASS_TREE.equals(strClassName) ){
			return "treeView.jsp";
		}else if( IContent.UICLASS_TREE_MANAGE.equals(strClassName) ){
			return "manageTreeView.jsp";
		}else{
			return "singleView.jsp";
		}
	}
}
