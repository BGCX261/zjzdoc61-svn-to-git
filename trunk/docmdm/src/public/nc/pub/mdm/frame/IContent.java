package nc.pub.mdm.frame;

/**
 * NC61 �����ݳ���<br>
 * 
 * @author �ܺ�ï
 * @since 2012-09-13
 */
public interface IContent {
	public static String ModuleName = "docmdm"; // ע�⣡��ֵ������ʵ�ʵ�modules��ģ��Ŀ¼��
	public static String BusiChecker = "nc.bs.mdm.frame.BaseBusiChecker";
	public static String BusiQuery = "nc.bs.mdm.frame.BaseBusiQueryImpl";
	
	public static String UICLASS_LIST = "nc.ui.mdm.base.SingleBodyUI";
	public static String UICLASS_TREE = "nc.ui.mdm.base.TreeCardUI";
	public static String UICLASS_TREE_MANAGE = "nc.ui.mdm.base.TreeManageUI";
	
	public static int DEFAULT_PAGESIZE = 20;
}
