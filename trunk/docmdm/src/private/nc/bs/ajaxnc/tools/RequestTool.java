package nc.bs.ajaxnc.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.ajaxnc.base.BaseCtrl;
import nc.bs.ajaxnc.constant.ILoginConstant;
import nc.pub.mdm.frame.BaseService;
import nc.pub.mdm.frame.BaseTimeMapFactory;
import nc.pub.mdm.frame.IContent;
import nc.pub.mdm.frame.tool.EncryptTool;
import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.frame.tool.Toolkit;
import nc.vo.mdm.frame.DocVO;
import nc.vo.mdm.frame.MenuVO;
import nc.vo.mdm.frame.PageParam;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author zhouhaimao
 * @since 2012-03-29
 */
public class RequestTool {

	public static String getTempletCode(ServletRequest request) {
		if (request != null) {
			return request.getParameter(ILoginConstant.PARAM_TEMPLET_CODE);
		}
		return null;
	}

	public static String getRootMenuPK(HttpServletRequest request) {
		String sessionPK = (String) request.getSession().getAttribute(ILoginConstant.LOGIN_AJAX_ROOTPK);

		if (sessionPK == null) {
			sessionPK = ILoginConstant.LOGIN_AJAX_ROOTPK;
		}
		return sessionPK;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, MenuVO> initMenu(HttpServletRequest request) {

		Map<String, MenuVO> menuMap = (Map<String, MenuVO>)BaseTimeMapFactory.getMap( MenuVO.class.getName()); //RequestTool.getAttributeSession(request, ILoginConstant.LOGIN_SESSION_MENUS);
		if (menuMap == null || menuMap.size()==0) {
			menuMap = new HashMap<String, MenuVO>();
			RequestTool.setAttributeSession(request, ILoginConstant.LOGIN_SESSION_MENUS, menuMap);

			Vector<MenuVO> vecMenusFirst = new Vector<MenuVO>();
			RequestTool.setAttributeSession(request, ILoginConstant.ATTR_SELECTED_MENU_FIRST, vecMenusFirst);

			StringBuffer buffSQL = new StringBuffer();
			buffSQL.append("(");
			buffSQL.append("select moduleid fun_code, parentcode parent_code, systypename fun_name,'' class_name, moduleid id, parentcode pid from dap_dapsystem where moduleid like '79%'");
			buffSQL.append(") union (");
			buffSQL.append("select funcode fun_code, own_module parent_code, fun_name fun_name,class_name, cfunid id,own_module pid from sm_funcregister where funcode like '79%' and PARENT_ID is null");
			buffSQL.append(") union (");
			buffSQL.append("select subfunc.funcode fun_code, pfunc.funcode parent_code, subfunc.fun_name fun_name,subfunc.class_name,subfunc.cfunid id,subfunc.PARENT_ID pid from sm_funcregister subfunc left join sm_funcregister pfunc on subfunc.parent_id=pfunc.cfunid where subfunc.funcode like '79%' and subfunc.PARENT_ID is not null");
			buffSQL.append(") order by fun_code");

			try {
				DocVO[] vos = BaseService.queryMainData(buffSQL.toString(), "dap_dapsystem", "id", null);// (strSQL, null);

				if (vos != null && vos.length > 0) {
					for (int i = 0; i < vos.length; i++) {
						MenuVO mvo = initMenuVO(vos[i]);
						menuMap.put(mvo.getMenuCode(), mvo);
						
						if ( mvo.getMenuParentCode() != null) {
							MenuVO pmvo = menuMap.get( mvo.getMenuParentCode() );
							if (pmvo != null) {
								Vector<MenuVO> subVec = pmvo.getSubMenuVec();
								if (subVec == null) {
									subVec = new Vector<MenuVO>();
									pmvo.setSubMenuVec(subVec);
								}
								if (!subVec.contains(mvo)) {
									subVec.add(mvo);
								}
								
							}
						}else if( mvo.getMenuParentCode()==null || ILoginConstant.LOGIN_AJAX_ROOTPK.equals(mvo.getMenuParentCode().trim())){
							MenuVO rootMenu = new MenuVO();
							rootMenu.setMenuCode(ILoginConstant.LOGIN_AJAX_ROOTPK);
							rootMenu.setSubMenuVec(new Vector<MenuVO>());
							menuMap.put(ILoginConstant.LOGIN_AJAX_ROOTPK, rootMenu);
							rootMenu.getSubMenuVec().add(mvo);
						}
					}
				}
			} catch (BusinessException e) {
				LogTool.error(e);
			}
		}
		
		return menuMap;

	}
	
	private static MenuVO initMenuVO(DocVO vo){

		String strFunCode = (String) vo.getAttributeValue("fun_code");
		String strFunName = (String) vo.getAttributeValue("fun_name");
		String strParentCode = (String) vo.getAttributeValue("parent_code");

		String strClassName = (String) vo.getAttributeValue("class_name");
		String id = (String) vo.getAttributeValue("id");
		String pid = (String) vo.getAttributeValue("pid");

		MenuVO mvo = new MenuVO();
		mvo = new MenuVO();
		mvo.setSubMenuVec(new Vector<MenuVO>());

		mvo.setMenuCode(strFunCode);
		mvo.setMenuName(strFunName);
		mvo.setMenuParentCode(strParentCode);
		mvo.setAttributeValue("class_name", strClassName);
		mvo.setAttributeValue("id", id);
		mvo.setAttributeValue("pid", pid);

		if (!Toolkit.isNull(strClassName)) {
			if ( IContent.UICLASS_LIST.equals(strClassName)) {
				mvo.setMenuURL(mvo.getMenuCode());

			} else if (IContent.UICLASS_TREE.equals(strClassName)) {
				mvo.setMenuURL( mvo.getMenuCode());

			} else if (IContent.UICLASS_TREE_MANAGE.equals(strClassName)) {
				mvo.setMenuURL( mvo.getMenuCode());
			}
		}

		return mvo;
	}

	@SuppressWarnings("unchecked")
	public static MenuVO getMenu(HttpServletRequest request, String menuCode) {
		Map<String, MenuVO> mapSessMenus = (Map<String, MenuVO>) getAttributeSession(request, ILoginConstant.LOGIN_SESSION_MENUS);
		MenuVO retVO = mapSessMenus.get(menuCode);
		return retVO;
	}

	public static Vector<MenuVO> getMenuFirst(HttpServletRequest request) {
		Vector<MenuVO> vecChilds = null;
		String strRootPK = getRootMenuPK(request);
		MenuVO menu = getMenu(request, strRootPK);
		if (menu != null) {
			vecChilds = menu.getSubMenuVec();
		}
		return vecChilds;
	}

	@SuppressWarnings("rawtypes")
	public static Vector getMenuSecond(HttpServletRequest request) {

		String strFirstMenuPK = (String) getAttributeSession(request, ILoginConstant.ATTR_SELECTED_MENU);
		Vector<MenuVO> vecChilds = null;
		MenuVO menu = getMenu(request, strFirstMenuPK);
		if (menu != null) {
			vecChilds = menu.getSubMenuVec();
		}
		return vecChilds;
	}

	public static String getWebWidth(HttpServletRequest request) {
		return (String) request.getSession().getAttribute(ILoginConstant.LOGIN_WEB_WIDTH);
	}

	public static String getWebHeight(HttpServletRequest request) {
		return (String) request.getSession().getAttribute(ILoginConstant.LOGIN_WEB_HEIGHT);
	}

	public static String getWebPanelHeight(HttpServletRequest request) {
		String strWebHeight = getWebHeight(request);
		String strWebPanelHeight = "" + (Integer.parseInt(strWebHeight) - 56 - 44);
		return strWebPanelHeight;
	}

	public static String getWebPanelBodyHeight(HttpServletRequest request) {
		// return getWebPanelHeight(request);
		String strWebHeight = getWebPanelHeight(request);
		String strWebPanelHeight = "" + (Integer.parseInt(strWebHeight) - 25);
		return strWebPanelHeight;
	}

	public static String getWebPanelBodyWidth(HttpServletRequest request) {
		// return getWebWidth(request);
		String strWebWidth = getWebWidth(request);
		String strWebPanelWidth = "" + (Integer.parseInt(strWebWidth)-15);
		return strWebPanelWidth;
	}

	public static String getRefType(ServletRequest request) {
		String strRefType = request.getParameter(ILoginConstant.PARAM_REFNAME);
		if (strRefType != null) {
			strRefType = new String(EncryptTool.byteFromHexString(strRefType));
		}
		return strRefType;
	}

	public static Object getAttributeSession(ServletRequest request, String strKey) {
		if (request instanceof HttpServletRequest) {
			return getAttributeSession((HttpServletRequest) request, strKey);
		} else {
			Object objRet = request.getAttribute(strKey);
			if (objRet == null) {
				objRet = request.getAttribute(strKey);
			}
			return objRet;
		}
	}

	public static Object getAttributeSession(HttpServletRequest request, String strKey) {
		Object objRet = request.getAttribute(strKey);
		if (objRet == null) {
			objRet = request.getSession().getAttribute(strKey);
			if (objRet == null) {
				objRet = request.getParameter(strKey);
			}
		}
		return objRet;
	}

	public static String getParam(HttpServletRequest request, String strKey) {
		String objRet = request.getParameter(strKey);
		return objRet;
	}

	public static String getLoginCorpPK(HttpServletRequest request) {
		return (String) getAttributeSession(request, ILoginConstant.LOGIN_PK_CORP);
	}

	public static String getLoginDataSource(HttpServletRequest request) {
		return (String) getAttributeSession(request, ILoginConstant.LOGIN_DATASOURCE);
	}

	public static UserVO getLoginUser(HttpServletRequest request) {
		return (UserVO) getAttributeSession(request, ILoginConstant.LOGIN_VO_SMUSER);
	}

	public static void setAttributeSession(HttpServletRequest request, String strKey, Object value) {
		request.setAttribute(strKey, value);
		request.getSession().setAttribute(strKey, value);
	}

	public static Object getSpringBean(HttpServletRequest request, String strBeanName) {
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
		return ctx.getBean(strBeanName);
	}

	public static void setCookie(HttpServletResponse response, String strKey, String strValue) {
		Cookie c = new Cookie(strKey, strValue);
		c.setMaxAge(7 * 24 * 60 * 60);
		// c.setDomain("sintal.cn");
		// c.setPath("/");
		// String userID="ÖÐÎÄ";
		// String data = userID+"|"+password;
		// c.setValue(URLEncoder.encode(data,"UTF-8"));

		response.addCookie(c);
	}

	public static String getCookie(HttpServletRequest request, String strKey) {
		String strCookieValue = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (strKey.equals(cookie.getName())) {
					strCookieValue = cookie.getValue();
					break;
				}
			}
		}
		return strCookieValue;

	}

	public static PageParam makePageParam(BaseCtrl ctrl, HttpServletRequest request) {
		PageParam pp = (PageParam) request.getAttribute(ILoginConstant.ATTR_PAGEPARAM);
		if (pp == null) {
			String strSessKey = ctrl.getClass().getName() + "_" + ILoginConstant.ATTR_PAGEPARAM;
			pp = (PageParam) request.getSession().getAttribute(strSessKey);
			if (pp == null) {
				pp = new PageParam();
			}
		}

		String strPage = request.getParameter(ILoginConstant.PARAM_PAGE);
		String strPageSize = request.getParameter(ILoginConstant.PARAM_PAGESIZE);

		Integer iPage = null;
		if (Toolkit.isNull(strPage)) {
			iPage = (Integer) request.getAttribute(ILoginConstant.PARAM_PAGE);
		} else {
			iPage = new Integer(strPage.trim());
		}
		Integer iPageSize = null;
		if (Toolkit.isNull(strPage)) {
			iPageSize = (Integer) request.getAttribute(ILoginConstant.PARAM_PAGESIZE);
		} else {
			iPageSize = new Integer(strPageSize.trim());
		}

		pp.setPage(iPage);
		pp.setPageSize(iPageSize);

		return pp;
	}

}
