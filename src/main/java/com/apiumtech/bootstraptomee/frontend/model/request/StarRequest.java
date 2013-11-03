package com.apiumtech.bootstraptomee.frontend.model.request;

import com.wordnik.swagger.annotations.ApiClass;
import com.wordnik.swagger.annotations.ApiProperty;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("serial")
@ApiClass("Star Request")
@Data
public class StarRequest implements Serializable {

	@ApiProperty(value = "id", required = true)
	private String id;

	@ApiProperty(value = "feed id", required = true)
	private Long feedId;

	@ApiProperty(value = "starred or not")
	private boolean starred;

}
