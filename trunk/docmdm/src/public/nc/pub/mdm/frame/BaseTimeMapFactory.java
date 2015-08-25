package nc.pub.mdm.frame;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存工厂
 * @author 周海茂
 * @since 2012-03-29
 */
public class BaseTimeMapFactory {

	private static Map<String,Long> timeMap = Collections.synchronizedMap(new HashMap<String, Long>());//new Hashtable<String, Long>();//CacheToMapAdapter.getInstance(BaseTimeMapFactory.class.getName());
	@SuppressWarnings("rawtypes")
	private static Map<String,Map> mapMap = Collections.synchronizedMap(new HashMap<String, Map>()); //new Hashtable<String, Map>();//CacheToMapAdapter.getInstance(BaseTimeMapFactory.class.getName());
	public static long defaultRefreshTime = 1000 * 60 * 10;// default refresh time= 10 minutes
	
	@SuppressWarnings({ "rawtypes" })
	public static synchronized Map getMap(String strMapKey, long refreshTime){
		strMapKey = BaseTimeMapFactory.class.getName() + "_" + strMapKey;
		Map retMap = null;
		retMap = mapMap.get(strMapKey);
		if( retMap==null ){
			retMap = new SafeHashTable();
			//retMap = new Hashtable();
			//retMap = new HashMap();
			mapMap.put(strMapKey, retMap);
		}
		checkTS(strMapKey, refreshTime);
		return retMap;
	}
	
	@SuppressWarnings("rawtypes")
	public static Map getMap(String strMapKey){
		return getMap(strMapKey, defaultRefreshTime);
	}
	
	@SuppressWarnings("rawtypes")
	public static synchronized  void checkTS(String strMapKey, long refreshTime){
		Long cacheTime = timeMap.get(strMapKey);
		if( cacheTime==null ){
			cacheTime = new Long( System.currentTimeMillis() );
			timeMap.put(strMapKey, cacheTime);
		}
		
		boolean isTimeOut = System.currentTimeMillis() > (cacheTime.longValue() + refreshTime) ;
		if( isTimeOut ){
			Map retMap = mapMap.get(strMapKey);
			retMap.clear();
			cacheTime = new Long( System.currentTimeMillis() );
			timeMap.put(strMapKey, cacheTime);
		}
	}
}
