package com.apiumtech.bootstraptomee.frontend.utils;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.template.PackageTextTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class WicketUtils {

	public static void loadJS(IHeaderResponse response, Class<?> klass, String fileName) {
		HeaderItem result = JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(klass, fileName + ".js"));
		response.render(result);
	}

	public static void loadJS(IHeaderResponse response, Class<?> klass, String fileName, Map<String, ? extends Object> variables) {
		HeaderItem result = null;
		PackageTextTemplate template = null;
		try {
			template = new PackageTextTemplate(klass, fileName + ".js");
			String script = template.asString(variables);
			result = JavaScriptHeaderItem.forScript(script, null);
		} finally {
			IOUtils.closeQuietly(template);
		}
		response.render(result);
	}

	public static HttpServletRequest getHttpServletRequest() {
		ServletWebRequest servletWebRequest = (ServletWebRequest) RequestCycle.get().getRequest();
		return servletWebRequest.getContainerRequest();
	}

	public static HttpServletResponse getHttpServletResponse() {
		WebResponse webResponse = (WebResponse) RequestCycle.get().getResponse();
		return (HttpServletResponse) webResponse.getContainerResponse();
	}

	/**
	 * like wicket's Request.getClientUrl() but returns an absolute url instead of a relative one
	 */
	public static String getClientFullUrl() {
		HttpServletRequest request = getHttpServletRequest();
		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();

		if (queryString == null) {
			return requestURL.toString();
		} else {
			return requestURL.append('?').append(queryString).toString();
		}
	}
}
