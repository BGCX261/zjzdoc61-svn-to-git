package nc.pub.mdm.frame;

import java.util.Hashtable;

/**
 * 安全哈希表
 * @author 周海茂
 * @since 2012-8-28
 */
public class SafeHashTable<K,V> extends Hashtable<K, V> {

	private static final long serialVersionUID = -8062357695784278967L;
	
	public synchronized V put(K key, V value) {
		if( key==null || value==null ){
			return null;
		}
		return super.put(key, value);
	};
	
	
	@Override
	public synchronized V get(Object key) {
		if( key==null ){
			return null;
		}
		return super.get(key);
	}

}
