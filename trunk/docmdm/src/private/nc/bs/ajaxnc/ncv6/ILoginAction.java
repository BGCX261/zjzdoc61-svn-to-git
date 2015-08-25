package nc.bs.ajaxnc.ncv6;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ILoginAction {
	public boolean doLogin(HttpServletRequest request, HttpServletResponse response);

}
