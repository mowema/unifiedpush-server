/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.unifiedpush.service.impl;

import java.util.UUID;

import org.jboss.aerogear.unifiedpush.api.Alias;
import org.jboss.aerogear.unifiedpush.cassandra.dao.AliasDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class AliasCrudServiceImpl implements AliasCrudService {

	@Autowired
	private AliasDao aliasDao;

	@Override
	public void remove(String pushApplicationId, String alias) {
		aliasDao.remove(pushApplicationId == null ? null : UUID.fromString(pushApplicationId), alias);
	}

	@Override
	public void remove(UUID pushApplicationId, String alias) {
		aliasDao.remove(pushApplicationId, alias);
	}

	@Override
	public Alias find(UUID pushApplicationId, String alias) {
		return aliasDao.findByAlias(pushApplicationId, alias);
	}

	@Override
	public Alias find(UUID pushApplicationId, UUID userId) {
		return aliasDao.findOne(pushApplicationId, userId);
	}

	@Override
	public void removeAll(UUID pushApplicationId) {
		aliasDao.removeAll(pushApplicationId);
	}

	@Override
	public void create(Alias alias) {
		aliasDao.create(alias);
	}

	@Override
	public void remove(UUID pushApplicationId, UUID userId) {
		aliasDao.remove(pushApplicationId, userId);
	}

}