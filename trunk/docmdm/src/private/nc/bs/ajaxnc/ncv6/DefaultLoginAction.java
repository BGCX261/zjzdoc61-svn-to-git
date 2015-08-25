package nc.bs.ajaxnc.ncv6;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.ajaxnc.constant.ILoginConstant;
import nc.bs.ajaxnc.tools.RequestTool;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Logger;
import nc.bs.uap.sf.facility.SFServiceFacility;
import nc.pub.mdm.frame.BaseService;
import nc.pub.mdm.frame.BaseTimeMapFactory;
import nc.pub.mdm.frame.tool.Toolkit;
import nc.vo.mdm.frame.DocVO;
import nc.vo.mdm.frame.DocVOProcessor;
import nc.vo.mdm.frame.UserVOProcessor;
import nc.vo.org.CorpVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.sm.UserVO;
import nc.vo.sm.config.Account;
import nc.vo.sm.config.ConfigParameter;
import nc.vo.uap.rbac.util.RbacUserPwdUtil;

public class DefaultLoginAction implements ILoginAction{

	public boolean doLogin(HttpServletRequest request, HttpServletResponse response) {
		
		String strBrowserHeight = request.getParameter("browserHeight");
		String strbrowserWidth = request.getParameter("browserWidth");
		RequestTool.setAttributeSession(request, ILoginConstant.LOGIN_WEB_HEIGHT, strBrowserHeight);
		RequestTool.setAttributeSession(request, ILoginConstant.LOGIN_WEB_WIDTH, strbrowserWidth);

		String strDataSource = getLoginDsName(request);
		BaseService.setDefaultDataSource(strDataSource);
		InvocationInfoProxy.getInstance().setUserDataSource(strDataSource);
		
		String strUserCode = (String) RequestTool.getParam(request, ILoginConstant.LOGIN_USERCODE);
		String strUserPwd = (String) RequestTool.getParam(request, ILoginConstant.LOGIN_USERPWD);
		UserVO userVO = queryUser(request, strUserCode, strUserPwd);
		if (userVO != null) {
			RequestTool.setAttributeSession(request, ILoginConstant.LOGIN_VO_SMUSER, userVO);
			return true;

		} else {
			return false;
		}

	}
	
	@SuppressWarnings("unchecked")
	private static UserVO queryUser(HttpServletRequest request, String strUserCode, String strUserPwd) {
		UserVO userVO = null;
		Map<String,UserVO> userMap = BaseTimeMapFactory.getMap(UserVO.class.getName());
		if( userMap.size()==0 ){
			initUserMap(userMap);
		}
		
		userVO = userMap.get(strUserCode);
		if( userVO!=null ){
			
			boolean isOk = RbacUserPwdUtil.checkUserPassword(userVO, strUserPwd);
			if( !isOk ){
				userVO=null;
			}
		}
		
		if( userVO!=null ){
			String pk_corp = userVO.getPk_group();
			if( pk_corp==null || "0001".equals(pk_corp) ) {
				Map<String,Vector<CorpVO>> userCorpMap = BaseTimeMapFactory.getMap("");
				initUserOrgMap(userCorpMap);
				Vector<CorpVO> vecTemp = userCorpMap.get(userVO.getPrimaryKey());
				if( vecTemp!=null ){
					for (int i = 0; i < vecTemp.size(); i++) {
						CorpVO vo = vecTemp.get(i);
						if( !"0001".equals(vo.getPk_corp()) ){
							pk_corp = vo.getPk_corp();
						}
					}
				}
			}
			
			CorpVO corpVO = getCorpMap().get(pk_corp);
			if( corpVO!=null ){
				RequestTool.setAttributeSession(request, ILoginConstant.LOGIN_VO_CORP, corpVO);
				RequestTool.setAttributeSession(request, ILoginConstant.LOGIN_PK_CORP, pk_corp);
				String strLoginDate = new UFDate(System.currentTimeMillis()).toString().substring(0,10);
				RequestTool.setAttributeSession(request, ILoginConstant.LOGIN_DATE, strLoginDate);

			}
		}

//		try {
//			CorpVO corpVO = (CorpVO) BaseService.queryByPK(CorpVO.class, pk_corp);
//			RequestTool.setAttributeSession(request, IContents.LOGIN_VO_CORP, corpVO);
//		} catch (BusinessException e) {
//			Logger.error(e.getMessage(), e.getCause());
//		}

		
		return userVO;

	}
	
	@SuppressWarnings("unchecked")
	private  static void initUserMap(Map<String,UserVO> userMap){
		if( userMap.size()==0 ){
			String strNC60 ="select cuserid,disabledate,dr,enablestate,islocked,pk_group,pk_org,pwdlevelcode,pwdparam,user_code,user_name,user_note,user_password,user_type from sm_user";
			try {
				UserVOProcessor processor = new UserVOProcessor();
				List<UserVO> vos = (List<UserVO>) BaseService.excuteQuery(strNC60, null, processor);
				for (int i = 0; i < vos.size(); i++) {
					UserVO tempUserVO = vos.get(i);
					userMap.put(tempUserVO.getUser_code(), tempUserVO);
				}
			} catch (BusinessException e) {
				e.printStackTrace();
				Logger.error(e.getMessage(), e.getCause());
			}
			
		}
	}
	@SuppressWarnings("unchecked")
	private static void initUserOrgMap(Map<String,Vector<CorpVO>> userCorpMap){
		Map<String,CorpVO> corpMap = getCorpMap();
		if( userCorpMap.size()==0 ){
			String strNC60 ="select cuserid,pk_org from sm_user_role where nvl(dr,0)=0 group by cuserid,pk_org order by cuserid,pk_org";
			try {
				DocVOProcessor processor = new DocVOProcessor("sm_user_role",null);
				List<DocVO> vos = (List<DocVO>) BaseService.excuteQuery(strNC60, null, processor);
				for (int i = 0; i < vos.size(); i++) {
					DocVO tempUserVO = vos.get(i);
					String strUserID = (String)tempUserVO.getAttributeValue("cuserid");
					Vector<CorpVO> vecCorp = userCorpMap.get(strUserID);
					if( vecCorp == null ){
						vecCorp = new Vector<CorpVO>();
						userCorpMap.put(strUserID, vecCorp);
					}
					String pk_corp = (String)tempUserVO.getAttributeValue("pk_corp");
					CorpVO corpVO = corpMap.get(pk_corp);
					if( corpVO!=null ){
						vecCorp.add(corpVO);
					}
				}
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e.getCause());
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String,CorpVO> getCorpMap(){
		Map<String,CorpVO> corpMap = BaseTimeMapFactory.getMap(CorpVO.class.getName());
		if( corpMap.size() == 0 ){
			try {
				CorpVO[] vos = (CorpVO[])BaseService.queryByCondition(CorpVO.class, null);
				if( vos!=null ){
					for (int i = 0;  i < vos.length; i++) {
						corpMap.put(vos[i].getPk_corp(), vos[i]);
					}
				}
				
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e.getCause());
			}
		}
		return corpMap;
	}
	

	protected static String getLoginAccount(HttpServletRequest request) {
		return (String) RequestTool.getAttributeSession(request, ILoginConstant.LOGIN_VO_ACCOUNT);
	}

	protected static String getLoginDsName(HttpServletRequest request) {
		String strDataSource = (String) RequestTool.getAttributeSession(request, ILoginConstant.LOGIN_DATASOURCE);
		if (Toolkit.isNull(strDataSource) || "design".equals(strDataSource)) {
			String strLoginAccountCode = (String) RequestTool.getParam(request, ILoginConstant.LOGIN_ACCOUNT);
			if (!Toolkit.isNull(strLoginAccountCode)) {
				try {
					ConfigParameter configPara = SFServiceFacility.getConfigService().getAccountConfigPara();
					Account[] accounts = configPara.getAryAccounts();
					for (int i = 0; i < accounts.length; i++) {
						if ("0000".equals(strLoginAccountCode)) {
							strDataSource = accounts[i].getDataSourceName();
							if (Toolkit.isNull(strDataSource)) {
								continue;
							} else {
								break;
							}
						}

						if (accounts[i].getAccountCode().equals(strLoginAccountCode)) {
							strDataSource = accounts[i].getDataSourceName();
							RequestTool.setAttributeSession(request, ILoginConstant.LOGIN_DATASOURCE, strDataSource);
							RequestTool.setAttributeSession(request, ILoginConstant.LOGIN_VO_ACCOUNT, accounts[i]);
							break;
						}
					}
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e.getCause());
				}
			}
		}

		if (Toolkit.isNull(strDataSource)) {
			strDataSource = null;
		}
		return strDataSource;

	}

}
