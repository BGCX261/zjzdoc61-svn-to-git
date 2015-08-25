package nc.pub.mdm.frame.tool;

/**
 * 安全比较
 * @author 周海茂
 * @since 2012-8-28
 */
public class SafeComparator {
	
	public static int compareNotNull(Object o1, Object o2){
		if( o1==null && o2!=null ){
			return -1;
		}else if( o1!=null && o2==null ){
			return 1;
		}else if( o1 instanceof String ){
			return ((String)o1).compareTo((String)o2);
		}
		return 0; // 两者都不为空
	}
	
	public static boolean isEquals(Object o1, Object o2){
		if( o1==null && o2!=null ){
			return false;
		}else if( o1!=null && o2==null ){
			return false;
		}else if( o1 instanceof String ){
			return ((String)o1).equals((String)o2);
		}
		return false; // 两者都为空=false
	}

}
