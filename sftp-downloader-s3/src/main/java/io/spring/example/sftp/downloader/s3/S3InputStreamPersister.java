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

package io.spring.example.sftp.downloader.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.spring.example.sftp.downloader.InputStreamPersister;
import io.spring.example.sftp.downloader.InputStreamTransfer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author David Turanski
 **/
public class S3InputStreamPersister implements InputStreamPersister {
	private static Log log = LogFactory.getLog(S3InputStreamPersister.class);
	private final AmazonS3 s3;
	private final String bucket;

	public S3InputStreamPersister(AmazonS3 s3, String bucket, boolean createBucket) {
		this.s3 = s3;
		this.bucket = bucket;
		verifyAndCreateBucketIfNecessary(createBucket);
	}

	@Override
	public void save(InputStreamTransfer transfer) {
		log.info(String.format("Saving source contents to bucket %s, key %s", bucket, transfer.getTarget()));
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, transfer.getTarget(), transfer.getSource(),
			getObjectMetadata(transfer));
		s3.putObject(putObjectRequest);
	}

	private ObjectMetadata getObjectMetadata(InputStreamTransfer transfer) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		if (transfer.getMetadata() != null) {
			objectMetadata.setUserMetadata(transfer.getMetadata());
		}
		return objectMetadata;
	}

	private void verifyAndCreateBucketIfNecessary(boolean createBucket) {
		if (!s3.doesBucketExistV2(bucket)) {
			if (createBucket) {
				s3.createBucket(bucket);
			}
			else {
				throw new IllegalArgumentException(String.format("Bucket %s does not exist", bucket));
			}
		}
	}
}
