package authorization_module.authorization_module;

import authorization_module.authorization_module.customRestrict.CustomRestrictAction;
import be.objectify.deadbolt.java.actions.Restrict;
import play.mvc.With;

import java.lang.annotation.*;

@With(CustomRestrictAction.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Inherited
public @interface CRestrict {

	UserRoles[] value();

	Restrict config() default @Restrict({});
}

/* To accept caller is enough to have one of role from list */
