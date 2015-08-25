package nc.bs.ajaxnc.ncv6;

import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.ajaxnc.base.BaseCtrl;
import nc.bs.ajaxnc.constant.ILoginConstant;
import nc.bs.ajaxnc.tools.AjaxTool;
import nc.bs.ajaxnc.tools.HtmlTool;
import nc.bs.ajaxnc.tools.RequestTool;
import nc.bs.uap.sf.facility.SFServiceFacility;
import nc.ui.pub.bill.IBillItem;
import nc.vo.mdm.frame.MenuVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.sm.UserVO;
import nc.vo.sm.config.Account;
import nc.vo.sm.config.ConfigParameter;

public class DefaultLoginCtrl extends BaseCtrl {

	private String viewDir = "/login/";

	private ILoginAction loginAction = null;

	public DefaultLoginCtrl() {

	}
	
	public void ajaxGetMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String menuCode = (String) request.getParameter("menuCode");
		Map<String, MenuVO> menuMap = filtUserMenu(request, response);
		MenuVO mvo = menuMap.get(menuCode);
		String strXML = AjaxTool.makeAjaxXML(mvo);
		writeXML(response, strXML);
	}

	protected int countSubMenus(MenuVO menuVO) {
		int i = 0;
		if (menuVO != null) {
			i = 1;
			if (menuVO.getSubMenuVec() != null && menuVO.getSubMenuVec().size() > 0) {
				for (MenuVO vo : menuVO.getSubMenuVec()) {
					i += countSubMenus(vo);
				}
			}
		}
		return i;
	}

	public String doLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean loginOK = getLoginAction().doLogin(request, response);
		if (loginOK) {
			UserVO userVO = (UserVO) RequestTool.getAttributeSession(request, ILoginConstant.LOGIN_VO_SMUSER);
			RequestTool.setCookie(response, ILoginConstant.LOGIN_USERCODE, userVO.getUser_code());
		}

		return onNavigator(request, response);
	}

	public void doLogoff(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RequestTool.setAttributeSession(request, ILoginConstant.LOGIN_VO_SMUSER, null);
		request.getSession().invalidate();
	}

	private Map<String, MenuVO> filtUserMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, MenuVO> menuMap = RequestTool.initMenu(request);
		UserVO userVO = (UserVO) RequestTool.getAttributeSession(request, ILoginConstant.LOGIN_VO_SMUSER);
		// FIXME: to filt user power
		return menuMap;
	}

	@Override
	protected String getDefaultMethod() {
		return "onLogin";
	}

	public ILoginAction getLoginAction() {
		return loginAction;
	}

	public String getViewDir() {
		return viewDir;
	}

	public String onLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (hasLogin(request)) {
			return "frame.jsp";
		} else {
			return "login.jsp";
		}
	}

	public String onMakeLoginAccountCbx(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String strRefHTML = null;
		// String strCtxPath = request.getContextPath();
		// UserVO userVO = RequestTool.getLoginUser(request);
		// String pk_corp = RequestTool.getLoginCorpPK(request);
		String strHtmlID = request.getParameter("htmlID");
		String strCookieAccountCode = RequestTool.getCookie(request, ILoginConstant.LOGIN_ACCOUNT);

		ConfigParameter configPara = SFServiceFacility.getConfigService().getAccountConfigPara();
		Account[] accounts = configPara.getAryAccounts();
		StringBuffer buff = new StringBuffer(IBillItem.COMBOTYPE_VALUE_X);
		for (int i = 0; i < accounts.length; i++) {
			if (!"0000".equals(accounts[i].getAccountCode())) {
				buff.append(",").append(accounts[i].getAccountName()).append("=").append(accounts[i].getAccountCode());
			}
		}
		strRefHTML = HtmlTool.makeHtmlComboBox(strHtmlID, buff.toString(), strCookieAccountCode, 160, true, 1);
		request.setAttribute("accounts", accounts);
		writeHTML(request, response, strRefHTML);
		return null;
	}
	public String onMakeLoginRef(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int iLoginRefWidth = 160;
		String strRefHTML = null;
		// String strCtxPath = request.getContextPath();
		String htmlID = request.getParameter("htmlID");
		UserVO userVO = RequestTool.getLoginUser(request);
		String pk_corp = RequestTool.getLoginCorpPK(request);

		if (htmlID == null || ILoginConstant.LOGIN_PK_CORP.equals(htmlID)) {
			String strRefType = RequestTool.getRefType(request);
			String strCookieCorpPK = RequestTool.getCookie(request, ILoginConstant.LOGIN_PK_CORP);
			strRefHTML = HtmlTool.makeHtmlRef(htmlID, strRefType, strCookieCorpPK, userVO, pk_corp, iLoginRefWidth, true, 2);

		} else if (ILoginConstant.LOGIN_DATE.equals(htmlID)) {
			String strToday = ("notToday".equals(request.getParameter("notToday")) ? null : new UFDate().toString());
			strRefHTML = HtmlTool.makeHtmlDate(htmlID, strToday, iLoginRefWidth, true, 3);

		} else if (ILoginConstant.LOGIN_USERCODE.equals(htmlID)) {
			String strCookieUserCode = RequestTool.getCookie(request, ILoginConstant.LOGIN_USERCODE);
			strRefHTML = HtmlTool.makeHtmlNormalInput(htmlID, strCookieUserCode, iLoginRefWidth, 4, true, false, false);

		} else if (ILoginConstant.LOGIN_USERPWD.equals(htmlID)) {
			strRefHTML = HtmlTool.makeHtmlNormalInput(htmlID, null, iLoginRefWidth, 5, true, false, true);
		}
		writeHTML(request, response, strRefHTML);
		return null;
	}
	
	public String onNavigator(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (hasLogin(request)) {
			Map<String, MenuVO> menuMap = filtUserMenu(request, response);
			Vector<MenuVO> vecFirst = null, vecSecond = null, vecMain = null;
			MenuVO rootMenuVO = menuMap.get(ILoginConstant.LOGIN_AJAX_ROOTPK);
			vecFirst = rootMenuVO.getSubMenuVec();
			MenuVO focusFirst = null;
			MenuVO focusSecond = null;
			String strFocusMenu = (String) request.getParameter(ILoginConstant.ATTR_SELECTED_MENU);
			if (strFocusMenu != null && menuMap.get(strFocusMenu) != null) {
				MenuVO tempFocus = menuMap.get(strFocusMenu);
				if (ILoginConstant.LOGIN_AJAX_ROOTPK.equals(tempFocus.getMenuParentCode())) {
					focusFirst = tempFocus;
				} else {
					// select Second Menu
					focusFirst = menuMap.get(tempFocus.getMenuParentCode());
					focusSecond = tempFocus;
					vecSecond = focusFirst.getSubMenuVec();
				}
			}

			if (focusFirst == null && vecFirst != null) {
				focusFirst = vecFirst.get(0);
				vecSecond = focusFirst.getSubMenuVec();
			}

			if (focusSecond == null && vecSecond != null) {
				focusSecond = vecSecond.get(0);
			}

			if (focusSecond != null) {
				vecMain = focusSecond.getSubMenuVec();
			}

			resetFocus(vecFirst, vecSecond, focusFirst, focusSecond);

			request.setAttribute(ILoginConstant.ATTR_SELECTED_MENU_FIRST, vecFirst);
			request.setAttribute(ILoginConstant.ATTR_SELECTED_MENU_SECOND, vecSecond);
			request.setAttribute(ILoginConstant.ATTR_SELECTED_MENU_MAIN, vecMain);

			return "navigator.jsp";
		}
		return "login.jsp";
	}

	private void resetFocus(Vector<MenuVO> vecFirst, Vector<MenuVO> vecSecond, MenuVO focusFirst, MenuVO focusSecond) {
		if (vecFirst != null) {
			for (MenuVO mvo : vecFirst) {
				mvo.setFocus(false);
			}
		}
		if (vecSecond != null) {
			for (MenuVO mvo : vecSecond) {
				mvo.setFocus(false);
			}
		}

		if (focusFirst != null) {
			focusFirst.setFocus(true);
		} else if (vecFirst != null && vecFirst.get(0) != null) {
			vecFirst.get(0).setFocus(true);
		}

		if (focusSecond != null) {
			focusSecond.setFocus(true);
		} else if (vecSecond != null && vecSecond.get(0) != null) {
			vecSecond.get(0).setFocus(true);
		}

	}

	public void setLoginAction(ILoginAction loginAction) {
		this.loginAction = loginAction;
	}

	public void setViewDir(String viewDir) {
		this.viewDir = viewDir;
	}
}
