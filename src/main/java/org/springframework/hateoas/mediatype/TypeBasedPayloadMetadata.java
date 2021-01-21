/*
 * Copyright 2019-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.hateoas.mediatype;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.ResolvableType;
import org.springframework.hateoas.AffordanceModel.InputPayloadMetadata;
import org.springframework.hateoas.AffordanceModel.Named;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/**
 * {@link InputPayloadMetadata} implementation based on a Java type.
 *
 * @author Oliver Drotbohm
 */
class TypeBasedPayloadMetadata implements InputPayloadMetadata {

	private final ResolvableType type;
	private final SortedMap<String, PropertyMetadata> properties;
	private final @Nullable MediaType mediaType;

	TypeBasedPayloadMetadata(ResolvableType type, Stream<PropertyMetadata> properties) {
		this(type, new TreeMap<>(
				properties.collect(Collectors.toMap(PropertyMetadata::getName, Function.identity()))), null);
	}

	TypeBasedPayloadMetadata(ResolvableType type, SortedMap<String, PropertyMetadata> properties,
			@Nullable MediaType mediaType) {

		this.type = type;
		this.properties = properties;
		this.mediaType = mediaType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.AffordanceModel.PayloadMetadata#customize(org.springframework.hateoas.AffordanceModel.Named, java.util.function.Function)
	 */
	@Override
	public <T extends Named> T customize(T target, Function<PropertyMetadata, T> customizer) {

		PropertyMetadata metadata = this.properties.get(target.getName());

		return metadata == null ? target : customizer.apply(metadata);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.mediatype.PayloadMetadata#stream()
	 */
	@Override
	public Stream<PropertyMetadata> stream() {
		return properties.values().stream();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.MessageSourceResolvable#getCodes()
	 */
	@Override
	public List<String> getI18nCodes() {

		Class<?> type = this.type.resolve(Object.class);

		return Arrays.asList(type.getName(), type.getSimpleName());
	}

	ResolvableType getType() {
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.AffordanceModel.InputPayloadMetadata#withMediaType(org.springframework.http.MediaType)
	 */
	@Override
	public InputPayloadMetadata withMediaType(@Nullable MediaType mediaType) {
		return new TypeBasedPayloadMetadata(type, properties, mediaType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.AffordanceModel.InputPayloadMetadata#getMediaType()
	 */
	@Nullable
	@Override
	public MediaType getMediaType() {
		return mediaType;
	}
}
