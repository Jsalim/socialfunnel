package controllers.admin;

import play.*;
import play.mvc.*;

import views.html.*;

public class Panel extends Controller {
	
    public static Result index() {
        return ok(views.html.panel.index.render());
    }
    
    public static Result account(){
    	 return ok(views.html.panel.account.render());
    }
    
    public static Result charts(){
    	 return ok(views.html.panel.charts.render());
    }
    
    public static Result faq(){
    	 return ok(views.html.panel.faq.render());
    }
    
    public static Result grid(){
    	 return ok(views.html.panel.grid.render());
    }
    
    public static Result login(){
    	 return ok(views.html.panel.login.render());
    }
    
    public static Result plans(){
    	 return ok(views.html.panel.plans.render());
    }
}
