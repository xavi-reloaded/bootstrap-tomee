package com.apiumtech.bootstraptomee.frontend.model.request;

import com.wordnik.swagger.annotations.ApiProperty;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("serial")
@Data
public class RegistrationRequest implements Serializable {

	@ApiProperty(value = "username, between 3 and 32 characters", required = true)
	private String name;

	@ApiProperty(value = "password, minimum 6 characters", required = true)
	private String password;

	@ApiProperty(value = "email address for password recovery", required = true)
	private String email;

}
