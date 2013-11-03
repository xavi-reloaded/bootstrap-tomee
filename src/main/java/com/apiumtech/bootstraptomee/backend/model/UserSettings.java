package com.apiumtech.bootstraptomee.backend.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "USERSETTINGS")
@SuppressWarnings("serial")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Getter
@Setter
public class UserSettings extends AbstractModel {

	public enum ReadingMode {
		all, unread
	}

	public enum ReadingOrder {
		asc, desc
	}

	public enum ViewMode {
		title, expanded
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReadingMode readingMode;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReadingOrder readingOrder;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ViewMode viewMode;

	@Column(name = "user_lang", length = 4)
	private String language;

	private boolean showRead;
	private boolean scrollMarks;
	private boolean socialButtons;

	@Column(length = 32)
	private String theme;

	@Lob
	@Column(length = Integer.MAX_VALUE)
	private String customCss;

}
