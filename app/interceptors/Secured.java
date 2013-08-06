/**
 * 
 */
package interceptors;

import controllers.routes;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
/**
 * @author marcio
 *
 */
public class Secured extends Security.Authenticator {

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.login());
    }
    
}
