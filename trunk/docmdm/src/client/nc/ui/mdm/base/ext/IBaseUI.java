package nc.ui.mdm.base.ext;

import nc.vo.uif2.LoginContext;


public interface IBaseUI {
	
	public static String MDM_EXT = "nc.ui.mdm.ext";
	public static String MDM_EXT_BTN = MDM_EXT+".Button";
	public static String MDM_EXT_EVT = MDM_EXT+".Event";

	public void doAdd() ;
	
	public void setRowEditable(int iRow);

	public void doCancel() ;

	public void doDelete() ;

	public void doEdit();

	public void doRefresh();

	public void doSave() throws Exception;
	
	public LoginContext getLoginCtx();

}
