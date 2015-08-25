package nc.pub.mdm.frame.tool;

import java.io.PrintWriter;
import java.io.StringWriter;

import nc.bs.logging.Logger;
import nc.bs.logging.NCSysOutWrapper;

/**
 * 日志工具
 * 
 * @author 周海茂
 * @since 2012-03-29
 */
public class LogTool {

	public static boolean isDebug = Toolkit.isInDebug();

	public static void error(Exception e) {
		StringWriter sw = new StringWriter();
		if (isDebug) {
			e.printStackTrace(new PrintWriter(sw));
			if( System.out instanceof NCSysOutWrapper){
				NCSysOutWrapper no = (NCSysOutWrapper)System.out;
				e.printStackTrace( no.getSysStream() );
			}else{
				System.out.println( sw.toString() );
			}
		}
		Logger.error(sw.toString(), e);
	}

	public static void error(String strErr) {
		if (isDebug) {
			if( System.out instanceof NCSysOutWrapper){
				NCSysOutWrapper no = (NCSysOutWrapper)System.out;
				no.getSysStream().println(strErr);
			}else{
				System.out.println( strErr );
			}
		}
		Logger.error(strErr);
	}

	public static void debug(String strMsg) {
		if (isDebug) {
			if( System.out instanceof NCSysOutWrapper){
				NCSysOutWrapper no = (NCSysOutWrapper)System.out;
				no.getSysStream().println(strMsg);
			}else{
				System.out.println( strMsg );
			}
		}
		Logger.debug(strMsg);
	}

	public static void info(String strMsg) {
		if (isDebug) {
			if( System.out instanceof NCSysOutWrapper){
				NCSysOutWrapper no = (NCSysOutWrapper)System.out;
				no.getSysStream().println(strMsg);
			}else{
				System.out.println( strMsg );
			}
		}
		Logger.info(strMsg);
	}
}
