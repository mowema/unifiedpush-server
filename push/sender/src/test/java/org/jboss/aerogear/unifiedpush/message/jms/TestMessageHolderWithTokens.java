/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.unifiedpush.message.jms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.aerogear.unifiedpush.api.AndroidVariant;
import org.jboss.aerogear.unifiedpush.api.FlatPushMessageInformation;
import org.jboss.aerogear.unifiedpush.api.Variant;
import org.jboss.aerogear.unifiedpush.message.SenderConfig;
import org.jboss.aerogear.unifiedpush.message.UnifiedPushMessage;
import org.jboss.aerogear.unifiedpush.message.holder.MessageHolderWithTokens;
import org.jboss.aerogear.unifiedpush.service.AbstractNoCassandraServiceTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import reactor.core.publisher.WorkQueueProcessor;

@ContextConfiguration(classes = { SenderConfig.class })
public class TestMessageHolderWithTokens extends AbstractNoCassandraServiceTest {


    private UnifiedPushMessage message;
    private FlatPushMessageInformation information;
    private Variant variant;
    private Collection<String> deviceTokens;
    private static CountDownLatch delivered;

	@Inject
	private WorkQueueProcessor<MessageHolderWithTokens> event;

    @Before
    public void setUp() {
        information = new FlatPushMessageInformation();
        message = new UnifiedPushMessage();
        deviceTokens = new ArrayList<>();
        delivered = new CountDownLatch(5);

        if (event.downstreamCount() == 0)
        	event.doOnNext(s -> observeMessageHolderWithVariants(s)).subscribe();
    }

    @Test
    public void test() throws InterruptedException {
        variant = new AndroidVariant();
        for (int i = 0; i < 5; i++) {
            event.onNext(new MessageHolderWithTokens(information, message, variant, deviceTokens, i));
        }
        delivered.await(5, TimeUnit.SECONDS);
    }

    public void observeMessageHolderWithVariants(MessageHolderWithTokens msg) {
        delivered.countDown();
    }

}
