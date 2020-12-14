package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class loginChecker implements Filter {
	
	public loginChecker() {}

	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginpath = req.getServletContext().getContextPath()+ "/login";
		System.out.println("Controllo del loginChecker!! Link : "+req.getServletPath());

		HttpSession session = req.getSession(true);
		if (session.isNew() || session.getAttribute("username") == null) {
			res.sendRedirect(loginpath);
			return;
		}
		if (req.getServletPath().equals("/login.html") && session.getAttribute("username") != null) {
			String homePath = req.getServletContext().getContextPath()+ "/homepage.html";
			res.sendRedirect(homePath);
			return;
		}
		// pass the request along the filter chain
		((HttpServletResponse) response).setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		chain.doFilter(request, response);
	}


	public void init(FilterConfig fConfig) throws ServletException {}


}
