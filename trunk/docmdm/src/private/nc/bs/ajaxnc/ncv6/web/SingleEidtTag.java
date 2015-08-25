package nc.bs.ajaxnc.ncv6.web;

import javax.servlet.http.HttpServletRequest;

import nc.bs.ajaxnc.base.BaseTag;
import nc.bs.ajaxnc.constant.ILoginConstant;
import nc.bs.ajaxnc.tools.RequestTool;
import nc.bs.ajaxnc.tools.TempletHtmlTool;
import nc.bs.ajaxnc.tools.TempletViewTool;
import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.frame.tool.Toolkit;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;

public class SingleEidtTag extends BaseTag {

	private static final long serialVersionUID = 4305132325956411090L;

	@Override
	public void makeHTML(HttpServletRequest request) {
		TempletView tv = TempletViewTool.findTempletView(request);

		StringBuffer buff = getHtmlBuffer();
		buff.setLength(0);

		BillTabVO[] tabVOs = tv.getTempletTabVOs();
		BillTabVO headTabVO = tabVOs[0];
		BillTempletBodyVO[] itemVOs = tv.getItemsInTab(headTabVO);
		
		String strWidth = RequestTool.getWebWidth(request);
		int iWidth = Integer.parseInt(strWidth);
		
		try {
			buff.append( Toolkit.newLine() );
			buff.append("<table class=\"listTable\" style=\"width:"+iWidth+"px;\">").append( Toolkit.newLine() );
			TempletHtmlTool.makeTableHead(getRequest(), tv, itemVOs, buff, true, true);

			DocVO[] billVOs = (DocVO[]) getRequest().getAttribute(ILoginConstant.ATTR_LIST_WEBVOS);
			TempletHtmlTool.makeTableBody(getRequest(), tv, itemVOs, buff, true, true, billVOs, false);
			
			buff.append("</table>").append( Toolkit.newLine() );
		} catch (BusinessException e) {
			LogTool.error(e);
		}
	}
	
}
