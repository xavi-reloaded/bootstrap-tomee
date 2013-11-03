package com.apiumtech.bootstraptomee.frontend.model;

import com.wordnik.swagger.annotations.ApiClass;
import com.wordnik.swagger.annotations.ApiProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
@ApiClass("User information")
@Data
public class UserModel implements Serializable {

	@ApiProperty(value = "user id", required = true)
	private Long id;

	@ApiProperty(value = "user name", required = true)
	private String name;

	@ApiProperty("user email, if any")
	private String email;

	@ApiProperty("api key")
	private String apiKey;

	@ApiProperty(value = "user password, never returned by the api")
	private String password;

	@ApiProperty(value = "account status")
	private boolean enabled;

	@ApiProperty(value = "account creation date")
	private Date created;

	@ApiProperty(value = "last login date")
	private Date lastLogin;

	@ApiProperty(value = "user is admin")
	private boolean admin;

}
