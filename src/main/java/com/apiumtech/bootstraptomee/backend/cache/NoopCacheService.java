package com.apiumtech.bootstraptomee.backend.cache;

import com.apiumtech.bootstraptomee.backend.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import java.util.Collections;
import java.util.List;

@Alternative
@ApplicationScoped
public class NoopCacheService extends CacheService {


    @Override
    public void getSomethingCached(User... users) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
