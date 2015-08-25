package nc.pub.mdm.frame;

/**
 * NC61 主数据常量<br>
 * 
 * @author 周海茂
 * @since 2012-09-13
 */
public interface IContent {
	public static String ModuleName = "docmdm"; // 注意！该值必须与实际的modules中模块目录名
	public static String BusiChecker = "nc.bs.mdm.frame.BaseBusiChecker";
	public static String BusiQuery = "nc.bs.mdm.frame.BaseBusiQueryImpl";
	
	public static String UICLASS_LIST = "nc.ui.mdm.base.SingleBodyUI";
	public static String UICLASS_TREE = "nc.ui.mdm.base.TreeCardUI";
	public static String UICLASS_TREE_MANAGE = "nc.ui.mdm.base.TreeManageUI";
	
	public static int DEFAULT_PAGESIZE = 20;
}
