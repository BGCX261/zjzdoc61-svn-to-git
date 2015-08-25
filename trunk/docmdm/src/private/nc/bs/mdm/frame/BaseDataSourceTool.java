package nc.bs.mdm.frame;

import nc.bs.uap.sf.facility.SFServiceFacility;
import nc.pub.mdm.frame.BaseService;
import nc.pub.mdm.frame.tool.Toolkit;
import nc.vo.pub.BusinessException;
import nc.vo.sm.config.Account;
import nc.vo.sm.config.ConfigParameter;

/**
 * 后台数据源工具
 * @author 周海茂
 * @since 2012-8-28
 */
public class BaseDataSourceTool {
	
	private static String strDataSource = getDataSource();
	
	public static void initDataSource(){
		BaseService.setDefaultDataSource(strDataSource);
		BaseService.setThreadDsName(strDataSource);
	}
	
	private static String getDataSource(){
		
		ConfigParameter configPara;
		try {
			configPara = SFServiceFacility.getConfigService().getAccountConfigPara();
			Account[] accounts = configPara.getAryAccounts();
			for (int i = 0; i < accounts.length; i++) {
				strDataSource = accounts[i].getDataSourceName();
				if (Toolkit.isNull(strDataSource)) {
					continue;
				} else {
					break;
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return strDataSource;
	}

}
