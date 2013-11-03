package com.apiumtech.bootstraptomee.frontend.rest.resources;

import com.codahale.metrics.MetricRegistry;
import com.apiumtech.bootstraptomee.backend.dao.UserDAO;
import com.apiumtech.bootstraptomee.backend.model.User;
import com.apiumtech.bootstraptomee.backend.model.UserRole.Role;
import com.apiumtech.bootstraptomee.frontend.CommaFeedApplication;
import com.apiumtech.bootstraptomee.frontend.CommaFeedSession;
import com.apiumtech.bootstraptomee.frontend.SecurityCheck;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.crypt.Base64;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.lang.reflect.Method;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityCheck(Role.USER)
public abstract class AbstractREST {

	@Context
	private HttpServletRequest request;

	@Context
	private HttpServletResponse response;

	@Inject
	MetricRegistry metrics;

	@Inject
	private UserDAO userDAO;

	@PostConstruct
	public void init() {
		CommaFeedApplication app = CommaFeedApplication.get();
		ServletWebRequest swreq = new ServletWebRequest(request, "");
		ServletWebResponse swresp = new ServletWebResponse(swreq, response);
		RequestCycle cycle = app.createRequestCycle(swreq, swresp);
		ThreadContext.setRequestCycle(cycle);
		CommaFeedSession session = (CommaFeedSession) app.fetchCreateAndSetSession(cycle);

		if (session.getUser() == null) {
			cookieLogin(app, session);
		}
		if (session.getUser() == null) {
			basicHttpLogin(swreq, session);
		}
	}

	private void cookieLogin(CommaFeedApplication app, CommaFeedSession session) {
		IAuthenticationStrategy authenticationStrategy = app.getSecuritySettings().getAuthenticationStrategy();
		String[] data = authenticationStrategy.load();
		if (data != null && data.length > 1) {
			session.signIn(data[0], data[1]);
		}
	}

	private void basicHttpLogin(ServletWebRequest req, CommaFeedSession session) {
		String value = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (value != null && value.startsWith("Basic ")) {
			value = value.substring(6);
			String decoded = new String(Base64.decodeBase64(value));
			String[] data = decoded.split(":");
			if (data != null && data.length > 1) {
				session.signIn(data[0], data[1]);
			}
		}
	}

	private void apiKeyLogin() {
		String apiKey = request.getParameter("apiKey");
		User user = userDAO.findByApiKey(apiKey);
		CommaFeedSession.get().setUser(user);
	}

	protected User getUser() {
		return CommaFeedSession.get().getUser();
	}

	@AroundInvoke
	public Object intercept(InvocationContext context) throws Exception {
		Method method = context.getMethod();

		// check security
		boolean allowed = true;
		User user = null;

		SecurityCheck check = method.isAnnotationPresent(SecurityCheck.class) ? method.getAnnotation(SecurityCheck.class) : method
				.getDeclaringClass().getAnnotation(SecurityCheck.class);

		if (check != null) {
			user = getUser();
			if (user == null && check.apiKeyAllowed()) {
				apiKeyLogin();
				user = getUser();
			}

			allowed = checkRole(check.value());
		}
		if (!allowed) {
			if (user == null) {
				return Response.status(Status.UNAUTHORIZED).entity("You are not authorized to do this.").build();
			} else {
				return Response.status(Status.FORBIDDEN).entity("You are not authorized to do this.").build();
			}

		}

		Object result = null;
		com.codahale.metrics.Timer.Context timer = metrics.timer(
				MetricRegistry.name(method.getDeclaringClass(), method.getName(), "responseTime")).time();
		try {
			result = context.proceed();
		} finally {
			timer.stop();
		}

		return result;
	}

	private boolean checkRole(Role requiredRole) {
		if (requiredRole == Role.NONE) {
			return true;
		}

		Roles roles = CommaFeedSession.get().getRoles();
		boolean authorized = roles.hasAnyRole(new Roles(requiredRole.name()));
		return authorized;
	}
}
