/* user role */
insert into 
	user_roles(
		name, 
		description
	)
values (
	"Administrator", 
	"Administrative role, has access to all modules."
);

/* role accesses */
insert into 
	role_accesses(
		user_role_id, 
		access_identifier
	)
values 
	(1, 'USER_VIEW'),
	(1, 'USER_CREATE'),
	(1, 'USER_EDIT'),
	(1, 'USER_DELETE'),
	(1, 'USER_RESTORE'),
	(1, 'USER_RESET_PASSWORD'),
	(1, 'USER_ROLE_VIEW'),
	(1, 'USER_ROLE_DELETE'),
	(1, 'USER_ROLE_RESTORE'),
	(1, 'USER_ROLE_EDIT'),
	(1, 'USER_ROLE_CREATE'),
	(1, 'CLIENT_VIEW'),
	(1, 'CLIENT_CREATE'),
	(1, 'CLIENT_EDIT'),
	(1, 'CLIENT_DELETE'),
	(1, 'CLIENT_RESTORE'),
	(1, 'CLIENT_IMPORT'),
	(1, 'CLIENT_DOWNLOAD_MASTER_LIST'),
	(1, 'GROUP_TYPE_VIEW'),
	(1, 'GROUP_TYPE_CREATE'),
	(1, 'GROUP_TYPE_EDIT'),
	(1, 'GROUP_TYPE_DELETE'),
	(1, 'GROUP_TYPE_RESTORE'),
	(1, 'GROUP_VIEW'),
	(1, 'GROUP_CREATE'),
	(1, 'GROUP_EDIT'),
	(1, 'GROUP_DELETE'),
	(1, 'GROUP_RESTORE')
	

/* user */
insert into 
	users (
		first_name,
		last_name,
		password,
		username,
		role_id
	)
values (
	'Joshua',
	'Zabala',
	'$2a$10$4jVR30wBnh8Kax6.K59DKew8afvv6GVGnAhUSf0szDPB1CUWT2.4i',
	'joshuazabala',
	1
)