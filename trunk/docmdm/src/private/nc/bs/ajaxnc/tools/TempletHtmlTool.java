package nc.bs.ajaxnc.tools;

import javax.servlet.http.HttpServletRequest;

import nc.bs.ajaxnc.ncv6.web.TempletView;
import nc.pub.mdm.frame.tool.Toolkit;
import nc.ui.pub.bill.IBillItem;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.sm.UserVO;

public class TempletHtmlTool {
	
	public static void makeTableHead(HttpServletRequest request, TempletView bt, BillTempletBodyVO[] itemVOs, StringBuffer buff, boolean isShowCheck, boolean isShowOperator) throws BusinessException{
		
		String strTableCode = itemVOs[0].getTable_code();
		String strColumnDivID = "Column_"+strTableCode;
		//buff.append("<div id=\""+strColumnDivID+"\" style=\"width:"+iWidth+"px;height:"+iHeight+"px;overflow:hidden\">").append( Toolkit.newLine() );
		buff.append("<tr class=\"ufidaTableHeader\">").append( Toolkit.newLine() );
		
		if( isShowCheck ){
			String checkboxID = strColumnDivID + "_cbx" ; 
			buff.append("<td class=\"ufidaTD20\">");
			buff.append("<input type=\"checkbox\" id=\"" + checkboxID + "\" onclick=\"ufidaSelectAll('" + strTableCode + "')\" />");
			buff.append("</td>").append( Toolkit.newLine() );
		}
		
		for (int i=0; i<itemVOs.length; i++) {
			BillTempletBodyVO itemVO = itemVOs[i];
			String tdID = strColumnDivID+"_td_"+itemVO.getItemkey();
			String strShowValue = itemVO.getDefaultshowname();
			buff.append("<td class=\"ufidaTD80\" id=\""+tdID+"\" ><span class=\"ufidaSPAN80\">"+ strShowValue +"</span></td>").append( Toolkit.newLine() );
		}
		
		if( isShowOperator ){
			String strColID = strColumnDivID +"_td_Operator";
			buff.append("<td id=\""+strColID+"\">&nbsp;</td>").append( Toolkit.newLine() );
		}
		buff.append("</tr>").append( Toolkit.newLine() );
		//buff.append("</div>").append( Toolkit.newLine() );
	}
	
	public static void makeTableBody(HttpServletRequest request, TempletView bt, BillTempletBodyVO[] itemVOs, StringBuffer buff, boolean isShowCheck, boolean isShowOperator, DocVO[] billVOs, boolean isEditCardPanel){

		String strTableCode = itemVOs[0].getTable_code();
		String strScrollDivID = "Scroll_"+strTableCode;
		
		for (int i = 0; (billVOs!=null && i < billVOs.length); i++) {
			buff.append("<tr>").append( Toolkit.newLine() );
			if( isShowCheck ){
				String htmlCbxID = strScrollDivID + "_cbx_" + i ; 
				buff.append("<td style=\"witdh:20px\">");
				buff.append("<input type=\"checkbox\" id=\"" + htmlCbxID + "\" onclick=\"ufidaOnSelectLine(this,'" + strTableCode + "',"+i+")\" />");
				buff.append("</td>").append( Toolkit.newLine() );
			}
			if( isEditCardPanel ){
				makeTableBodyEdit(request, bt, buff, itemVOs, billVOs[i], i);
			}else{
				for (int j = 0; j < itemVOs.length; j++) {
					Object objValue = WebTool.getValueObject(billVOs[i], itemVOs[j].getItemkey());
					String strValue = "";
					if( objValue!=null ){
						strValue = objValue.toString();
					}
					buff.append("<td><span>");
					buff.append( strValue );
					buff.append("</span></td>").append( Toolkit.newLine() );
				}
			}
			
			buff.append("</tr>").append( Toolkit.newLine() );
		}
	}
	
	public static void makeTableBodyEdit(HttpServletRequest request, TempletView bt, StringBuffer buff, BillTempletBodyVO[] itemVOs, DocVO billVO, int iRow){
		itemVOs[0].getCardflag();

		UserVO userVO = RequestTool.getLoginUser( request );
		String pk_corp = RequestTool.getLoginCorpPK(request);

		for (int i = 0; i < itemVOs.length; i++) {
			BillTempletBodyVO itemVO = itemVOs[i];
			int iItemWidth = 150;
			Object objValue = WebTool.getValueObject(billVO, itemVO.getItemkey());
			String strStyle = "";
			if( objValue instanceof String){
			}else{
				strStyle = " style=\"align:right\"";
			}
			buff.append("<td witdh=\""+iItemWidth+"px\""+strStyle+">");
			int iColunm = i;
			int iTabIndex = (iRow+1)*(iColunm+1);
			String strHtmlID = itemVO.getTable_code()+"_" + itemVO.getItemkey() + "_" + iRow + "_" + iColunm;
			int iDataType = itemVO.getDatatype().intValue();
			
			String strTempHTML = "";
			boolean isEdit = TempletViewTool.isEdit(true, itemVO);
			if (iDataType == IBillItem.UFREF) {
				strTempHTML = HtmlTool.makeHtmlRef(strHtmlID, itemVO.getReftype(), objValue.toString(), userVO, pk_corp, iItemWidth, isEdit, iTabIndex);
				
			}else if( iDataType == IBillItem.COMBO ){
				strTempHTML = HtmlTool.makeHtmlComboBox(strHtmlID, itemVO.getReftype(), objValue.toString(), iItemWidth, isEdit, iTabIndex);
			}else if( iDataType == IBillItem.DATE ){
				strTempHTML = HtmlTool.makeHtmlDate(strHtmlID, objValue.toString(), iItemWidth, isEdit, iTabIndex);
			}else{
				boolean isPwd = (iDataType == IBillItem.PASSWORDFIELD);
				boolean isMoney = (iDataType == IBillItem.DECIMAL || iDataType == IBillItem.INTEGER);
				strTempHTML = HtmlTool.makeHtmlNormalInput( strHtmlID, objValue.toString(), iItemWidth, iTabIndex, isEdit, isMoney, isPwd);
			}
			
			buff.append( strTempHTML );
			buff.append("</td>").append( Toolkit.newLine() );
		}
	
	}
}
