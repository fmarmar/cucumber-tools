package com.github.fmarmar.cucumber.tools.report.model;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import lombok.Getter;

@Getter
public class Metadata {
	
	public static Metadata NO_METADATA_INSTANCE = new Metadata(); 

	private final String os;
	
	private final String language;
	
	private final String browser;

	private final String device;
	
	private final String id;
	
	private Metadata() {
		this(null, null, null, null);
	}
	
	@JsonCreator
	public Metadata(@JsonProperty("os") String os, @JsonProperty("language") String language, @JsonProperty("device") String device, @JsonProperty("browser") String browser) {
		this.os = os;
		this.language = language;
		this.device = device;
		this.browser = browser;
		this.id = buildId();
	}

	private String buildId() {
		
		Iterable<String> values = Arrays.asList(os, language, device, browser);
		values = Iterables.filter(values, Predicates.notNull());
		
		return (Iterables.isEmpty(values)) ? StringUtils.EMPTY : Joiner.on(';').join(values);
	}
	
	public boolean empty() {
		return Strings.isNullOrEmpty(id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object anObject) {
		
		if (anObject instanceof Metadata) {
			return id.equals(((Metadata) anObject).id);
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return id;
	}
	
}
