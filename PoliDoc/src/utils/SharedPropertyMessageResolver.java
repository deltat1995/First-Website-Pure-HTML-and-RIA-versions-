package utils;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.ServletContextTemplateResource;

/**
 * 
 * The MultiPathMessageResolver class extends StandardMessageResolver in order
 * to customize the place where we can search for the property files for
 * customization by overriding the method resolveMessagesForTemplate. You need
 * to set this MessageResolver for your template engine as follows:
 * this.templateEngine.setMessageResolver(new
 * SharedPropertyMessageResolver(servletContext, "NAME OF YOUR FOLDER INSIDE WEB-INF", "BASENAME OF YOUR PROPERTY FILE"));
 * 
 * @author Samuele Pasini
 * @author Francesco Azzoni
 *
 */

public class SharedPropertyMessageResolver extends StandardMessageResolver {

	private ServletContext context;
	private String directory;
	private String fileName;

	public SharedPropertyMessageResolver(ServletContext context, String path, String fileName) {
		super();
		this.context = context;
		this.directory = path;
		this.fileName = fileName;
	}

	@Override
	protected Map<String, String> resolveMessagesForTemplate(String template, ITemplateResource templateResource,
			Locale locale) {

		String finalpath = "/WEB-INF/" + directory + "/" + fileName + ".html";
		templateResource = new ServletContextTemplateResource(context, finalpath, "ISO-8859-1");
		return super.resolveMessagesForTemplate(finalpath, templateResource, locale);
	}

}
