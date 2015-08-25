package nc.bs.mdm.ws;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.md.model.IColumn;
import nc.md.model.IForeignKey;
import nc.md.model.ITable;
import nc.mddb.model.impl.Column;
import nc.mddb.model.impl.ForeignKey;
import nc.pub.mdm.frame.BaseService;
import nc.pub.mdm.frame.tool.CacheTool;
import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.proxy.BaseUserObject;
import nc.pub.mdm.ws.IDocMdmService;
import nc.vo.mdm.frame.DocAggVO;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultAttribute;

public class DocMdmServiceImpl implements IDocMdmService {

	@Override
	public String query(String params) {
		// �����������ƴ��document
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(params);
		} catch (DocumentException e) {
			LogTool.error(e);
		}
		// ����document����ȡ������Ϣ
		Element root = doc.getRootElement();
		String syscode = root.attribute("syscode").getText();
		String pwd = root.attribute("pswd").getText();
		String doctype = root.attribute("doctype").getText();
		
		String vcode =root.attribute("vcode")!=null? root.attribute("vcode").getText():null;
		String vname =root.attribute("vname")!=null? root.attribute("vname").getText():null;
		
		String parent =root.attribute("parentcode")!=null? root.attribute("parentcode").getText():null;
		// ��֤��ϵͳ����
		boolean isPass = false;

		Map<String, Map<String, DocVO>> map1 = CacheTool.initSysCacheDoc("docmdm_sys_extsys", "vcode", syscode);
		Map<String, DocVO> map2 = map1.get("docmdm_sys_extsys");
		DocVO extSys = map2.get(syscode);
		if (extSys.getAttributeValue("vpassword").equals(pwd)) {
			isPass = true;
		}

		if (!isPass) {
			return "�û���������֤��ͨ������";
		}
		// ��֤ͨ����
		// ��ѯ������Ӧ�ı���
		String tabName = null;
		map1 = CacheTool.initSysCacheDoc("docmdm_sys_doctype", "vcode", doctype);
		map2 = map1.get("docmdm_sys_doctype");
		DocVO mdmtype = map2.get(doctype);

		tabName = mdmtype.getAttributeValue("metatab") == null ? null : (String) mdmtype.getAttributeValue("metatab");

		if (tabName == null || "".equals(tabName)) {
			return "û��" + doctype + "�����͵������ݣ���";
		}
		// ��ѯ����
		String whereStr="";
		if(vcode!=null)
			whereStr+=tabName+".vcode='"+vcode+"' ";
		if(vname!=null){
			if(whereStr!=null&&!"".equals(whereStr))
				whereStr+=" and ";
			whereStr+=tabName+".vname='"+vname+"' ";
		}
		DocVO[] rlt=null;
		try {
			if(parent!=null&&!"".equals(parent)){
				rlt = BaseService.queryMainDataByParent(tabName,parent);
			}else {
				rlt = BaseService.queryMainDataByWhere(tabName,whereStr);
			}
		} catch (BusinessException e1) {
			LogTool.error(e1);
		}
		if (rlt != null && rlt.length > 0) {
			// ��������ƴ��document
			doc = DocumentHelper.createDocument();
			root=doc.addElement("docvos");
			root.addAttribute("doctype", doctype);
			for (int i = 0; i < rlt.length; i++) {
				Element e = root.addElement("docvo");
				for (String key : rlt[i].getAttributeNames()) {
					Object vlu = rlt[i].getAttributeValue(key);
					e.addAttribute(key, vlu == null ? "" : vlu.toString());
				}
			}
			// ����ѯ�����������ַ�����ʽ���أ���Ӧ��ѯ����
			String strRet = doc.asXML();
			XMLWriter xw = null; 
			//��ʽ�� 
			try {
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setEncoding("UTF-8");
				StringWriter sw = new StringWriter();
				xw = new XMLWriter(sw, format);
				xw.setEscapeText(false);
				xw.write(doc);
				strRet = sw.toString();
				xw.flush();
			} catch (IOException e) {
				LogTool.error(e);
			} finally {
				if (xw != null) {
					try {
						xw.close();
					} catch (IOException e) {
					}
				}
			}
			return strRet;
		}

		return null;
	}

	@Override
	public String save(String params) {

		// �����������ƴ��document
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(params);
		} catch (DocumentException e) {
			LogTool.error(e);
		}
		// ����document����ȡ������Ϣ
		Element root = doc.getRootElement();
		String syscode = root.attribute("syscode").getText();
		String pwd = root.attribute("pswd").getText();
		String doctype = root.attribute("doctype").getText();

		// ��֤��ϵͳ����
		boolean isPass = false;
		Map<String, Map<String, DocVO>> map1 = CacheTool.initSysCacheDoc("docmdm_sys_extsys", "vcode", syscode);
		Map<String, DocVO> map2 = map1.get("docmdm_sys_extsys");
		DocVO extSys = map2.get(syscode);
		if (extSys.getAttributeValue("vpassword").equals(pwd)) {
			isPass = true;
		}

		if (!isPass) {
			return "�û���������֤��ͨ������";
		}
		// ��֤ͨ����

		// ��ѯ������Ӧ�ĵ������

		map1 = CacheTool.initSysCacheDoc("docmdm_sys_doctype", "vcode", doctype);
		map2 = map1.get("docmdm_sys_doctype");
		DocVO mdmtype = map2.get(doctype);

		String tabName = mdmtype.getAttributeValue("metatab").toString();

		map1 = CacheTool.initSysCacheDoc("docmdm_sys_imp", "vtable", tabName);
		map2 = map1.get("docmdm_sys_imp");
		DocVO sysimp = map2.get(tabName);

		// ��ȡDom,ƴ�ӳ�DocVO[]
		ArrayList<DocVO> docList = new ArrayList<DocVO>();
		Iterator it = root.element("bddocs").iterator();
		DocVO docvo = null;
		while (it.hasNext()) {
			Element e = (Element) it.next();
			docvo = new DocVO();
			Iterator it2 = e.attributeIterator();
			while (it2.hasNext()) {
				DefaultAttribute att = (DefaultAttribute) it2.next();
				docvo.setAttributeValue(att.getName(), att.getValue());
			}
			docvo.setTableCode(tabName);
			docvo.setPrimaryKeyField((String) sysimp.getAttributeValue("vpk"));
			docvo.setParentKeyField((String) sysimp.getAttributeValue("vpkparent"));
			docList.add(docvo);
		}

		// ƴ�ɿɵ�����������ͣ�ֱ�ӵ��õ���Ĺ���
		sysimp.setAttributeValue(DocVO.KEY_IMPORT_VOS, docList.toArray(new DocVO[0]));
		sysimp.setStatus(VOStatus.UPDATED);

		DocAggVO aggVO = new DocAggVO();
		aggVO.setParentVO(sysimp);
		BaseUserObject userObj = new BaseUserObject();
		try {
			AggregatedValueObject aggRet = BaseService.getService().saveBD(aggVO, userObj);
		} catch (BusinessException e) {
			LogTool.error(e);
			return e.getMessage();
		}
		return "ok!";
	}

}
