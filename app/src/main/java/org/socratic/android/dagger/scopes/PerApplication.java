package org.socratic.android.dagger.scopes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by williamxu on 7/10/17.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerApplication {}
