package com.apiumtech.bootstraptomee.frontend;

import com.apiumtech.bootstraptomee.backend.model.UserRole.Role;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

@Inherited
@InterceptorBinding
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurityCheck {

	/**
	 * Roles needed.
	 */
	@Nonbinding
	Role value() default Role.USER;

	@Nonbinding
	boolean apiKeyAllowed() default false;
}