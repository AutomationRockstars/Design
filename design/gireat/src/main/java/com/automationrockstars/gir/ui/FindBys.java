package com.automationrockstars.gir.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Used to mark a field on a Page Object to indicate that lookup should use a series of @FindBy tags
 * in a chain as described in {@link org.openqa.selenium.support.pagefactory.ByChained}
 *
 * It can be used on a types as well, but will not be processed by default.
 *
 * Eg:
 *
 * <pre class="code">
 * &#64;FindBys({&#64;FindBy(id = "foo"),
 *           &#64;FindBy(className = "bar")})
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface FindBys {
	FindBy[] value();
}
