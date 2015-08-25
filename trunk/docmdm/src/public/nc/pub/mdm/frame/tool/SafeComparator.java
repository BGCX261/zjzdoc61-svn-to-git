package nc.pub.mdm.frame.tool;

/**
 * ��ȫ�Ƚ�
 * @author �ܺ�ï
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
		return 0; // ���߶���Ϊ��
	}
	
	public static boolean isEquals(Object o1, Object o2){
		if( o1==null && o2!=null ){
			return false;
		}else if( o1!=null && o2==null ){
			return false;
		}else if( o1 instanceof String ){
			return ((String)o1).equals((String)o2);
		}
		return false; // ���߶�Ϊ��=false
	}

}
