package com.apiumtech.bootstraptomee.backend.cache;

import com.apiumtech.bootstraptomee.backend.model.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;

public abstract class CacheService {


	// user categories

	public abstract void getSomethingCached(User... users);

}
