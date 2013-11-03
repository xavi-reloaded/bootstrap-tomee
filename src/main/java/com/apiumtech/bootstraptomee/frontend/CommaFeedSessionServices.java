package com.apiumtech.bootstraptomee.frontend;

import com.apiumtech.bootstraptomee.backend.dao.UserRoleDAO;
import com.apiumtech.bootstraptomee.backend.services.UserService;
import org.apache.wicket.Component;

import javax.inject.Inject;

// extend Component in order to benefit from injection 
public class CommaFeedSessionServices extends Component {

	private static final long serialVersionUID = 1L;

	@Inject
	UserService userService;

	@Inject
	UserRoleDAO userRoleDAO;

	public CommaFeedSessionServices() {
		super("services");
	}

	public UserService getUserService() {
		return userService;
	}

	public UserRoleDAO getUserRoleDAO() {
		return userRoleDAO;
	}

	@Override
	protected void onRender() {
		// do nothing
	}
}
