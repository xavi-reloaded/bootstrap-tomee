package com.apiumtech.bootstraptomee.frontend.model.request;

import com.wordnik.swagger.annotations.ApiClass;
import com.wordnik.swagger.annotations.ApiProperty;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("serial")
@ApiClass
@Data
public class IDRequest implements Serializable {

	@ApiProperty
	private Long id;

}
