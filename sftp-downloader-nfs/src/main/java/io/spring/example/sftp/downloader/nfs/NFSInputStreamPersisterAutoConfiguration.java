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

import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.connector.nfs.NFSServiceConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration for use with Cloud Foundry Volume Services.
 *
 * @author David Turanski
 **/
@Configuration
@Profile("cloud")
public class NFSInputStreamPersisterAutoConfiguration {

	@Bean
	public InputStreamPersister nfsInputStreamPersister(NFSServiceConnector nfs) {
		return new NFSInputStreamPersister(nfs.getVolumeMounts()[0].getContainerDir());
	}
}
