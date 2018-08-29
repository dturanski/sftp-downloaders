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

import io.spring.example.sftp.downloader.InputStreamPersister;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.cloud.CloudPlatform;
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
public class NFSInputStreamPersisterAutoConfiguration {

	@Bean
	public InputStreamPersister nfsInputStreamPersister(VcapService nfs) {
		return new NFSInputStreamPersister(nfs.getVolumeMounts().get(0).getContainerDir());
	}

	@Bean
	public VcapService nfs() {
		return new VcapService();
	}

	@Bean
	NFSConfigPostProcessor postProcessor(Environment environment) {
		return new NFSConfigPostProcessor(environment);
	}

	static class NFSConfigPostProcessor implements BeanPostProcessor {

		private final Environment environment;
		;

		NFSConfigPostProcessor(Environment environment) {
			this.environment = environment;
		}

		@Nullable
		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
			if (bean instanceof VcapService) {
				bean = NFS.load(environment.getProperty("VCAP_SERVICES"));
			}
			return bean;
		}
	}
}
