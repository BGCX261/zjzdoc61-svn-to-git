package nc.bs.ajaxnc.tools;

import java.util.Map;
import java.util.Vector;

import nc.vo.mdm.frame.DocVO;


public class AjaxTool {
	public static String makeAjaxXML(DocVO vo){
		
		StringBuffer buff = new StringBuffer();//"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		if( vo!=null ){
			String clzName = vo.getClass().getName();
			clzName = clzName.substring( clzName.lastIndexOf(".") + 1);
			buff.append("<").append(clzName).append(" ");
			String[] attrs = vo.getAttributeNames();
			for (int i = 0; i < attrs.length; i++) {
				buff.append(attrs[i]).append("=\"");
				Object objValue = vo.getAttributeValue(attrs[i]);
				if( objValue!=null && !(objValue instanceof Map || objValue instanceof Vector) ){
					buff.append( objValue.toString().trim() );
				}
				buff.append("\" ");
			}
			buff.append("></").append(clzName).append(">");
		}
		return buff.toString();
	}
}
