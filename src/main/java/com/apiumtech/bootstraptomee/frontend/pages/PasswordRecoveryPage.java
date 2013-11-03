package com.apiumtech.bootstraptomee.frontend.pages;

import com.apiumtech.bootstraptomee.backend.model.User;
import com.apiumtech.bootstraptomee.backend.utils.Utils;
import com.apiumtech.bootstraptomee.frontend.pages.components.BootstrapFeedbackPanel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Date;
import java.util.UUID;

@SuppressWarnings("serial")
@Slf4j
public class PasswordRecoveryPage extends BasePage {

	public PasswordRecoveryPage() {

		IModel<String> email = new Model<String>();
		add(new BootstrapFeedbackPanel("feedback"));
		Form<String> form = new Form<String>("form", email) {
			@Override
			protected void onSubmit() {
				super.onSubmit();
				User user = userDAO.findByEmail(getModelObject());
				if (user == null) {
					error("Email not found.");
				} else {
					try {
						user.setRecoverPasswordToken(DigestUtils.sha1Hex(UUID.randomUUID().toString()));
						user.setRecoverPasswordTokenDate(new Date());
						userDAO.saveOrUpdate(user);
						mailService.sendMail(user, "Password recovery", buildEmailContent(user));
						info("Email sent.");
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						error("Cannot send email, please contact the staff.");
					}

				}
			}

		};
		add(form);

		form.add(new RequiredTextField<String>("email", email) {
			@Override
			protected String getInputType() {
				return "email";
			}
		}.add(RfcCompliantEmailAddressValidator.getInstance()));

		form.add(new BookmarkablePageLink<Void>("cancel", HomePage.class));
	}

	private String buildEmailContent(User user) throws Exception {

		String publicUrl = Utils.removeTrailingSlash(applicationSettingsService.get().getPublicUrl());
		publicUrl += "/recover2";

		return String
				.format("You asked for password recovery for account '%s', <a href='%s'>follow this link</a> to change your password. Ignore this if you didn't request a password recovery.",
						user.getName(), callbackUrl(user, publicUrl));
	}

	private String callbackUrl(User user, String publicUrl) throws Exception {
		return new URIBuilder(publicUrl).addParameter(PasswordRecoveryCallbackPage.PARAM_EMAIL, user.getEmail())
				.addParameter(PasswordRecoveryCallbackPage.PARAM_TOKEN, user.getRecoverPasswordToken()).build().toURL().toString();
	}
}
