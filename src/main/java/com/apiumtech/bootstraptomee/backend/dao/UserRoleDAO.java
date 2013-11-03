package com.apiumtech.bootstraptomee.backend.dao;

import com.apiumtech.bootstraptomee.backend.model.User;
import com.apiumtech.bootstraptomee.backend.model.UserRole;
import com.apiumtech.bootstraptomee.backend.model.UserRole.Role;
import com.apiumtech.bootstraptomee.backend.model.UserRole_;
import com.apiumtech.bootstraptomee.backend.model.User_;
import com.google.common.collect.Sets;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

@Stateless
public class UserRoleDAO extends GenericDAO<UserRole> {

	@Override
	public List<UserRole> findAll() {
		CriteriaQuery<UserRole> query = builder.createQuery(getType());
		Root<UserRole> root = query.from(getType());
		query.distinct(true);

		root.fetch(UserRole_.user, JoinType.LEFT);

		return em.createQuery(query).getResultList();
	}

	public List<UserRole> findAll(User user) {
		CriteriaQuery<UserRole> query = builder.createQuery(getType());
		Root<UserRole> root = query.from(getType());

		query.where(builder.equal(root.get(UserRole_.user).get(User_.id), user.getId()));
		return cache(em.createQuery(query)).getResultList();
	}

	public Set<Role> findRoles(User user) {
		Set<Role> list = Sets.newHashSet();
		for (UserRole role : findAll(user)) {
			list.add(role.getRole());
		}
		return list;
	}
}
