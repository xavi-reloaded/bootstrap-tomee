package com.apiumtech.bootstraptomee.frontend.rest;

import com.apiumtech.bootstraptomee.frontend.rest.resources.UserREST;
import com.apiumtech.bootstraptomee.frontend.rest.resources.*;
import com.google.common.collect.Sets;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("/rest")
public class RESTApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> set = Sets.newHashSet();
		set.add(JsonProvider.class);

		set.add(UserREST.class);


		return set;
	}
}
