package nc.bs.mdm.frame;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.pub.mdm.frame.BaseService;
import nc.pub.mdm.frame.BaseTimeMapFactory;
import nc.pub.mdm.frame.tool.CodeTool;
import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.frame.tool.SafeComparator;
import nc.pub.mdm.proxy.BaseUserObject;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.trade.pub.IBDACTION;

/**
 * @author 周海茂
 * @since 2012-8-28
 */
public class DocPrivateAction implements IPrivateAction {

	public void check(int intBdAction, AggregatedValueObject vo, Object userObj) throws Exception {
		if (vo != null && vo.getParentVO() != null) {
			CircularlyAccessibleValueObject hvo = vo.getParentVO();
			if (hvo != null && hvo instanceof DocVO) {
				DocVO mdHVO = (DocVO) hvo;
				DocVO[] impVOs = (DocVO[]) mdHVO.getAttributeValue(DocVO.KEY_IMPORT_VOS);
				if (impVOs != null) {
					vo.setParentVO(null);
					vo.setChildrenVO(null);
					impData(mdHVO, impVOs);
				}
			}
		}
	}

	public void dealAfter(int intBdAction, AggregatedValueObject billVo, Object userObj) throws Exception {
		BaseUserObject bbg = (BaseUserObject) userObj;
		AggregatedValueObject aggVO = bbg.getBillVO();
		if (aggVO != null && aggVO.getParentVO() != null) {
			SuperVO hvo = (SuperVO) aggVO.getParentVO();
			if (hvo instanceof DocVO) {
				String pk_mdmdoc = billVo.getParentVO().getPrimaryKey();
				DocVO mdHVO = (DocVO) hvo;
				DocVO[] subVOs = (DocVO[]) mdHVO.getAttributeValue(DocVO.KEY_SUB_VOS);
				BaseDAO dao = new BaseDAO();
				if (subVOs != null && subVOs.length > 0) {
					if ( IBDACTION.DELETE == intBdAction ) {
						dao.deleteVOArray(subVOs);
					}else if( IBDACTION.SAVE == intBdAction ){
						for (int i = 0; i < subVOs.length; i++) {
							DocVO svo = subVOs[i];
							String strParentField = svo.getParentKeyField();
							if (strParentField != null) {
								svo.setAttributeValue(strParentField, pk_mdmdoc);
								if ( VOStatus.DELETED == svo.getStatus()) {
									dao.deleteVO(svo);
									
								}else if( VOStatus.NEW == svo.getStatus()){
									dao.insertVOWithPK( svo );
									
								}else if( VOStatus.UPDATED == svo.getStatus()){
									dao.updateVO( svo );
								}
							} 
						}
					}

				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, DocVO> impMapQuery(String strTableCode, String strPKField, boolean isReQuery) {
		Map<String, DocVO> codeMap = (Map<String, DocVO>) BaseTimeMapFactory.getMap(this.getClass().getName() + "_CodeMap_" + strTableCode);
		if (isReQuery) {
			codeMap.clear();
		}
		if (codeMap.size() == 0) {
			String strCodeField = BaseService.getTableCodeField(strTableCode, null);
			String strSQL = "select * from " + strTableCode + " where isnull(dr,0)=0";
			try {
				DocVO[] dbVOs = BaseService.queryMainData(strSQL, strTableCode, strPKField, null);
				if (dbVOs != null) {
					for (int i = 0; i < dbVOs.length; i++) {
						DocVO dbvo = dbVOs[i];
						String strCode = (String) dbvo.getAttributeValue(strCodeField);
						String strShortCode = strCode;
						// while( strShortCode!=null && strShortCode.endsWith("0")){
						// strShortCode = strShortCode.substring(0, strShortCode.length()-1 );
						// }
						codeMap.put(strShortCode, dbvo);
					}
				}
			} catch (BusinessException e) {
				LogTool.error(e);
			}
		}
		return codeMap;
	}

	private Map<String, DocVO> impMapClean(DocVO ivo) {
		String strPKField = ivo.getPrimaryKeyField();
		String strTableCode = ivo.getTableCode();
		return impMapQuery(strTableCode, strPKField, true);
	}

	private void impData(DocVO mdHVO, DocVO[] impVOs) throws Exception {

		Map<String, DocVO> shortCodeMap = new HashMap<String, DocVO>();
		Object bz=mdHVO.getAttributeValue("is_endzero");
		UFBoolean ufbDelZero =bz instanceof String?
				new UFBoolean(bz.toString()): bz instanceof UFBoolean? (UFBoolean)bz:new UFBoolean(false) ;
		
		boolean isDelZero = (ufbDelZero != null && ufbDelZero.booleanValue());
		String strTreeRule = (String) mdHVO.getAttributeValue("tree_rule");

		BaseDAO dao = new BaseDAO();

		if (impVOs != null && impVOs.length > 0 && impVOs[0] != null) {
			Map<String, DocVO> codeMap = impMapClean(impVOs[0]);
			Iterator<String> itKey = codeMap.keySet().iterator();
			while (itKey.hasNext()) {
				String strCode = itKey.next();
				DocVO vo = codeMap.get(strCode);
				String strShortCode = CodeTool.makeShortCode(strCode, strTreeRule, isDelZero);
				shortCodeMap.put(strShortCode, vo);
			}
		}

		// dao.updateVO(mdHVO); // 更新TS时间戳，以便记录最后一次导入数据

		for (int i = 0; i < impVOs.length; i++) {
			try {
				DocVO ivo = impVOs[i];
				String strPKField = ivo.getPrimaryKeyField();
				String strParentField = ivo.getParentKeyField();
				String strTableCode = ivo.getTableCode();

				String strCodeField = BaseService.getTableCodeField(strTableCode, null);
				Map<String, DocVO> codeMap = impMapQuery(strTableCode, strPKField, false);

				if (strCodeField != null) {
					String strCode = (String) ivo.getAttributeValue(strCodeField);
					String strParentCode = (String) ivo.getAttributeValue(DocVO.KEY_IMP_PARENT_CODE);
					if (strParentCode == null || strParentCode.trim().length() < 1) {
						strParentCode = CodeTool.makeParentCode(strCode, strTreeRule, isDelZero);
					}
					if (strCode != null) {
						DocVO parentVO = null;
						if (strParentCode != null) {
							parentVO = isDelZero ? shortCodeMap.get(strParentCode) : codeMap.get(strParentCode);
						}
						if (parentVO != null) {
							String parentPK = parentVO.getPrimaryKey();
							boolean isSelfPK = SafeComparator.isEquals(parentPK, ivo.getPrimaryKey());
							if (!isSelfPK) {
								ivo.setAttributeValue(strParentField, parentVO.getPrimaryKey());
							}
						}

						DocVO dbVO = codeMap.get(strCode);
						if (dbVO != null) {
							ivo.setPrimaryKey(dbVO.getPrimaryKey());
							ivo.setAttributeValue("ts", dbVO.getAttributeValue("ts"));
							dao.updateVO(ivo);

						} else {
							dao.insertVO(ivo);
							codeMap.put(strCode, ivo);
							if (isDelZero) {
								strCode = CodeTool.makeShortCode(strCode, strTreeRule, isDelZero);
								shortCodeMap.put(strCode, ivo);
							}
						}
					}
				}
			} catch (Exception e) {

				UFBoolean isSingleDB = (UFBoolean) mdHVO.getAttributeValue("is_single");
				if (isSingleDB != null && isSingleDB.booleanValue()) {
					// 抛出异常，所有的数据统一回滚
					throw e;
				} else {
					// do nothing 丢弃异常，其他导入数据正常提交数据库
				}
			}
		}// End of for
	}
}
