package com.apiumtech.bootstraptomee.backend.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Abstract model for all entities, defining id and table generator
 * 
 */
@SuppressWarnings("serial")
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractModel implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "gen")
	@TableGenerator(
			name = "gen",
			table = "hibernate_sequences",
			pkColumnName = "sequence_name",
			valueColumnName = "sequence_next_hi_value",
			allocationSize = 1000)
	private Long id;
}
