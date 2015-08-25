package nc.bs.ajaxnc.constant;

public interface ILoginConstant {
	
	public static int NC_TEMPLET_POS_HEAD = 0;
	public static int NC_TEMPLET_POS_BODY = 1;
	public static int NC_TEMPLET_POS_FOOT = 2;
	
	public static int FONT_SIZE_BYTE_WITH = 6;
	public static int FONT_SIZE_MAX_WITH = 150;
	
	public static String ERROR_JSP_500 = "error500.jsp";
	public static String ERROR_JSP_404 = "error404.jsp";
	public static String ERROR_POP_MSG = "ufida_error_pop_message";
	public static String BREAK_JSP = "ufida_break_jsp";

	public static String LOGIN_DATASOURCE	= "ufida_login_datasource";
	public static String LOGIN_ACCOUNT 		= "ufida_login_account";
	public static String LOGIN_DATE 		= "ufida_login_date";
	public static String LOGIN_PK_CORP 		= "ufida_login_pkcorp";
	public static String LOGIN_USERCODE 	= "ufida_login_usercode";
	public static String LOGIN_USERPWD	 	= "ufida_login_userpwd";

	public static String LOGIN_VO_ACCOUNT 	= "ufida_login_accountvo";
	public static String LOGIN_VO_CORP	 	= "ufida_login_corpvo";
	public static String LOGIN_VO_SMUSER 	= "ufida_login_uservo";
	public static String LOGIN_VO_PSNBASDOC = "ufida_login_psnvo";
	public static String LOGIN_VO_DEPTDOC 	= "ufida_login_deptvo";

	public static String LOGIN_WEB_WIDTH 		= "ufida_login_width";
	public static String LOGIN_WEB_HEIGHT 		= "ufida_login_height";
	
	public static String LOGIN_AJAX_ROOTPK		= "~"; //select * from dap_dapsystem where moduleid like '79%'
	public static String LOGIN_SESSION_MENUS		= "Login_User_Menus";
	
	public static String ATTR_SELECTED_MENU 		= "selectedMenu";
	public static String ATTR_SELECTED_MENU_FIRST 	= "ufida_menu_first";
	public static String ATTR_SELECTED_MENU_SECOND 	= "ufida_menu_second";
	public static String ATTR_SELECTED_MENU_MAIN 	= "ufida_menu_main";
	
	public static String ATTR_HANDLER = "ufida_handler_controller";
	public static String ATTR_TEMPLET_CLASS = "ufida_templet_class";
	public static String ATTR_TEMPLET_PK = "ufida_templet_pk";
	public static String ATTR_LIST_VO_CLZ = "ufida_list_voclass";
	public static String ATTR_LIST_WEBVOS = "ufida_list_webvos";
	public static String ATTR_MESSAGE = "ufida_message";
	public static String ATTR_PAGEPARAM = "ufida_pageparam";
	public static String ATTR_EDIT_VO_HEAD = "ufida_edit_vohead";
	public static String ATTR_EDIT_VO_BODY = "ufida_edit_vobodys";
	
	public static String HTML_SELECTED_PKS = "ufida_Global_Selected_PKS";

	public static String PARAM_TEMPLET_PK = "templetPK";
	public static String PARAM_TEMPLET_CODE = "templetCode";
	public static String PARAM_MENUCODE = "menuCode";
	public static String PARAM_REFNAME = "refName";
	public static String PARAM_REFPK = "refPK";
	public static String PARAM_REF_INPUTID = "refInputId";
	public static String PARAM_REFCODE = "refCode";
	public static String PARAM_REFMODEL = "refModel";
	public static String PARAM_REF_FIELD_PK = "field_pk";
	public static String PARAM_REF_FIELD_CODE = "field_code";
	public static String PARAM_REF_FIELD_NAME = "field_name";
	public static String PARAM_IS_BASE64 = "isBase64";
	public static String PARAM_PAGE = "page";
	public static String PARAM_PAGESIZE = "pageSize";
	public static String PARAM_WHERE = "where";
	public static String PARAM_WEB_WHERE = "webWhere";
	public static Integer PARAM_PAGE_DEFAULT = new Integer(1);
	public static Integer PARAM_PAGESIZE_DEFAULT = new Integer(20);
	public static String PARAM_COSTSUBJ_PARENT = "costsubj_parent_map";
	
	public static String PARSER_CHARSET = "utf-8";
	public static String PARSER_CHARSET_KEY = "charset=";
	
	public static Integer DEFAULT_PAGE = new Integer(1);
	public static Integer DEFAULT_PAGESIZE = new Integer(20);
	
	public static String ERROR_KEY = "Ufida_BusinessException";
}
