package com.github.fmarmar.assertj;

import java.util.Collection;

import org.assertj.core.api.Condition;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matchers;

public class Conditions {
	
	@SuppressWarnings("rawtypes")
	public static Condition<Collection> empty() {
		return new Condition<Collection>() {

			@Override
			public boolean matches(Collection value) {
				return value.isEmpty();
			}
		};
	}
	
	@SuppressWarnings("rawtypes")
	public static Condition<Collection> contains(final Collection values) {
		return new Condition<Collection>() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean matches(Collection value) {
				return value.containsAll(values);
			}
		};
	}
	
	public static Condition<Object> instanceOf(Class<?> type) {
		return new HamcrestCondition<>(Matchers.instanceOf(type));
	}
	
	public static <T> Condition<T> equalTo(T o) {
		return new HamcrestCondition<>(Matchers.equalTo(o));
	}
	
	public static <T extends java.lang.Comparable<T>> Condition<T> greaterThanOrEqualTo(T number) {
		return new HamcrestCondition<>(Matchers.greaterThanOrEqualTo(number));
	}
	
}