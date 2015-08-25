package nc.bs.ajaxnc.ncv6;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.ajaxnc.constant.ILoginConstant;
import nc.bs.ajaxnc.tools.RequestTool;
import nc.vo.sm.UserVO;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class DefaultInterceptor implements HandlerInterceptor  {

	@Override
	public void afterCompletion(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse, Object obj, Exception exception) throws Exception {
	}

	@Override
	public void postHandle(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse, Object obj, ModelAndView modelandview) throws Exception {
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		request.setCharacterEncoding("UTF-8");
		boolean isContinue = false;

		request.setAttribute(ILoginConstant.ATTR_HANDLER, handler);

		if (handler instanceof DefaultLoginCtrl ) {
			isContinue = true;
			
		}else if( handler instanceof DefaultReferenceCtrl){
			isContinue = true;
			
		} else {
			UserVO user = RequestTool.getLoginUser(request);
			if( user==null ){
				String loginJsp = "/login.go";
				request.getRequestDispatcher(loginJsp).forward(request, response);
			}else{
				isContinue = true;
			}
		}

		return isContinue;
	}

}
