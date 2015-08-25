package nc.bs.mdm.frame;

import java.io.Serializable;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.trade.business.IBDBusiCheck;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.bd.refcheck.IReferenceCheck;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pub.mdm.frame.BaseTimeMapFactory;
import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.proxy.BaseUserObject;
import nc.vo.bd.BDMsg;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.trade.pub.IBDACTION;
import nc.vo.trade.pub.IExAggVO;

/**
 * 后台事务内检查校验<br>
 * 校验1、删除引用 2、唯一字段
 * @author 周海茂
 * @since 2011-12-26
 */
public class BaseBusiChecker implements IBDBusiCheck, Serializable {

	private static final long serialVersionUID = 6242665249192519637L;

	/**
	 * @see nc.bs.trade.comdelete.BillDelete#deleteBD(AggregatedValueObject, Object)
	 */
	public BaseBusiChecker() {
	}

	public void check(int intBdAction, AggregatedValueObject vo, Object userObj) throws Exception {

		IPrivateAction prvAction = getPrivateAction(vo);
		if (prvAction != null) {
			try{
				prvAction.check(intBdAction, vo, userObj);
			}catch(Exception e){
				LogTool.error(e);
				throw new BusinessException("主数据后台("+prvAction.getClass().getName()+")处理异常！", e.getCause());
			}
		}

		BaseUserObject getter = (BaseUserObject) userObj;
		HYBillVO cloneVO = new HYBillVO(); // TODO:IExAggVO ??

		SuperVO cloneHeader = null;
		if (vo.getParentVO() != null) {
			cloneHeader = (SuperVO) vo.getParentVO().clone();
			cloneVO.setParentVO(cloneHeader);
		}
		cloneVO.setChildrenVO(vo.getChildrenVO());

		// getter.setBillVO(vo);
		getter.setBillVO(cloneVO);

		if (getter.getStrUniqueFieldCodes() == null || getter.getStrUniqueFieldNames() == null) {
			return;
		}
		if (intBdAction == IBDACTION.SAVE) {
			CircularlyAccessibleValueObject header = vo.getParentVO();
			CircularlyAccessibleValueObject[] body = vo.getChildrenVO();
			if (header != null && !getter.isSingleBody()) {
				// if (header.getPrimaryKey() != null) {
				checkVO(header, getter);
				// }
			} else {
				if (body != null && body.length > 0) {
					for (int i = 0; i < body.length; i++) {
						if (body[i].getPrimaryKey() == null) {
							checkVO(body[i], getter);
						}

						if (body[i].getStatus() == VOStatus.DELETED) {
							checkReference((SuperVO) body[i]);
						}
					}
				}
			}

		} else if (intBdAction == IBDACTION.DELETE) {
			CircularlyAccessibleValueObject header = vo.getParentVO();
			CircularlyAccessibleValueObject[] body = vo.getChildrenVO();
			if (header != null && !getter.isSingleBody()) {
				checkReference((SuperVO) header);
			} else {
				if (body != null && body.length > 0) {
					checkReference((SuperVO[]) body);
				}
			}

		}
	}

	private void checkReference(SuperVO vo) throws Exception {
		if (vo instanceof DocVO) {
			DocVO dvo = (DocVO) vo;
			IReferenceCheck iIReferenceCheck = (IReferenceCheck) NCLocator.getInstance().lookup(IReferenceCheck.class.getName());
			if (vo != null && iIReferenceCheck.isReferenced(dvo.getTableCode(), dvo.getPrimaryKey())) {
				throw new BusinessException(BDMsg.MSG_REF_NOT_DELETE());
			}
		}
	}

	private void checkReference(SuperVO[] vos) throws Exception {
		IReferenceCheck iIReferenceCheck = (IReferenceCheck) NCLocator.getInstance().lookup(IReferenceCheck.class.getName());
		for (int i = 0; i < vos.length; i++) {
			SuperVO vo = vos[i];
			if (vo instanceof DocVO) {
				DocVO dvo = (DocVO) vo;
				if (vo != null && iIReferenceCheck.isReferenced(dvo.getTableCode(), vo.getPrimaryKey())) {
					throw new BusinessException(BDMsg.MSG_REF_NOT_DELETE());
				}
			}
		}
	}

	private void checkVO(CircularlyAccessibleValueObject vo, BaseUserObject getter) throws Exception {
		if (vo instanceof DocVO) {
			DocVO dvo = (DocVO) vo;
			StringBuffer sbWhere = new StringBuffer();
			String[] strORs = getter.getStrUniqueFieldCodes().split(",");
			for (int i = 0; i < strORs.length; i++) {
				if (i > 0) {
					sbWhere.append(" or ");
				}
				String strFieldTemp = strORs[i];
				if (strFieldTemp.indexOf("+") > 0) {
					sbWhere.append("(");
					String[] strANDs = strFieldTemp.split("\\+");
					for (int j = 0; j < strANDs.length; j++) {
						String strAndField = strANDs[j].trim();
						Object objValue = vo.getAttributeValue(strAndField);
						if (j > 0) {
							sbWhere.append(" and ");
						}
						makeWhere(objValue, strAndField, sbWhere);
					}
					sbWhere.append(")");
				} else {
					Object objValue = vo.getAttributeValue(strFieldTemp);
					makeWhere(objValue, strFieldTemp, sbWhere);
				}
			}

			if (sbWhere.indexOf("()") == -1) {
				// IUifService server = (IUifService)
				// NCLocator.getInstance().lookup(IUifService.class.getName());
				// SuperVO[] vos = server.queryByCondition(vo.getClass(),
				// sbWhere.toString());
				sbWhere.insert(0, "(").append(")");
				if (vo.getPrimaryKey() != null) {
					if (vo instanceof SuperVO) {
						sbWhere.append(" and ").append(dvo.getPrimaryKeyField()).append("<>'").append(vo.getPrimaryKey()).append("'");
					}
				}
				sbWhere.append(" and isnull(dr,0)=0");

				IUAPQueryBS impl = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
				String strCountSql = "select count(*) from " + dvo.getTableCode() + " where " + sbWhere.toString();
				Object objMax = impl.executeQuery(strCountSql, new ColumnProcessor());
				if (objMax != null) {
					if ((Integer) objMax > 0) {
						throw new BusinessException("存在不唯一值： " + getter.getStrUniqueFieldNames());
					}
				}
			}
		}
	}

	/**
	 * @param objFieldValue
	 * @param strFieldCode
	 * @param sbWhere
	 */
	private void makeWhere(Object objFieldValue, String strFieldCode, StringBuffer sbWhere) {
		if (objFieldValue != null) {
			sbWhere.append(strFieldCode).append("=");
			if (objFieldValue instanceof UFDouble || objFieldValue instanceof Integer) {
				sbWhere.append(objFieldValue.toString());
			} else {
				sbWhere.append("'").append(objFieldValue.toString()).append("'");
			}
		} else {
			sbWhere.append(" ").append(strFieldCode).append(" is null");
		}
	}

	/*
	 * @see nc.bs.trade.business.IBDBusiCheck#dealAfter(int, nc.vo.pub.AggregatedValueObject, java.lang.Object)
	 */
	public void dealAfter(int intBdAction, AggregatedValueObject billVo, Object userObj) throws Exception {
		if (IBDACTION.SAVE == intBdAction && billVo instanceof IExAggVO && userObj instanceof BaseUserObject) {
			// BaseUserObject getter = (BaseUserObject) userObj;
			// DocVO parent = (DocVO) getter.getBillVO().getParentVO();
			// String strTable = parent.getTableCode();
			// String strPKField = parent.getPrimaryKeyField();
			// parent = (DocVO) BaseService.queryMainDataByPK(strTable, strPKField, parent.getPrimaryKey());
			// billVo.setParentVO(parent);

		} else if (IBDACTION.DELETE == intBdAction) {
			if (billVo == null && userObj instanceof BaseUserObject) {
				BaseUserObject getter = (BaseUserObject) userObj;
				billVo = getter.getBillVO();
			}
		}

		IPrivateAction prvAction = getPrivateAction(billVo);
		if (prvAction != null) {
			prvAction.dealAfter(intBdAction, billVo, userObj);
		}

	}

	/**
	 * @see<br> nc.vo.crccprj.main.ProjectApproveVO<br>
	 *          nc.bs.crccprj.main.ProjectApprovePrivateAction<br>
	 * @param billVo
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private IPrivateAction getPrivateAction(AggregatedValueObject billVo) {
		String strImplClz = null;
		IPrivateAction prvAction = null;
		if (billVo != null) {
			Class voClz = null;
			if (billVo.getParentVO() != null) {
				voClz = billVo.getParentVO().getClass();
			} else {
				if (billVo.getChildrenVO() != null && billVo.getChildrenVO().length > 0) {
					voClz = billVo.getChildrenVO()[0].getClass();
				}
			}
			if (voClz != null) {
				String strClz = voClz.getName();
				strImplClz = strClz.replaceAll("\\.vo\\.", "\\.bs\\.");
				if (strImplClz.endsWith("VO")) {
					strImplClz = strImplClz.substring(0, strImplClz.length() - 2) + "PrivateAction";
				} else {
					strImplClz = strImplClz + "PrivateAction";
				}
			}
		}

		if (strImplClz != null) {
			Map<String, IPrivateAction> clzMap = BaseTimeMapFactory.getMap(IPrivateAction.class.getName());
			if (!clzMap.keySet().contains(strImplClz)) {
				try {
					Class clz = this.getClass().getClassLoader().loadClass(strImplClz);
					prvAction = (IPrivateAction) clz.newInstance();
				} catch (Exception e) {
					Logger.error(e.getMessage(), e.getCause());
				}
				clzMap.put(strImplClz, prvAction);

			} else {
				prvAction = clzMap.get(strImplClz);
			}
		}
		return prvAction;
	}
}
