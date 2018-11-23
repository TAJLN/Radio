package moonplex.tajln.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandInfo
{
  String description() default "no description available";
  
  String usage() default "/<command> ";
  
  String permission() default "";
  
  boolean onlyIngame() default false;
}