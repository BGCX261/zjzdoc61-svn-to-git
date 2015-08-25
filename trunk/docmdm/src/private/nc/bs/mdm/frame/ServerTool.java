package nc.bs.mdm.frame;

/**
 * @author ÷‹∫£√Ø
 * @since 2012-8-28
 */
public class ServerTool {
	public static String getNCHome() {
		return System.getProperty("nc.server.location", System.getProperty("user.dir"));
	}

}
