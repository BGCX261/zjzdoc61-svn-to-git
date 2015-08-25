package nc.bs.ajaxnc.base;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import nc.bs.ajaxnc.tools.RequestTool;

public abstract class BaseTag  extends TagSupport {

	private static final long serialVersionUID = 32287526908314873L;

	private StringBuffer buff = new StringBuffer();
	
	protected StringBuffer getHtmlBuffer(){
		return buff;
	}
	
	protected String getBillTypeCode(){
		return RequestTool.getTempletCode( getRequest() );
	}
	
	/*
	 * 子类理应在此方法之前将所有 html内容都拼装完毕(子类实现的makeHTML()方法)，并注入htmlBuffer中
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {
			out.write(getHtmlBuffer().toString());
		} catch (IOException e) {
			e.printStackTrace();
			return SKIP_BODY;
		}
		getHtmlBuffer().setLength(0);
		return EVAL_BODY_AGAIN;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	public int doStartTag() throws JspException {
		makeHTML( getRequest() );
		return EVAL_BODY_INCLUDE;
	}
	
	public HttpServletRequest getRequest(){
		ServletRequest request = pageContext.getRequest();
		if( request instanceof HttpServletRequest ){
			return (HttpServletRequest)request;
		}
		return null;
	}
	
	/**
	 * 子类应该覆盖此方法
	 */
	public abstract void makeHTML(HttpServletRequest request);

}
