package com.apiumtech.bootstraptomee.frontend.pages;

import com.apiumtech.bootstraptomee.backend.model.User;
import com.apiumtech.bootstraptomee.backend.model.UserRole.Role;
import com.apiumtech.bootstraptomee.backend.model.UserSettings;
import com.apiumtech.bootstraptomee.frontend.CommaFeedSession;
import com.apiumtech.bootstraptomee.frontend.SecurityCheck;
import com.apiumtech.bootstraptomee.frontend.resources.UserCustomCssReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

@SuppressWarnings("serial")
@SecurityCheck(Role.USER)
public class HomePage extends BasePage {

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(CssHeaderItem.forReference(new UserCustomCssReference() {
			@Override
			protected String getCss() {
				User user = CommaFeedSession.get().getUser();
				if (user == null) {
					return null;
				}
				UserSettings settings = userSettingsDAO.findByUser(user);
				return settings == null ? null : settings.getCustomCss();
			}
		}, new PageParameters().add("_t", System.currentTimeMillis()), null));
	}
}
