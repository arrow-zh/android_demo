package com.ev.hikvideo.util;

/**
 * @author zhangwei59 2017/3/10 14:28
 * @version V1.0.0
 */
public final class Constants {

	public static String PLATFORM_IP = "111.9.5.26:1443";
	public static String USERNAME = "admin";
	public static String PASSSWD = "hik12345+";

	private Constants() {
	}

	/**
	 * SharedPreferences数据表名称
	 */
	public static String APP_DATA = "app_data";
	/**
	 * SharedPreferences数据表用户名
	 */
	public static String USER_NAME = "user_name";
	/**
	 * SharedPreferences数据表用户密码
	 */
	public static String PASSWORD = "password";
	/**
	 * SharedPreferences数据表登录IP地址
	 */
	public static String ADDRESS_NET = "address_net";

	/**
	 * Intent相关常量
	 */
	public interface IntentKey {
		/**
		 * 获取根节点数据
		 */
		String GET_ROOT_NODE = "getRootNode";
		/**
		 * 获取子节点列表
		 */
		String GET_SUB_NODE = "getChildNode";
		/**
		 * 父节点类型
		 */
		String PARENT_NODE_TYPE = "parentNodeType";
		/**
		 * 父节点ID
		 */
		String PARENT_ID = "parentId";
		/**
		 * 监控点资源
		 */
		String CAMERA = "Camera";
	}
}
