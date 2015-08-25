package nc.bs.ajaxnc.tools;

import java.util.Vector;

import nc.vo.mdm.frame.MenuVO;

public class MenuTool {

	@SuppressWarnings({ "rawtypes" })
	public static Vector groupMenu(Vector vecSub, int iMax) {
		Vector<MenuVO> vecAll = new Vector<MenuVO>();
		groupMenuRecursion(vecAll, vecSub, 0);
		
		Vector<MenuVO> tempVec = new Vector<MenuVO>();
		Vector<Vector<MenuVO>> ret = new Vector<Vector<MenuVO>>();
		ret.add(tempVec);
		for (MenuVO mvo : vecAll) {
			if (tempVec.size() > iMax) {
				tempVec = new Vector<MenuVO>();
				ret.add(tempVec);
			} else if (tempVec.size() > (iMax - 1) && mvo.getSubMenuVec() != null && mvo.getSubMenuVec().size() > 0) {
				tempVec = new Vector<MenuVO>();
				ret.add(tempVec);
			}
			tempVec.add(mvo);
		}
		return ret;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void groupMenuRecursion(Vector vecAll, Vector vecSub, int deep) {
		for (int i=0; i<vecSub.size(); i++) {
			MenuVO mvo = (MenuVO)vecSub.get(i);
			// 必须得clone，不然每次都会添加，导致……严重后果
			MenuVO showVO = (MenuVO)mvo.clone();
			StringBuffer buff = new StringBuffer( showVO.getMenuName());
			for (int j = 0; j < deep; j++) {
				buff.insert(0, "&nbsp;");
			}
			showVO.setMenuName( buff.toString() );
			vecAll.add(showVO);
			if (showVO.getSubMenuVec() != null && showVO.getSubMenuVec().size() > 0) {
				groupMenuRecursion(vecAll, showVO.getSubMenuVec(), deep+1);
			}
		}
	}


}
