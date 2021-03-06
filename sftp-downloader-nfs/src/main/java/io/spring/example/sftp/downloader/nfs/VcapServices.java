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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author David Turanski
 **/
class VcapServices extends HashMap<String, List<VcapService>> {
	static VcapServices load(InputStream vcapServicesJson) {
		try {
			return new ObjectMapper().readValue(vcapServicesJson, VcapServices.class);
		}
		catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	static VcapServices load(String json) {
		return load(new ByteArrayInputStream(json.getBytes()));
	}
}
