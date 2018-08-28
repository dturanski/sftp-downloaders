/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.example.sftp.downloader.nfs;

import java.util.Optional;

import io.spring.example.sftp.downloader.InputStreamPersister;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

/**
 * Configuration for use with Cloud Foundry Volume Services.
 *
 * @author David Turanski
 **/
@Configuration
@ConditionalOnCloudPlatform(CloudPlatform.CLOUD_FOUNDRY)
@EnableConfigurationProperties(NFSConfigurationProperties.class)
public class NFSInputStreamPersisterAutoConfiguration {

	@Bean
	public InputStreamPersister nfsInputStreamPersister(NFSMountPathSupplier mountPathSupplier) {
		return new NFSInputStreamPersister(mountPathSupplier.getMountPath());
	}

	@Bean
	public NFSMountPathSupplier nfsMountPathSupplier() {
		return new NFSMountPathSupplier();
	}

	@Bean
	NFSConfigPostProcessor postProcessor(Environment environment, NFSConfigurationProperties properties) {
		return new NFSConfigPostProcessor(environment, properties);
	}

	static class NFSConfigPostProcessor implements BeanPostProcessor {
		private static final String VCAP_SERVICES_PREFIX = "vcap.services";

		private final Environment environment;
		private final NFSConfigurationProperties properties;

		NFSConfigPostProcessor(Environment environment, NFSConfigurationProperties properties) {
			this.properties = properties;
			this.environment = environment;
		}

		@Nullable
		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
			if (bean instanceof NFSMountPathSupplier) {
				String serviceInstance = properties.getServiceInstance();

				((NFSMountPathSupplier) bean).setMountPath(
					Optional.ofNullable(environment.getProperty(
					String.join(".", VCAP_SERVICES_PREFIX, serviceInstance, "volume_mounts[0]", "container_dir")))
					.orElseThrow(() -> new IllegalArgumentException(
						"Service " + serviceInstance + " is not bound to this application" + ".")));
			}
			return bean;
		}
	}
}
