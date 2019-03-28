package com.mnn.society.server.messaging;

/**
 * This holds all the RequestTypes that will be send between client and server. 
 * If the Request isnt here, it wont be interpreted.
 * @author mickeman
 *
 */
public class RequestType {
	public static final int SERVER_STATE 			= 1001;
	public static final int LOGIN 					= 1002;
	public static final int LOGOUT 					= 1003;
	public static final int LOGIN_SUCCESS 			= 1004;
	public static final int LOGIN_FAILURE 			= 1005;
	public static final int WORKER_BAMUL_UPDATE 	= 1006;
	public static final int WORKER_OWNER_BROADCAST 	= 1007;
	public static final int WORKER_OWNER_LIST 		= 1008;
	public static final int WORKER_BAMUL_UPDATE_OK 	= 1009;
	public static final int WORKER_BAMUL_INVALID 	= 1010;
	public static final int WORKER_SINGLE_UPDATE 	= 1011;
	public static final int WORKER_ACTION_BEGIN 	= 1012;
	public static final int WORKER_ACTION_COMPLETED = 1013;
	public static final int WORKER_ABORT_SCRIPT 	= 1014;
	public static final int WORKER_ABORT_SCRIPT_OK 	= 1015;
	public static final int WORKER_ABORT_SCRIPT_DONE= 1016;
	public static final int WORKER_BUSY 			= 1017;
	public static final int USER_CREATE_FAIL 		= 1018;
	public static final int USER_CREATE_SUCCESS 	= 1019;
	public static final int USER_CREATE 			= 1020;
}