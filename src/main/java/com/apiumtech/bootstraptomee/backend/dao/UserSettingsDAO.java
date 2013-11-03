package com.apiumtech.bootstraptomee.backend.dao;

import com.apiumtech.bootstraptomee.backend.model.User;
import com.apiumtech.bootstraptomee.backend.model.UserSettings;
import com.apiumtech.bootstraptomee.backend.model.UserSettings_;
import com.apiumtech.bootstraptomee.backend.model.User_;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Stateless
public class UserSettingsDAO extends GenericDAO<UserSettings> {

	public UserSettings findByUser(User user) {

		CriteriaQuery<UserSettings> query = builder.createQuery(getType());
		Root<UserSettings> root = query.from(getType());

		query.where(builder.equal(root.get(UserSettings_.user).get(User_.id), user.getId()));

		UserSettings settings = null;
		try {
			settings = cache(em.createQuery(query)).getSingleResult();
		} catch (NoResultException e) {
			settings = null;
		}
		return settings;
	}
}
