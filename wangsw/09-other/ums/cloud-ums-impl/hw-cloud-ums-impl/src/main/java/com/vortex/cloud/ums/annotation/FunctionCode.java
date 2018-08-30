package com.vortex.cloud.ums.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vortex.cloud.ums.enums.ResponseType;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionCode {
	public String value();

	public ResponseType type();
}
