package com.automationrockstars.gir.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a field on a Page Object to indicate that lookup should use a series of @FindBy tags
 * It will then search for all elements that match any of the FindBy criteria. Note that elements
 * are not guaranteed to be in document order.
 *
 * It can be used on a types as well, but will not be processed by default.
 *
 * Eg:
 *
 * <pre class="code">
 * &#64;FindAll({&#64;FindBy(how = How.ID, using = "foo"),
 *           &#64;FindBy(className = "bar")})
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface FindAll {
  FindBy[] value();
}