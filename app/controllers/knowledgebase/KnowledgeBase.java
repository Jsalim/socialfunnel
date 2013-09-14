package controllers.knowledgebase;

import interceptors.DefaultInterceptor;
import interceptors.UserSessionInterceptor;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import util.UserSession;

@With({DefaultInterceptor.class, UserSessionInterceptor.class})
public class KnowledgeBase extends Controller{

	public static Result home(){
		return ok(views.html.kb.index.render());
	}
}
