package controllers.knowledgebase;

import play.mvc.Controller;
import play.mvc.Result;

public class KnowledgeBase extends Controller{

	public static Result home(){
		return ok(views.html.kb.index.render());
	}
}
