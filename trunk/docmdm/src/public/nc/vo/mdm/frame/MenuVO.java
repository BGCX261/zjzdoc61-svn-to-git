package nc.vo.mdm.frame;

import java.util.Vector;

import nc.vo.pub.lang.UFBoolean;

public class MenuVO extends DocVO implements Cloneable{

	private static final long serialVersionUID = -5800835003756503098L;
	
	private static final String MEMU_CODE = "menuCode", MENU_NAME = "menuName", MENU_URL="menuURL", MENU_PCODE="menuParentCode", MENU_SUB_VEC = "subMenuVec", MENU_IS_FOCUS = "isFocus";
	
	public String getMenuClass(){
		return(String)getAttributeValue("class_name");
	}
	public String getMenuCode() {
		return(String)getAttributeValue(MEMU_CODE);
	}

	public String getMenuParentCode() {
		return(String)getAttributeValue(MENU_PCODE);
	}

	public String getMenuName() {
		return(String)getAttributeValue(MENU_NAME);
	}

	public String getMenuURL() {
		return(String)getAttributeValue( MENU_URL );
	}

	@SuppressWarnings("unchecked")
	public Vector<MenuVO> getSubMenuVec() {
		Object obj = getAttributeValue( MENU_SUB_VEC );
		if( obj!=null ){
			return (Vector<MenuVO>)obj;
		}
		return null;
	}

	public boolean isFocus() {
		Object obj = getAttributeValue(MENU_IS_FOCUS);
		if( obj!=null && obj instanceof UFBoolean ){
			return ((UFBoolean)obj).booleanValue();
		}
		return false;
	}

	public void setFocus(boolean isFocus) {
		setAttributeValue(MENU_IS_FOCUS, new UFBoolean(isFocus) );
	}

	public void setMenuCode(String menuCode) {
		setAttributeValue(MEMU_CODE, menuCode );
	}

	public void setMenuName(String menuName) {
		setAttributeValue(MENU_NAME, menuName );
	}

	public void setMenuParentCode(String menuParentCode) {
		setAttributeValue(MENU_PCODE, menuParentCode );
	}

	public void setMenuURL(String menuURL) {
		setAttributeValue(MENU_URL, menuURL );
	}

	public void setSubMenuVec(Vector<MenuVO> subMenuVec) {
		setAttributeValue(MENU_SUB_VEC, subMenuVec );
	}
}
