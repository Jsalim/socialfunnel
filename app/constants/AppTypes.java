package constants;

public enum AppTypes {
	TAB_APP, EMBEDDABLE_APP, CUSTOM_APP, FACEBOOK_APP;
}

// An apptype must have a max of 40 characters length because of the utfmb4

//[info] play - database [default] connected at jdbc:mysql://127.0.0.1/socialfunnel
//[error] o.h.t.h.SchemaUpdate - HHH000388: Unsuccessful: create table app_types (app_id bigint not null, apptypes_id varchar(255) not null, primary key (app_id, apptypes_id))
//[error] o.h.t.h.SchemaUpdate - Specified key was too long; max key length is 767 bytes
//[error] o.h.t.h.SchemaUpdate - HHH000388: Unsuccessful: alter table app_types add index FK_i8n7e6m6tiqjiqhxngi9qgh7g (app_id), add constraint FK_i8n7e6m6tiqjiqhxngi9qgh7g foreign key (app_id) references App (id)
//[error] o.h.t.h.SchemaUpdate - Table 'socialfunnel.app_types' doesn't exist
