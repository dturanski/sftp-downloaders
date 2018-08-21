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

package io.spring.example.sftp.downloader.nfs.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;

import io.spring.example.sftp.downloader.InputStreamProvider;
import io.spring.example.sftp.downloader.InputStreamTransfer;
import io.spring.example.sftp.downloader.nfs.NFSConnectorAutoConfiguration;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.config.java.CloudScanConfiguration;
import org.springframework.cloud.config.java.ServiceScanConfiguration;
import org.springframework.cloud.connector.nfs.Mode;
import org.springframework.cloud.connector.nfs.NFSServiceConnector;
import org.springframework.cloud.connector.nfs.VolumeMount;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.file.FileHeaders;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author David Turanski
 **/
@SpringBootTest(properties = {"spring.cloud.enabled=false"})
@RunWith(SpringRunner.class)
@ActiveProfiles("cloud")
public class SftpDownloaderNFSTests {

	@ClassRule
	public static final TemporaryFolder localTemporaryFolder = new TemporaryFolder();

	@Autowired
	private Function<Message, Message> transfer;


	@Test
	public void functionConfiguredAndImplemented() throws IOException {

		assertThat(localTemporaryFolder.getRoot().exists()).isTrue();

		String target ="target.txt";

		Message message = MessageBuilder.withPayload("foo")
			.setHeader(FileHeaders.REMOTE_FILE,"source.txt")
			.setHeader(FileHeaders.FILENAME,target)
			.build();
		assertThat(transfer.apply(message)).isSameAs(message);
		assertThat(Files.exists(Paths.get(localTemporaryFolder.getRoot().getAbsolutePath(),target))).isTrue();

		assertThat(Files.readAllBytes(Paths.get(localTemporaryFolder.getRoot().getAbsolutePath(), target))).isEqualTo(
			IOUtils.toByteArray(new ClassPathResource("source.txt").getInputStream()));
	}

	@SpringBootApplication(exclude = NFSConnectorAutoConfiguration.class)
	 static class MyApplication {
		@Bean
		public BeanFactoryPostProcessor postProcessor() {
			return configurableListableBeanFactory -> {
				NFSServiceConnector nfsServiceConnector = mock(NFSServiceConnector.class);
				when(nfsServiceConnector.getVolumeMounts()).thenReturn(new VolumeMount[] {
					new VolumeMount(localTemporaryFolder.getRoot().getAbsolutePath(), Mode.ReadWrite) });
				configurableListableBeanFactory.registerSingleton("nfs", nfsServiceConnector);

			};
		}

		@Bean
		InputStreamProvider inputStreamProvider() {
			return resource -> {
				InputStream is = null;
				try {
					is = new ClassPathResource(resource).getInputStream();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				return is;
			};
		}
	}
}
