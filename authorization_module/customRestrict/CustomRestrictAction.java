package authorization_module.authorization_module.customRestrict;

import authorization_module.authorization_module.CRestrict;
import authorization_module.authorization_module.UserRoles;
import be.objectify.deadbolt.java.actions.RestrictAction;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomRestrictAction extends Action<CRestrict> {

	@Override
	public Result call(Http.Context context) throws Throwable {
		final CRestrict outerConfig = configuration;
		RestrictAction restrictAction = new RestrictAction(configuration.config(), this.delegate) {

			@Override
			public List<String[]> getRoleGroups() {
				List<String[]> roleNamesGroups = new ArrayList<>();
				for (UserRoles roleGroup : outerConfig.value()) {
					roleNamesGroups.add(Arrays.asList(roleGroup.getName()).toArray(new String[0]));
				}
				return roleNamesGroups;
			}
		};
		return restrictAction.call(context);
	}
}
