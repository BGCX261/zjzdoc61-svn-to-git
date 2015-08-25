package nc.pub.mdm.frame.tool;

import java.util.HashMap;
import java.util.Map;

import nc.md.model.ITable;
import nc.pub.mdm.frame.BaseService;
import nc.pub.mdm.frame.BaseTimeMapFactory;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.sm.UserVO;

/**
 * 缓存工具
 * @author 周海茂
 * @since 2011-03-22
 */
public class CacheTool {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static Map<Class, Map> clazzMap = BaseTimeMapFactory.getMap(HashMap.class.getName());

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SuperVO getVoByPK(Class voClz, String strPK) throws BusinessException{
		Map mapTemp = BaseTimeMapFactory.getMap(voClz.getName());
		SuperVO vo = (SuperVO)mapTemp.get(strPK);
		if( vo==null ){
			vo = BaseService.queryByPK(voClz, strPK);
			mapTemp.put(strPK, vo);
		}
		return vo;
	
	}

	public static UserVO getVoUser(String pk_smuser) throws BusinessException{
		return (UserVO)getVoByPK(UserVO.class, pk_smuser);
	}

	@SuppressWarnings({ "rawtypes" })
	public static Map initCache(Class docvoClass, String strCodeField) throws BusinessException {
		Map cache = clazzMap.get(docvoClass);
		if (cache == null) {
			cache = BaseTimeMapFactory.getMap(docvoClass.getName());
			clazzMap.put(docvoClass, cache);
	
			if (SuperVO.class.isAssignableFrom(docvoClass)) {
				initCacheDoc(cache, docvoClass, strCodeField);
			}
		}
		return cache;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void initCacheDoc(Map cache, Class docvoClass, String strCodeField) throws BusinessException {
	
		SuperVO[] vos = BaseService.queryByCondition(docvoClass, null);
		for (int i = 0; i < vos.length; i++) {
			cache.put(vos[i].getPrimaryKey(), vos[i]);
			cache.put(vos[i].getAttributeValue(strCodeField), vos[i]);
		}
	
	}
	
static Map<String,Map<String, DocVO>> sysMap = new HashMap<String,Map<String,DocVO>>();
	
	public static Map<String,Map<String, DocVO>> initSysCacheDoc(String tabName,String keyFld,String keyVlu){
		Map<String, DocVO> map=sysMap.get(tabName);
		if(map==null||map.size()<=0){
			DocVO[] docvos=null;
			try {
				docvos=BaseService.queryMainData(tabName);
				Map<String, DocVO> subMap=new HashMap<String, DocVO>();
				for(DocVO doc:docvos){
					subMap.put((String) doc.getAttributeValue(keyFld), doc);
				}
				sysMap.put(tabName, subMap);
			} catch (BusinessException e) {
				LogTool.error(e);
			}
		}else{
			DocVO vo=map.get(keyVlu);
			if(vo==null){
				ITable md = BaseService.getTableMD(tabName);
				try {
					DocVO[] rltvos=BaseService.queryMainDataByWhere(tabName, md.getPrimaryKeyName(), keyFld+"='" + keyVlu + "'");
					map.put(keyVlu, rltvos[0]);
				} catch (BusinessException e) {
					LogTool.error(e);
				}
			}
		}
		return sysMap;
	}

}
