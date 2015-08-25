package nc.bs.ajaxnc.tools;

import java.util.ArrayList;

import nc.bs.ajaxnc.constant.IHtmlConstant;
import nc.bs.ajaxnc.constant.ILoginConstant;
import nc.bs.logging.Logger;
import nc.pub.mdm.frame.tool.EncryptTool;
import nc.pub.mdm.frame.tool.Toolkit;
import nc.ui.bd.mmpub.DataDictionaryReader;
import nc.ui.pub.bill.IBillItem;
import nc.vo.sm.UserVO;

/**
 * @author zhouhaimao
 * @since 2012-03-29
 */
public class HtmlTool {

	public static String getInputValue(String strValue){
		return strValue==null?"":strValue.trim();
	}

	public static String makeHtmlComboBox(String strHtmlID, String strComboBoxRefType, String strSelectedValue, int iWidth, boolean isEdit, int iTabIndex) {
		String strHtmlDivID = strHtmlID + IHtmlConstant.TAIL_DIV;
		//String strHtmlProxyID = strHtmlID + IHtmlContents.TAIL_PROXY;
		StringBuffer buff = new StringBuffer();

		buff.append("<div id=\"" + strHtmlDivID + "\" name=\"" + strHtmlDivID + "\" style=\"width:" + iWidth + "px;\">");
		if (isEdit) {
			buff.append("<select class=\"ufidaComboBox\" name=\"" + strHtmlID + "\" id=\"" + strHtmlID + "\"");
			buff.append(" onchange=\"ufidaInputChange(this);\" ");
			buff.append(" tabIndex=\"" + iTabIndex + "\"");
			buff.append(" style=\"width:100%;border:0px;\">");
			makeHtmlComboBoxValue(buff, strComboBoxRefType, strSelectedValue);
			buff.append("</select>");
		} else {
			buff.append("<input name=\"" + strHtmlID + "\" id=\"" + strHtmlID + "\" type=\"text\" value=\"" + strSelectedValue + "\" readOnly style=\"background-color:#E7EEF8;width:" + iWidth + "px\" >");
		}
		buff.append("</div>");
		return buff.toString();
	}
	
	public static String getComboxShowValue(String strComboBoxRefType, String strSelectedValue){
		return makeHtmlComboBoxValue(null, strComboBoxRefType, strSelectedValue);
	}
	
	private static String makeHtmlComboBoxValue(StringBuffer buff, String strComboBoxRefType, String strSelectedValue) {
		String strShowName = "";
		String[] items = strComboBoxRefType.split(",");
		String strComboType = items[0].trim().toUpperCase();
		String[] strArray = new String[] { IBillItem.COMBOTYPE_INDEX, IBillItem.COMBOTYPE_INDEX_X, IBillItem.COMBOTYPE_VALUE, IBillItem.COMBOTYPE_VALUE_X };

		// boolean isSX = IBillItem.COMBOTYPE_VALUE_X.equals(items[0]); // SX
		// boolean isIX = IBillItem.COMBOTYPE_INDEX_X.equals(items[0]); // IX

		if ( Toolkit.getStringIndexOfArray(strArray, items[0]) >= 0 ) {
			ArrayList<String> alTemp = new ArrayList<String>();
			for (int i = 1; i < items.length; i++) {
				alTemp.add(items[i].trim());
			}
			items = new String[alTemp.size()];
			alTemp.toArray(items);

		} else if (IBillItem.COMBOTYPE_INDEX_DBFIELD.equals(strComboType) || IBillItem.COMBOTYPE_VALUE_DBFIELD.equals(strComboType)) {
			items = new DataDictionaryReader(items[1], items[2]).getQzsm();
		}

		for (int i = 0; i < items.length; i++) {
			int pos = items[i].indexOf('=');
			String name = pos >= 0 ? items[i].substring(0, pos) : items[i];
			String value = null;
			if (name.indexOf("-0") > 0) {
				try {
					String x_name = "";// TODO: nc.ui.ml.NCLangRes.getInstance().getStrByID(getNodeCode(), name);
					if (x_name != null) {
						name = x_name;
					}
				} catch (Exception e) {
					Logger.info(e.getMessage());
				}
			}
			if (pos >= 0) {
				value = items[i].substring(pos + 1);
			} else if (IBillItem.COMBOTYPE_VALUE.equals(strComboType)){
				value=items[i];
			}else{
				value = "" + i;
			}
			String isSelected = "";
			if (strSelectedValue!=null && (strSelectedValue.equals(value) || strSelectedValue.equals(name))) {
				isSelected = " SELECTED=\"SELECTED\"";
				strShowName = name;
			}
			
			if( buff!=null ){
				buff.append("<option class=\"ufidaComboBox\" style=\"border:0px\" value=\"" + value + "\" " + isSelected + ">" + name + "</option>");
			}
		}
		return strShowName;
	}
	
	public static String makeHtmlDate(String strHtmlID, String strValue, int iWidth, boolean isEdit, int iTabIndex) {
		String strHtmlDivID = strHtmlID + IHtmlConstant.TAIL_DIV;
		String strRefBtnID = strHtmlID + IHtmlConstant.TAIL_BTN;
		//String strHtmlProxyID = strHtmlID + IHtmlContents.TAIL_PROXY;
		StringBuffer buff = new StringBuffer();
		
		if( strValue==null ){
			strValue = "";
		}else if (strValue.length() > 10) {
			strValue = strValue.substring(0, 10);
		}
		buff.append("<div class=\"ufidaRefDiv\" id=\"" + strHtmlDivID + "\" style=\"width:"+iWidth+"px\">");
		buff.append("<input class=\"ufidaInputRef\" name=\"" + strHtmlID + "\" id=\"" + strHtmlID + "\" value=\"" + strValue + "\" ");
		iWidth = iWidth - IHtmlConstant.REF_BTN_WIDTH - 2;
		if (!isEdit) {
			buff.append(" readOnly style=\" background-color: #E7EEF8; width:"+iWidth+"px;\" >");
		} else {
			buff.append(" tabIndex=\"" + iTabIndex + "\" onKeyDown=\"ufidaRefKeyDown(this);\"");
			buff.append(" onchange=\"ufidaInputChange(this);\" ");
			buff.append(" style=\"width:"+iWidth+"px\" onfocus=\"ufidaInputDateFocus('"+strHtmlID+"')\" onblur=\"ufidaInputDateFocusLost('"+strHtmlID+"')\" />");
		}
		buff.append("<img id=\""+strRefBtnID+"\" src=\"" + IHtmlConstant.IMG_DATE + "\"");
		if (isEdit) {
			buff.append(" onclick=\"ufidaShowDate('"+strHtmlID+"');\" style=\"cursor:hand;\" ");
		}
		buff.append("/></div>");
		return buff.toString();
	}
	
	public static String makeHtmlNormalInput(String strHtmlID, String strValue, int iWidth, int iTabIndex,  boolean isEdit, boolean isMoney, boolean isPwd) {

		String strHtmlDivID = strHtmlID + IHtmlConstant.TAIL_DIV;
		strValue = getInputValue(strValue);
		StringBuffer buff = new StringBuffer();
		buff.append("<div class=\"ufidaRefDiv\" id=\"" + strHtmlDivID + "\" style=\"width:"+iWidth+"px\">");
		buff.append("<input class=\"ufidaInputRef\" name=\"" + strHtmlID + "\" id=\"" + strHtmlID + "\" value=\"" + strValue + "\" ");
		iWidth = iWidth - 2;
		if( isPwd ){
			buff.append(" type=\"password\"");
		}else{
			buff.append(" type=\"text\"");
		}
		if (!isEdit) {
			buff.append(" readOnly style=\" background-color: #E7EEF8; width:"+iWidth+"px;\" >");
		} else {
			buff.append(" tabIndex=\"" + iTabIndex + "\" onKeyDown=\"ufidaInputKeyDown(this);\"");
			buff.append(" style=\"width:"+iWidth+"px;");
			if( isMoney ){
				buff.append(" align:right; \" ");
				buff.append(" onchange=\"ufidaInputChange(this);\"");
			}else{
				buff.append(" \" onchange=\"ufidaInputChange(this);\"");
			}
			buff.append(" onfocus=\"ufidaInputFocus('"+strHtmlID+"');\" onblur=\"ufidaInputFocusLost('"+strHtmlID+"');\" />");
		}
		
		buff.append("</div>");

		return buff.toString();
	}
	
	
	public static String makeHtmlRef(String strHtmlID, String strRefType, String strRefPK, UserVO userVO, String pk_corp, int iWidth, boolean isEdit, int iTabIndex) {

		String strHtmlDivID = strHtmlID + IHtmlConstant.TAIL_DIV;
		String strHtmlProxyID = strHtmlID + IHtmlConstant.TAIL_PROXY;
		String strRefBtnID = strHtmlID + IHtmlConstant.TAIL_BTN;
		String strShowValue = "";//RefTool.queryRefShowValue(strRefType, strRefPK, userVO, pk_corp);
		String strRefTypeEncode = EncryptTool.byteToHexString(strRefType.getBytes());

		StringBuffer buff = new StringBuffer();
		buff.append("<div class=\"ufidaRefDiv\" id=\"" + strHtmlDivID + "\" style=\"width:"+iWidth+"px\">");
		buff.append("<input name=\"" + strHtmlID + "\" id=\"" + strHtmlID + "\" type=\"hidden\" value=\"" + getInputValue(strRefPK) + "\">");
		buff.append("<input class=\"ufidaInputRef\" name=\"" + strHtmlProxyID + "\" id=\"" + strHtmlProxyID + "\" value=\"" + strShowValue + "\" ");
		iWidth = iWidth - IHtmlConstant.REF_BTN_WIDTH - 2;
		if (!isEdit) {
			buff.append(" readOnly style=\" background-color: #E7EEF8; width:"+iWidth+"px;\" >");
		} else {
			buff.append(" tabIndex=\"" + iTabIndex + "\" onKeyDown=\"ufidaInputKeyDown(this);\"");
			buff.append(" onchange=\"ufidaInputRefChange(this,'"+strHtmlID+"','" + strRefTypeEncode + "');\" ");
			buff.append(" style=\"width:"+iWidth+"px\"/ onfocus=\"ufidaInputRefFocus('"+strHtmlID+"')\" onblur=\"ufidaInputRefFocusLost('"+strHtmlID+"')\" />");
		}
		buff.append("<img id=\""+strRefBtnID+"\" src=\"" + IHtmlConstant.IMG_REF + "\"");
		if (isEdit) {
			buff.append(" onclick=\"ufidaShowRef('"+strHtmlID+"','" + strRefTypeEncode + "');\" style=\"cursor:hand;\" ");
		}
		buff.append("/></div>");

		return buff.toString();
	}

	public static String cutString(String strValue, int iPixel){
		int iCount = 0;
		StringBuffer buff = new StringBuffer();
		if( (strValue.getBytes().length * ILoginConstant.FONT_SIZE_BYTE_WITH) < iPixel ){
			return strValue;
		}else{
			for (int i = 0; i < strValue.length(); i++) {
				String strWord = strValue.substring(i,i+1);
				iCount += (strWord.getBytes().length * ILoginConstant.FONT_SIZE_BYTE_WITH);
				if( iCount>iPixel ){
					break;
				}else{
					buff.append( strWord );
				}
			}
		}
		return buff.toString();
	}

	public static int getShowWidth(Object objValue, int iMaxPixel) {
		int iRet = 0;
		String strValue = null;
		if (objValue != null) {
			strValue = objValue.toString();
			int iBytes = strValue.getBytes().length;
			iRet = iBytes * ILoginConstant.FONT_SIZE_BYTE_WITH;
			if (iMaxPixel != -1 && iRet > iMaxPixel) {
				iRet = iMaxPixel;
			}
		}
		return iRet;
	}

}
