package filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UrlFilter implements Filter{
	public UrlFilter() {}

	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		if (checkUrl(req.getServletPath())) {
			// pass the request along the filter chain
			((HttpServletResponse) response).setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
			chain.doFilter(request, response);
		}
		else {
			String indexPath = req.getServletContext().getContextPath()+ "/index";
			System.out.println("Controllo del UrlChecker!! Link : "+req.getServletPath());
			res.sendRedirect(indexPath);
		}
	}
	
	
	private boolean checkUrl(String url) {
		ArrayList<String> patterns = new ArrayList<>(Arrays.asList("/fonth1.TTF","/style.css","/Dir","/Doc","/getDocInfo","/getListDocs","/index","/moveDoc"));
		for(String pat : patterns) {
			if (url.startsWith(pat)) return true;
		}
		return false;
	}

	public void init(FilterConfig fConfig) throws ServletException {}
}
