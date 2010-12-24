package org.coffeebreaks.yui;

@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Inherited
public @interface URLsFrom {
  /**
   * @return a URLsLister class
   */
  Class<? extends URLsLister> value();
}
