package nc.bs.ajaxnc.base;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.ajaxnc.constant.ILoginConstant;
import nc.bs.ajaxnc.tools.RequestTool;
import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.frame.tool.Toolkit;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public abstract class BaseCtrl extends AbstractController {

	public String ErrorJsp = "/" + ILoginConstant.ERROR_JSP_500;

	/**
	 * See Spring <property name="prefix" value="/WEB-INF/views" /> <br>
	 * return "/bill/normal/";<br>
	 * /WEB-INF/views/bill/normal/
	 * @return
	 */
	public abstract String getViewDir();

	public boolean hasLogin(HttpServletRequest request){
		return RequestTool.getLoginUser(request)!=null;
	}
	
	public String forwardJSP(HttpServletRequest request, HttpServletResponse response) throws Exception{
		return null;
	}
	
	protected String completeJspURL(String strJsp){
		if( strJsp!=null && getViewDir()!=null && !strJsp.contains(getViewDir()) ){
			strJsp = getViewDir()+strJsp;
		}
		return strJsp;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String strJSP = null;
		try {
			String strMethod = request.getParameter("method");
			if (strMethod == null ) {
				strMethod = (String) request.getAttribute("method");
				if (strMethod == null ) {
					strMethod = getDefaultMethod();
				}
			}

			Class[] parameterTypes = new Class[]{HttpServletRequest.class, HttpServletResponse.class};
			Method m = null;
			try{
				m = this.getClass().getMethod(strMethod, parameterTypes);
			}catch(Exception e){
				LogTool.error( "Can not find " + this.getClass().getName() + "." + strMethod + "(HttpServletRequest request, HttpServletResponse response)" );
			}
			
			if( m!=null ){
				Object objRet = m.invoke(this, new Object[]{ request, response});
				strJSP = (objRet!=null?objRet.toString():null);
			}else{
				strJSP = forwardJSP(request, response);
			}
			
			if ( !Toolkit.isNull(strJSP) ) {
				strJSP = completeJspURL( strJSP );
			}

		} catch (Exception be) {
			LogTool.error(be);
			request.setAttribute(ILoginConstant.ERROR_KEY, be);
			strJSP = ErrorJsp;
		}
		
		String stBreakJSP = (String) request.getAttribute(ILoginConstant.BREAK_JSP);
		if (stBreakJSP != null && stBreakJSP.length() > 0) {
			strJSP = stBreakJSP;
		}
		
		if( Toolkit.isNull(strJSP) ) {
			return null;
		}else {
			return new ModelAndView(strJSP);
		}
	}
	
	protected String getDefaultMethod() {
		return "onList";
	}

	protected void writeByteFile(HttpServletRequest request, HttpServletResponse response, String strFileName, byte[] fileContent) throws Exception {

		strFileName = Toolkit.makeDownloadFileName(strFileName, 140, "UTF-8");

		response.setContentType("application/octet-stream;charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment;filename=" + strFileName);

		GZIPInputStream gzin = new GZIPInputStream(new ByteArrayInputStream(fileContent));
		BufferedOutputStream bufOut = new BufferedOutputStream(response.getOutputStream());
		byte[] buf = new byte[1024];
		int num;
		while ((num = gzin.read(buf)) != -1) {
			bufOut.write(buf, 0, num);
		}

		bufOut.flush();
		bufOut.close();
	}

	protected void writeHTML(HttpServletRequest request, HttpServletResponse response, String strHTML) throws Exception {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		BufferedWriter out = new BufferedWriter(response.getWriter());

		String strBeforeString = (String) request.getAttribute("beforeString");
		if (strBeforeString != null) {
			out.write(strBeforeString);
		}
		out.write(strHTML);
		out.flush();
		out.close();
	}

	/**
	 * @param response
	 * @param strXML
	 * @throws Exception
	 */
	protected void writeXML(HttpServletResponse response, String strXML) throws Exception {
		response.setContentType("application/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(strXML);
		response.getWriter().flush();
	}

	protected void writeXMLFile(HttpServletRequest request, HttpServletResponse response, String strFullPath) throws Exception {
		String strFilePath = request.getSession().getServletContext().getRealPath(strFullPath);
		response.setContentType("application/xml");
		response.setCharacterEncoding("UTF-8");
		BufferedWriter out = new BufferedWriter(response.getWriter());
		BufferedReader br = new BufferedReader(new FileReader(strFilePath));

		String strLine = br.readLine();
		while (strLine != null) {
			out.write(strLine);
			out.newLine();
			strLine = br.readLine();
		}

		out.flush();
		br.close();
	}

}
