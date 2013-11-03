package com.apiumtech.bootstraptomee.frontend.pages;

import com.apiumtech.bootstraptomee.backend.dao.*;
import com.apiumtech.bootstraptomee.backend.model.ApplicationSettings;
import com.apiumtech.bootstraptomee.backend.model.User;
import com.apiumtech.bootstraptomee.backend.model.UserSettings;
import com.apiumtech.bootstraptomee.backend.services.ApplicationSettingsService;
import com.apiumtech.bootstraptomee.backend.services.MailService;
import com.apiumtech.bootstraptomee.backend.startup.StartupBean;
import com.apiumtech.bootstraptomee.frontend.CommaFeedSession;
import com.apiumtech.bootstraptomee.frontend.utils.WicketUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings("serial")
public abstract class BasePage extends WebPage {

	@Inject
	StartupBean startupBean;

	@Inject
	protected UserDAO userDAO;

	@Inject
	protected UserSettingsDAO userSettingsDAO;

	@Inject
	protected UserRoleDAO userRoleDAO;

	@Inject
	MailService mailService;

	@Inject
	ApplicationSettingsService applicationSettingsService;

	private ApplicationSettings settings;

	public BasePage() {

		String lang = "en";
		String theme = "default";
		User user = CommaFeedSession.get().getUser();
		if (user != null) {
			UserSettings settings = userSettingsDAO.findByUser(user);
			if (settings != null) {
				lang = settings.getLanguage() == null ? "en" : settings.getLanguage();
				theme = settings.getTheme() == null ? "default" : settings.getTheme();
			}
		}

		add(new TransparentWebMarkupContainer("html").setMarkupId("theme-" + theme).add(new AttributeModifier("lang", lang)));

		settings = applicationSettingsService.get();
		add(new HeaderResponseContainer("footer-container", "footer-container"));
		add(new WebMarkupContainer("uservoice") {
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisibilityAllowed(settings.isFeedbackButton());
			}
		});
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		if (getApplication().getConfigurationType() == RuntimeConfigurationType.DEPLOYMENT) {
			long startupTime = startupBean.getStartupTime();
			String suffix = "?" + startupTime;
			response.render(JavaScriptHeaderItem.forUrl("static/all.js" + suffix));
			response.render(CssHeaderItem.forUrl("static/all.css" + suffix));
		} else {
			response.render(JavaScriptHeaderItem.forUrl("wro/lib.js"));
			response.render(CssHeaderItem.forUrl("wro/lib.css"));
			response.render(CssHeaderItem.forUrl("wro/app.css"));

			response.render(JavaScriptHeaderItem.forUrl("js/epiceditor/assets/js/epiceditor.js"));
			response.render(JavaScriptHeaderItem.forUrl("js/epiceditor/assets/js/keywordParser.js"));
			response.render(JavaScriptHeaderItem.forUrl("js/extractor/views/ExtractorView.js"));
			response.render(JavaScriptHeaderItem.forUrl("js/extractor/models/ExtractorModel.js"));

			response.render(JavaScriptHeaderItem.forUrl("js/welcome.js"));
			response.render(JavaScriptHeaderItem.forUrl("js/main.js"));
			response.render(JavaScriptHeaderItem.forUrl("js/controllers.js"));
			response.render(JavaScriptHeaderItem.forUrl("js/directives.js"));
			response.render(JavaScriptHeaderItem.forUrl("js/filters.js"));
			response.render(JavaScriptHeaderItem.forUrl("js/services.js"));

		}

		if (StringUtils.isNotBlank(settings.getGoogleAnalyticsTrackingCode())) {
			Map<String, Object> vars = Maps.newHashMap();
			vars.put("trackingCode", settings.getGoogleAnalyticsTrackingCode());
			WicketUtils.loadJS(response, BasePage.class, "analytics", vars);
		}

	}
}
