package com.apiumtech.bootstraptomee.frontend.pages;

import com.apiumtech.bootstraptomee.backend.services.UserService;
import com.apiumtech.bootstraptomee.backend.startup.StartupBean;
import com.apiumtech.bootstraptomee.frontend.CommaFeedSession;
import org.apache.wicket.markup.html.WebPage;

import javax.inject.Inject;

public class DemoLoginPage extends WebPage {

	private static final long serialVersionUID = 1L;

	@Inject
	UserService userService;

	public DemoLoginPage() {
		CommaFeedSession.get().authenticate(StartupBean.USERNAME_DEMO, StartupBean.USERNAME_DEMO);
		setResponsePage(getApplication().getHomePage());
	}
}
