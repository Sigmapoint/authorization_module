# Authorization module

You need to:

* implement authorization_module.UsersManager, for example as sigleton:

        public class YourImplementationOfUserManager implements authorization_module.UsersManager {
    
        	private static YourImplementationOfUserManager instance = null;
    
        	private YourImplementationOfUserManager() {
        	}
    
        	public static YourImplementationOfUserManager getInstance() {
    	        if (instance == null) {
    	            instance = new YourImplementationOfUserManager();
    	        }
    	        return instance;
    	    }
    
    	    @Override
    	    public List<? extends Role> getRolesByID(String userID) {
    	        MyOwnUser userFromDB = getUserById(userID);
    	        if (userFromDB == null) {
    	            return Collections.emptyList();
    	        }
    	        return userFromDB.getRoles();
    	    }
    
    	    public MyOwnUser getUserById(String userID) {
    	        MyOwnUser user;
    	        user = MyOwnUser.find.where().eq("email", userID).findUnique();
                if (user == null) {
                    return null;
                }
                return user;
    	    }
    	}



* setUp authorization_module.AuthorizationFacade by UsersManager instance
	
    	public class Global extends GlobalSettings {
    
    		public void onStart(Application app) {
    			...
    			AuthorizationFacade.setManager(YourImplementationOfUserManager.getInstance());
    		}
    	}


* let Your models representing users implement authorization_module.UserAuthentication and sign by @MappedSuperclass ann
	
    	@MappedSuperclass
    	public class YourOwnUser extends play.db.ebean.Model implements authorization_module.UserAuthentication {
    		//...
    	}


* add "authorization_module.*" to ebean server (ebean.default for default) in conf/application.conf

        #EBEAN CONFIGURATION
        ebean.default="models.*,authorization_module.*"
    
    
    
# And use it !

### Mark controlers as avaible to specific group of users

        @CRestrict({UserRoles.ADMIN, UserRoles.CUSTOMER})
        public static Result get(UUID orderID) {
        
### Login Logout
#### Prepare user to ability login/out, eg during registration

        AuthorizationFacade.registerForAuth(user);

#### Login user

        try {
            String token = AuthorizationFacade.login(user);
            return created(token);
        } catch (UserNotExistsException e) {
            return badRequest(makeError("user", "not exists"));
        } catch (WrongCredentialsException e) {
            return unauthorized(makeError("user", "bad password"));
        }
        
#### Logout

        try {
            AuthorizationFacade.logoutUserUsingContext(ctx);
            return noContent();
        } catch (SessionNotExistsException e) {
            return badRequest(makeError("session", "not exists"));
        }
        
#### Change password for actually logged user

        try {
        	AuthorizationFacade.changePassword(ctx(), userOldPasswod, userNewPassword);
    		return ok();
    	} catch (WrongCredentialsException e) {
    		return unauthorized(makeError("user", "bad password"));
    	} catch (SessionNotExistsException e) {
    		return badRequest(makeError("session", "not exists"));
    	}
        
#### If implement simple helper like this below, You can easily check user is the one who he claims, is it admin etc by use AuthorizationFacade.getIDLoggedUser(context):

        public class Helpers {
    
            public static boolean correctUser(UUID id, Http.Context context) {
                return id != null && id.equals(currentUserDbId(context));
            }
            
            public static boolean isAdmin(Http.Context context) {
                YoutOwnUser user = currentUser(context);
            	List<? extends Role> roles = user.getRoles();
            	return roles.contains(UserRoles.ADMIN);
            }
        
            public static YoutOwnUser currentUser(Http.Context context) {
                String id = AuthorizationFacade.getIDLoggedUser(context);
                if (id == null) {
                    return null;
                } else {
                    return YourImplementationOfUserManager.getUserById(id);
                }
            }
        
            public static UUID currentUserDbId(Http.Context context) {
                User currentUser = currentUser(context);
                if (currentUser == null) {
                    return null;
                } else {
                    return currentUser.getYourDataBaseId(); //method in YourOwnUserModel to retireve identifier used by You, in this example UUID
                }
            }
        }
        
        

## Licence


		Copyright (C) <year> <copyright holders>
		
		Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
		
		The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
		
		THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
