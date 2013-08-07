package controllers;

import interceptors.DefaultInterceptor;
import models.Invitation;
import models.PasswordReset;
import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.With;
import services.EmailService;

/**
 * Action for building all the emails
 * */
@With(DefaultInterceptor.class)
public class Emails extends Controller{

	/**
	 * Returns a play.api.template.Html object with a Email that was built from the parameters
	 * @see {@link EmailService#batchInvitationEmails(java.util.Set)}
	 * */
	public static Html invitationEmail(Invitation invitation){
		return views.html.emails.invitationEmail.render(invitation);
	}
	/**
	 * Returns a play.api.template.Html object with a Email that was built from the parameters
	 * @see {@link EmailService#resetEmail(PasswordReset)
	 * */
	public static Html passwordResetEmail(PasswordReset passwordReset){
		return views.html.emails.passwordResetEmail.render(passwordReset);
	}
}
