package com.apiumtech.bootstraptomee.frontend.pages.components;

import com.apiumtech.bootstraptomee.backend.dao.UserDAO;
import com.apiumtech.bootstraptomee.backend.model.User;
import com.apiumtech.bootstraptomee.backend.model.UserRole.Role;
import com.apiumtech.bootstraptomee.backend.services.ApplicationSettingsService;
import com.apiumtech.bootstraptomee.backend.services.UserService;
import com.apiumtech.bootstraptomee.frontend.CommaFeedSession;
import com.apiumtech.bootstraptomee.frontend.model.request.RegistrationRequest;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.StringValidator;

import javax.inject.Inject;
import java.util.Arrays;

@SuppressWarnings("serial")
public class RegisterPanel extends Panel {

	@Inject
	UserDAO userDAO;

	@Inject
	UserService userService;

	@Inject
	ApplicationSettingsService applicationSettingsService;

	public RegisterPanel(String markupId) {
		super(markupId);

		IModel<RegistrationRequest> model = Model.of(new RegistrationRequest());

		Form<RegistrationRequest> form = new StatelessForm<RegistrationRequest>("form", model) {
			@Override
			protected void onSubmit() {
				if (applicationSettingsService.get().isAllowRegistrations()) {
					RegistrationRequest req = getModelObject();
					userService.register(req.getName(), req.getPassword(), req.getEmail(), Arrays.asList(Role.USER));

					IAuthenticationStrategy strategy = getApplication().getSecuritySettings().getAuthenticationStrategy();
					strategy.save(req.getName(), req.getPassword());
					CommaFeedSession.get().signIn(req.getName(), req.getPassword());
				}
				setResponsePage(getApplication().getHomePage());
			}
		};
		add(form);
		add(new BootstrapFeedbackPanel("feedback", new ContainerFeedbackMessageFilter(form)));

		form.add(new RequiredTextField<String>("name", new PropertyModel<String>(model, "name")).add(StringValidator.lengthBetween(3, 32))
				.add(new IValidator<String>() {
					@Override
					public void validate(IValidatable<String> validatable) {
						String name = validatable.getValue();
						User user = userDAO.findByName(name);
						if (user != null) {
							validatable.error(new ValidationError("Name is already taken."));
						}
					}
				}));
		form.add(new PasswordTextField("password", new PropertyModel<String>(model, "password")).setResetPassword(false).add(
				StringValidator.minimumLength(6)));
		form.add(new RequiredTextField<String>("email", new PropertyModel<String>(model, "email")) {
			@Override
			protected String getInputType() {
				return "email";
			}
		}.add(RfcCompliantEmailAddressValidator.getInstance()).add(new IValidator<String>() {
			@Override
			public void validate(IValidatable<String> validatable) {
				String email = validatable.getValue();
				User user = userDAO.findByEmail(email);
				if (user != null) {
					validatable.error(new ValidationError("Email is already taken."));
				}
			}
		}));
	}
}
