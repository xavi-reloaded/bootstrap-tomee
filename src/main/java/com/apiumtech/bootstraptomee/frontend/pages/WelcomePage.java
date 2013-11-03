package com.apiumtech.bootstraptomee.frontend.pages;

import com.apiumtech.bootstraptomee.backend.services.ApplicationSettingsService;
import com.apiumtech.bootstraptomee.frontend.pages.components.LoginPanel;
import com.apiumtech.bootstraptomee.frontend.pages.components.RegisterPanel;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import javax.inject.Inject;

@SuppressWarnings("serial")
public class WelcomePage extends BasePage {

	@Inject
	ApplicationSettingsService applicationSettingsService;

	public WelcomePage() {
		add(new BookmarkablePageLink<Void>("logo-link", getApplication().getHomePage()));
		add(new BookmarkablePageLink<Void>("demo-login", DemoLoginPage.class));
		add(new LoginPanel("login"));
		add(new RegisterPanel("register") {
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisibilityAllowed(applicationSettingsService.get().isAllowRegistrations());
			}
		});
	}
}
