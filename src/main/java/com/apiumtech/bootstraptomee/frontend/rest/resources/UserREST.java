package com.apiumtech.bootstraptomee.frontend.rest.resources;

import com.apiumtech.bootstraptomee.backend.dao.UserDAO;
import com.apiumtech.bootstraptomee.backend.dao.UserRoleDAO;
import com.apiumtech.bootstraptomee.backend.dao.UserSettingsDAO;
import com.apiumtech.bootstraptomee.backend.model.User;
import com.apiumtech.bootstraptomee.backend.model.UserRole;
import com.apiumtech.bootstraptomee.backend.model.UserRole.Role;
import com.apiumtech.bootstraptomee.backend.model.UserSettings;
import com.apiumtech.bootstraptomee.backend.model.UserSettings.ReadingMode;
import com.apiumtech.bootstraptomee.backend.model.UserSettings.ReadingOrder;
import com.apiumtech.bootstraptomee.backend.model.UserSettings.ViewMode;
import com.apiumtech.bootstraptomee.backend.services.ApplicationSettingsService;
import com.apiumtech.bootstraptomee.backend.services.PasswordEncryptionService;
import com.apiumtech.bootstraptomee.backend.services.UserService;
import com.apiumtech.bootstraptomee.backend.startup.StartupBean;
import com.apiumtech.bootstraptomee.frontend.SecurityCheck;
import com.apiumtech.bootstraptomee.frontend.model.Settings;
import com.apiumtech.bootstraptomee.frontend.model.UserModel;
import com.apiumtech.bootstraptomee.frontend.model.request.ProfileModificationRequest;
import com.apiumtech.bootstraptomee.frontend.model.request.RegistrationRequest;
import com.google.common.base.Preconditions;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Arrays;

@Path("/user")
@Api(value = "/user", description = "Operations about the user")
public class UserREST extends AbstractREST {

	@Inject
	UserDAO userDAO;

	@Inject
	UserSettingsDAO userSettingsDAO;

	@Inject
	UserRoleDAO userRoleDAO;

	@Inject
	StartupBean startupBean;

	@Inject
	UserService userService;

	@Inject
	PasswordEncryptionService encryptionService;

	@Inject
	ApplicationSettingsService applicationSettingsService;

	@Path("/settings")
	@GET
	@ApiOperation(
			value = "Retrieve user settings",
			notes = "Retrieve user settings",
			responseClass = "com.apiumtech.bootstraptomee.frontend.model.Settings")
	public Response getSettings() {
		Settings s = new Settings();
		UserSettings settings = userSettingsDAO.findByUser(getUser());
		if (settings != null) {
			s.setReadingMode(settings.getReadingMode().name());
			s.setReadingOrder(settings.getReadingOrder().name());
			s.setViewMode(settings.getViewMode().name());
			s.setShowRead(settings.isShowRead());
			s.setSocialButtons(settings.isSocialButtons());
			s.setScrollMarks(settings.isScrollMarks());
			s.setTheme(settings.getTheme());
			s.setCustomCss(settings.getCustomCss());
			s.setLanguage(settings.getLanguage());
		} else {
			s.setReadingMode(ReadingMode.unread.name());
			s.setReadingOrder(ReadingOrder.desc.name());
			s.setViewMode(ViewMode.title.name());
			s.setShowRead(true);
			s.setTheme("default");
			s.setSocialButtons(true);
			s.setScrollMarks(true);
			s.setLanguage("en");
		}
		return Response.ok(s).build();
	}

	@Path("/settings")
	@POST
	@ApiOperation(value = "Save user settings", notes = "Save user settings")
	public Response saveSettings(@ApiParam Settings settings) {
		Preconditions.checkNotNull(settings);

		if (startupBean.getSupportedLanguages().get(settings.getLanguage()) == null) {
			settings.setLanguage("en");
		}

		UserSettings s = userSettingsDAO.findByUser(getUser());
		if (s == null) {
			s = new UserSettings();
			s.setUser(getUser());
		}
		s.setReadingMode(ReadingMode.valueOf(settings.getReadingMode()));
		s.setReadingOrder(ReadingOrder.valueOf(settings.getReadingOrder()));
		s.setShowRead(settings.isShowRead());
		s.setViewMode(ViewMode.valueOf(settings.getViewMode()));
		s.setScrollMarks(settings.isScrollMarks());
		s.setTheme(settings.getTheme());
		s.setCustomCss(settings.getCustomCss());
		s.setSocialButtons(settings.isSocialButtons());
		s.setLanguage(settings.getLanguage());
		userSettingsDAO.saveOrUpdate(s);
		return Response.ok().build();

	}

	@Path("/profile")
	@GET
	@ApiOperation(value = "Retrieve user's profile", responseClass = "com.apiumtech.bootstraptomee.frontend.model.UserModel")
	public Response get() {
		User user = getUser();
		UserModel userModel = new UserModel();
		userModel.setId(user.getId());
		userModel.setName(user.getName());
		userModel.setEmail(user.getEmail());
		userModel.setEnabled(!user.isDisabled());
		userModel.setApiKey(user.getApiKey());
		for (UserRole role : userRoleDAO.findAll(user)) {
			if (role.getRole() == Role.ADMIN) {
				userModel.setAdmin(true);
			}
		}
		return Response.ok(userModel).build();
	}

	@Path("/profile")
	@POST
	@ApiOperation(value = "Save user's profile")
	public Response save(@ApiParam(required = true) ProfileModificationRequest request) {
		User user = getUser();

		Preconditions.checkArgument(StringUtils.isBlank(request.getPassword()) || request.getPassword().length() >= 6);
		if (StringUtils.isNotBlank(request.getEmail())) {
			User u = userDAO.findByEmail(request.getEmail());
			Preconditions.checkArgument(u == null || user.getId().equals(u.getId()));
		}

		if (StartupBean.USERNAME_DEMO.equals(user.getName())) {
			return Response.status(Status.FORBIDDEN).build();
		}

		user.setEmail(StringUtils.trimToNull(request.getEmail()));
		if (StringUtils.isNotBlank(request.getPassword())) {
			byte[] password = encryptionService.getEncryptedPassword(request.getPassword(), user.getSalt());
			user.setPassword(password);
			user.setApiKey(userService.generateApiKey(user));
		}
		if (request.isNewApiKey()) {
			user.setApiKey(userService.generateApiKey(user));
		}
		userDAO.saveOrUpdate(user);
		return Response.ok().build();
	}

	@Path("/register")
	@POST
	@ApiOperation(value = "Register a new account")
	@SecurityCheck(Role.NONE)
	public Response register(@ApiParam(required = true) RegistrationRequest req) {
		try {
			userService.register(req.getName(), req.getPassword(), req.getEmail(), Arrays.asList(Role.USER));
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

	}

	@Path("/profile/deleteAccount")
	@POST
	@ApiOperation(value = "Delete the user account")
	public Response delete() {
		if (StartupBean.USERNAME_ADMIN.equals(getUser().getName()) || StartupBean.USERNAME_DEMO.equals(getUser().getName())) {
			return Response.status(Status.FORBIDDEN).build();
		}
		userService.unregister(getUser());
		return Response.ok().build();
	}
}