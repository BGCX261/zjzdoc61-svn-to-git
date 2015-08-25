package nc.ui.mdm.base;

import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.AbstractFunclet;
import nc.sfbase.client.ClientToolKit;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.uif2.NodeTypeUtil;
import nc.vo.uif2.AppStatusRegistery;
import nc.vo.uif2.LoginContext;

public class UITool {

	public UITool() {
		ClientToolKit.getApplet();
		// WorkbenchEnvironment env = WorkbenchEnvironment.getInstance();
		// INCUserTypeConstant.USER_TYPE_USER
		// SFBaseUtil.getNCHomePath();
	}

	public static LoginContext makeLoginContext(AbstractFunclet ui) {
		LoginContext loginCtx = new LoginContext();
		loginCtx.setNodeType(NodeTypeUtil.funcregisterVO2NODE_TYPE(ui.getFuncletContext().getFuncRegisterVO()));
		loginCtx.setNodeCode(ui.getFuncCode());
		loginCtx.setPk_loginUser(WorkbenchEnvironment.getInstance().getLoginUser().getPrimaryKey());
		if (WorkbenchEnvironment.getInstance().getGroupVO() != null) {
			loginCtx.setPk_group(WorkbenchEnvironment.getInstance().getGroupVO().getPk_group());
		}
		loginCtx.setEntranceUI(ui);
		loginCtx.setFuncInfo(ui.getFuncletContext().getFuncSubInfo());
		if (ui.getFuncletContext().getFuncSubInfo() != null) {
			String pkorgs[] = ui.getFuncletContext().getFuncSubInfo().getFuncPermissionPkorgs();
			loginCtx.setPkorgs(pkorgs);
		}

		AppStatusRegistery statusRegistery = new AppStatusRegistery();
		String pkUser = WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();
		statusRegistery.load(ui.getFuncCode(), pkUser);
		loginCtx.setStatusRegistery(statusRegistery);
		return loginCtx;
	}

	//	public static BillTempletVO getTemplet(String strBillType) {
	//		return TempletTool.queryTempletByCode(strBillType);
	//	}
	
	public static File getSeletedFile(Class<?> clz){
		Preferences preferences = Preferences.userNodeForPackage( clz );
		UIFileChooser chooser = new UIFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		String preferencesDir = preferences.get("document", System.getProperty("user.dir"));
		chooser.setCurrentDirectory(new File(preferencesDir));
		
		chooser.showOpenDialog(ClientToolKit.getApplet());
		File selectedFile = chooser.getSelectedFile();
		if (selectedFile != null) {
			preferences.put("document", selectedFile.getParent());
		}
		return selectedFile;
	}
}
