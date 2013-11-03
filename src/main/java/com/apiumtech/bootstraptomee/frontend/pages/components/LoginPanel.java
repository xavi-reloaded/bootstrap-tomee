package com.apiumtech.bootstraptomee.frontend.pages.components;

import com.apiumtech.bootstraptomee.backend.services.ApplicationSettingsService;
import com.apiumtech.bootstraptomee.frontend.pages.PasswordRecoveryPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import javax.inject.Inject;

@SuppressWarnings("serial")
public class LoginPanel extends SignInPanel {

	@Inject
	ApplicationSettingsService applicationSettingsService;

	public LoginPanel(String id) {
		super(id);
		replace(new BootstrapFeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this)));
		Form<?> form = (Form<?>) get("signInForm");
		form.add(new BookmarkablePageLink<Void>("recover", PasswordRecoveryPage.class) {
			@Override
			protected void onConfigure() {
				super.onConfigure();
				String smtpHost = applicationSettingsService.get().getSmtpHost();
				setVisibilityAllowed(StringUtils.isNotBlank(smtpHost));
			}
		});
	}

}
