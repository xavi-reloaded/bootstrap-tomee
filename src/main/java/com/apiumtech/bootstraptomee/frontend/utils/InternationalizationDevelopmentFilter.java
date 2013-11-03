package com.apiumtech.bootstraptomee.frontend.utils;

import com.apiumtech.bootstraptomee.backend.dao.UserSettingsDAO;
import com.apiumtech.bootstraptomee.backend.model.UserSettings;
import com.apiumtech.bootstraptomee.backend.services.ApplicationPropertiesService;
import com.apiumtech.bootstraptomee.frontend.CommaFeedSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replace variables from templates on the fly in dev mode only. In production the substitution is done at build-time.
 * 
 */
@Slf4j
public class InternationalizationDevelopmentFilter implements Filter {

	@Inject
	UserSettingsDAO userSettingsDAO;

	private boolean production = true;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ApplicationPropertiesService properties = ApplicationPropertiesService.get();
		production = properties.isProduction();
	}

	@Override
	public void destroy() {
		// do nothing
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if (production) {
			chain.doFilter(request, response);
			return;
		}

		final ServletOutputStream wrapper = new ServletOutputStreamWrapper();
		ServletResponse interceptor = new HttpServletResponseWrapper((HttpServletResponse) response) {

			@Override
			public ServletOutputStream getOutputStream() throws IOException {
				return wrapper;
			}
		};
		chain.doFilter(request, interceptor);

		UserSettings settings = userSettingsDAO.findByUser(CommaFeedSession.get().getUser());
		String lang = (settings == null || settings.getLanguage() == null) ? "en" : settings.getLanguage();

		byte[] bytes = translate(wrapper.toString(), lang).getBytes("UTF-8");
		response.setContentLength(bytes.length);
		response.setCharacterEncoding("UTF-8");
		response.getOutputStream().write(bytes);
		response.getOutputStream().close();

	}

	private String translate(String content, String lang) {
		Properties props = new Properties();
		InputStream is = null;
		try {
			is = getClass().getResourceAsStream("/i18n/" + lang + ".properties");
			props.load(new InputStreamReader(is, "UTF-8"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(is);
		}

		return replace(content, props);
	}

	private static final Pattern PATTERN = Pattern.compile("\\$\\{(.+?)\\}");

	public String replace(String content, Properties props) {
		Matcher m = PATTERN.matcher(content);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String var = m.group(1);
			Object replacement = props.get(var);
			String replacementValue = replacement == null ? var : replacement.toString().split("#")[0];
			m.appendReplacement(sb, replacementValue);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private static class ServletOutputStreamWrapper extends ServletOutputStream {

		private ByteArrayOutputStream baos = new ByteArrayOutputStream();

		@Override
		public void write(int b) throws IOException {
			baos.write(b);
		}

		@Override
		public String toString() {
			return baos.toString();
		}
	}

}
