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

package io.spring.example.sftp.downloader.local;

import java.io.File;

import io.spring.example.sftp.downloader.AbstractFileInputStreamPersister;
import io.spring.example.sftp.downloader.InputStreamTransfer;

/**
 * @author David Turanski
 **/
public class LocalInputStreamPersister extends AbstractFileInputStreamPersister {

	@Override
	public void save(InputStreamTransfer transfer) {
		File targetFile = new File(transfer.getTarget());
		doSave(transfer.getSource(), targetFile);
	}
}
