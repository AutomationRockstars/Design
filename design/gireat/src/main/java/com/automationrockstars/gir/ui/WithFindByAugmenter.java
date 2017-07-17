package com.automationrockstars.gir.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotate UiPart class or method with this annotation specifying the class implementing {@link FindByAugmenter} 
 * e.g.
 *  
 *  {@literal @}FindBy(id="DYNAMIC_PLACEHOLDER_span")
 *  {@literal @}WithFindAugmenter(DynamicPlaceholderResolver.class)
 *  public interface DynamicDiv extends UiPart {
 *  ...
 *  }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface WithFindByAugmenter {

	Class<? extends FindByAugmenter> value();
}
