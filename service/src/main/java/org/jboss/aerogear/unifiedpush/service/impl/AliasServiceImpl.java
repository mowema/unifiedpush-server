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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jboss.aerogear.unifiedpush.api.Alias;
import org.jboss.aerogear.unifiedpush.api.PushApplication;
import org.jboss.aerogear.unifiedpush.service.AliasService;
import org.jboss.aerogear.unifiedpush.service.PushApplicationService;
import org.jboss.aerogear.unifiedpush.service.impl.spring.IKeycloakService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.utils.UUIDs;

@Service
public class AliasServiceImpl implements AliasService {
	private final Logger logger = LoggerFactory.getLogger(AliasServiceImpl.class);

	@Inject
	private AliasCrudService aliasCrudService;
	@Inject
	private IKeycloakService keycloakService;
	@Inject
	private PushApplicationService pushApplicationService;

	public List<Alias> addAll(PushApplication pushApplication, List<Alias> aliases, boolean oauth2) {
		logger.debug("OAuth2 flag is: " + oauth2);
		List<Alias> aliasList = new ArrayList<>();

		// Create keycloak client if missing.
		if (oauth2)
			keycloakService.createClientIfAbsent(pushApplication);

		aliases.forEach(alias -> {
			create(alias);
			aliasList.add(alias);
		});

		return aliasList;
	}

	@Override
	public void updateAliasePassword(String aliasId, String currentPassword, String newPassword) {
		keycloakService.updateUserPassword(aliasId, currentPassword, newPassword);
	}

	@Override
	public void remove(UUID pushApplicationId, String alias) {
		// Remove any aliases related to this alias name
		remove(pushApplicationId, alias, false);
	}

	@Override
	public void remove(UUID pushApplicationId, UUID userId) {
		remove(pushApplicationId, userId, false);
	}

	@Override
	public void remove(UUID pushApplicationId, UUID userId, boolean destructive) {
		Alias alias = aliasCrudService.find(pushApplicationId, userId);
		this.remove(pushApplicationId, StringUtils.isNotEmpty(alias.getEmail()) ? alias.getEmail() : alias.getOther(),
				destructive);
	}

	private void remove(UUID pushApplicationId, String alias, boolean destructive) {
		// Remove any aliases belong to user_id
		aliasCrudService.remove(pushApplicationId, alias);

		if (destructive) {
			// Remove user from keyCloak
			keycloakService.delete(alias);

			// TODO - Remove all documents for a given alias
		}
	}

	@Override
	public Alias find(String pushApplicationId, String alias) {
		if (StringUtils.isEmpty(alias))
			return null;

		return aliasCrudService.find(StringUtils.isEmpty(pushApplicationId) ? null : UUID.fromString(pushApplicationId),
				alias);
	}

	@Override
	public Alias find(UUID pushApplicationId, UUID userId) {
		return aliasCrudService.find(pushApplicationId, userId);
	}

	/**
	 * Test if user exists / registered to KC.
	 *
	 * @param alias
	 *            alias name
	 */
	@Override
	public boolean registered(String alias) {
		return keycloakService.exists(alias);
	}

	/**
	 * Validate rather an alias is associated to a team/application.
	 *
	 * @param alias
	 *            alias name
	 * @param fqdn
	 *            domain / team name.
	 */
	public boolean associated(String alias, String fqdn) {
		PushApplication pushApplication = null;

		// Return application name from fqdn.
		if (StringUtils.isNotEmpty(fqdn)) {
			String applicationName = keycloakService.strip(fqdn);
			pushApplication = pushApplicationService.findByName(applicationName);
		}

		// TODO - Disallow query when fqdn is missing.
		// This can be done only when: 1) 1.6.0 is out of market 2) CAPTCHA was
		// added to login template.
		Alias aliasObj = find(pushApplication == null ? null : pushApplication.getPushApplicationID(), alias);

		return aliasObj != null;
	}

	private Alias exists(UUID pushApplicationUUID, Alias aliasToFind) {
		Alias alias = null;
		if (aliasToFind.getId() != null) {
			alias = aliasCrudService.find(pushApplicationUUID, aliasToFind.getId());

			if (alias != null)
				return alias;
		}

		if (StringUtils.isNotEmpty(aliasToFind.getEmail())) {
			alias = aliasCrudService.find(pushApplicationUUID, aliasToFind.getEmail());
			if (alias != null)
				return alias;
		}

		if (StringUtils.isNotEmpty(aliasToFind.getOther())) {
			alias = aliasCrudService.find(pushApplicationUUID, aliasToFind.getOther());
			if (alias != null)
				return alias;
		}

		return alias;
	}

	/*
	 * Remove all aliases by application id and invalidates alias cache.
	 */
	@Override
	public void removeAll(UUID pushApplicationId) {
		aliasCrudService.removeAll(pushApplicationId);

		// TODO - Remove KC client and all related users.
	}

	/**
	 * Create alias while preserving user uuid.
	 */
	@Override
	public void create(Alias alias) {
		// Initialize a new time-based UUID on case one is missing.
		if (alias.getId() == null) {
			// Search if alias is already register for application.
			// If so, use the same userId in-order to keep previous history.
			Alias existingAlias = exists(alias.getPushApplicationId(), alias);

			if (existingAlias != null) {
				// Remove all references to previous alias
				remove(alias.getPushApplicationId(), existingAlias.getId());
				// TODO - if user exists with KC, and primary email changed?
				// Change user alias and enforce registration process

				alias.setId(existingAlias.getId());
			} else {
				alias.setId(UUIDs.timeBased());
			}
		}

		aliasCrudService.create(alias);
	}

	@Override
	@Async
	public void createAsynchronous(Alias alias) {
		create(alias);
	}
}