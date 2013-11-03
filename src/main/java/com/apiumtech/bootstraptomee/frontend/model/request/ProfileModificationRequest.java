package com.apiumtech.bootstraptomee.frontend.model.request;

import com.wordnik.swagger.annotations.ApiClass;
import com.wordnik.swagger.annotations.ApiProperty;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("serial")
@ApiClass("Profile modification request")
@Data
public class ProfileModificationRequest implements Serializable {

	@ApiProperty(value = "changes email of the user, if specified")
	private String email;

	@ApiProperty(value = "changes password of the user, if specified")
	private String password;

	@ApiProperty(value = "generate a new api key")
	private boolean newApiKey;

}
