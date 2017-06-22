/**
 * Copyright 2009-2017 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.javacrumbs.shedlock.spring;

import net.javacrumbs.shedlock.core.DefaultLockManager;
import net.javacrumbs.shedlock.core.LockProvider;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.StringValueResolver;

import java.time.temporal.TemporalAmount;

/**
 * Helper class to simplify configuration of Spring LockableTaskScheduler. embeddedValueResolver is injected by Spring automatically.
 * That's why this class implements FactoryBean.
 */
class SpringLockableTaskSchedulerFactoryBean implements FactoryBean<LockableTaskScheduler>, EmbeddedValueResolverAware, ScheduledLockConfiguration {
    private final TaskScheduler taskScheduler;

    private final LockProvider lockProvider;

    private final TemporalAmount defaultLockAtMostFor;

    private final TemporalAmount defaultLockAtLeastFor;

    private StringValueResolver embeddedValueResolver;

    SpringLockableTaskSchedulerFactoryBean(TaskScheduler taskScheduler, LockProvider lockProvider, TemporalAmount defaultLockAtMostFor, TemporalAmount defaultLockAtLeastFor) {
        this.taskScheduler = taskScheduler;
        this.lockProvider = lockProvider;
        this.defaultLockAtMostFor = defaultLockAtMostFor;
        this.defaultLockAtLeastFor = defaultLockAtLeastFor;
    }

    @Override
    public LockableTaskScheduler getObject() throws Exception {
        return new LockableTaskScheduler(
            taskScheduler,
            new DefaultLockManager(lockProvider, new SpringLockConfigurationExtractor(defaultLockAtMostFor, defaultLockAtLeastFor, embeddedValueResolver))
        );
    }

    @Override
    public Class<?> getObjectType() {
        return LockableTaskScheduler.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }
}
