package nc.pub.mdm.proxy;

import java.awt.Container;
import java.io.Serializable;

import nc.bs.framework.core.util.ObjectCreator;
import nc.bs.logging.Logger;
import nc.bs.trade.business.IBDBusiCheck;
import nc.bs.trade.comsave.IQueryAfterSave;
import nc.pub.mdm.frame.IContent;
import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.frame.tool.Toolkit;
import nc.ui.trade.businessaction.IPFACTION;
import nc.vo.jcom.util.ClassUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.trade.pub.IBDACTION;
import nc.vo.trade.pub.IBDGetCheckClass;
import nc.vo.trade.pub.IServerSideFactory;

/**
 * 后台逻辑桥接器，主要用于数据的后台校验+自定逻辑<br>
 * 本桥接器最终会将进入private端，详见getBDBusiCheckInstance()方法。
 * @author 周海茂
 * @since 2012-03-29 {@link BaseUserObject#runClass(Container, String, String, AggregatedValueObject, Object)} <br>
 *        nc.bs.ccd.frame.BaseUniqueChecker.check(int intBdAction, AggregatedValueObject vo, Object userObj)
 * 
 */
public class BaseUserObject implements IBDGetCheckClass, IServerSideFactory, Serializable {
	private static final long serialVersionUID = 9143403342343889575L;
	AggregatedValueObject billVO = null;
	IBDBusiCheck checker = null;
	String initWhere = null;
	boolean isSingleBody;
	IQueryAfterSave queryServer = null;
	String strUniqueFieldCodes = null;
	String strUniqueFieldNames = null;

	@SuppressWarnings({ "rawtypes" })
	Class[] voClzs = null;

	/**
	 * {@link nc.ui.pub.pf.IUIBeforeProcAction#runClass(Container, String, String, AggregatedValueObject, Object)}
	 */
	public BaseUserObject() {
	}

	@SuppressWarnings("rawtypes")
	public BaseUserObject(String strUniqueFieldCodes, String strUniqueFieldNames, boolean isSingleBody, Class[] voClzs) {
		this.isSingleBody = isSingleBody;
		this.strUniqueFieldCodes = strUniqueFieldCodes;
		this.strUniqueFieldNames = strUniqueFieldNames;
		this.voClzs = voClzs;
	}

	@SuppressWarnings("rawtypes")
	public Class getPrivateClass(String strClzName) throws ClassNotFoundException {
		return ClassUtil.loadClass(strClzName);
	}

	public IBDBusiCheck getBDBusiCheckInstance() {
		if (checker == null) {
			// @see PfUtilTools.instantizeObject()
			if (Toolkit.isInDebug()) {
				try {
					checker = (IBDBusiCheck) Class.forName(IContent.BusiChecker).newInstance();
				} catch (Exception e) {
					LogTool.error(e);
				}
			} else {
				checker = (IBDBusiCheck) ObjectCreator.newInstance(IContent.ModuleName, IContent.BusiChecker);
			}
			if (checker == null) {
				throw new RuntimeException("Can not found " + IContent.BusiChecker);
			}
		}
		return checker;
	}

	public AggregatedValueObject getBillVO() {
		return billVO;
	}

	public String getCheckClass() {
		return "nc.bs.mdm.frame.BaseBusiChecker";
	}

	public String getCheckQueryClass() {
		return "nc.bs.mdm.frame.BaseBusiQueryImpl";
	}

	public IBDBusiCheck getChecker() {
		return checker;
	}

	public String getInitWhere() {
		return initWhere;
	}

	public IQueryAfterSave getQueryAfterSaveInstance() {
		if (queryServer == null) {
			try {
				// @see PfUtilTools.instantizeObject()
				Object objChecker = ObjectCreator.newInstance(IContent.ModuleName, IContent.BusiQuery);
				if (objChecker == null) {
					throw new RuntimeException("Can not found " + IContent.BusiQuery);
				}

				if (objChecker instanceof IBaseBusiQuery) {
					queryServer = (IBaseBusiQuery) objChecker;
					((IBaseBusiQuery) queryServer).setAggVO(getBillVO());
					((IBaseBusiQuery) queryServer).setWhere(this.initWhere);
				}
			} catch (Exception e) {
				Logger.error(e.getMessage(), e.getCause());
			}
		}
		return queryServer;
	}

	public String getStrUniqueFieldCodes() {
		return strUniqueFieldCodes;
	}

	public String getStrUniqueFieldNames() {
		return strUniqueFieldNames;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getVoClzs() {
		return voClzs;
	}

	public boolean isSingleBody() {
		return isSingleBody;
	}

	public void runBatchClass(Container parent, String billType, String actionName, AggregatedValueObject[] vos, Object[] obj) throws Exception {
		for (int i = 0; i < vos.length; i++) {
			runClass(parent, billType, actionName, vos[i], obj[i]);
		}
	}

	/*
	 * @see nc.ui.pub.pf.IUIBeforeProcAction#runClass(java.awt.Container, java.lang.String, java.lang.String, nc.vo.pub.AggregatedValueObject, java.lang.Object)
	 */
	public void runClass(Container parent, String billType, String actionName, AggregatedValueObject vo, Object obj) throws Exception {
		IBDBusiCheck checker = getBDBusiCheckInstance();
		int iAction = 0;
		if (IPFACTION.SAVE.equals(actionName) || IPFACTION.COMMIT.equals(actionName)) {
			iAction = IBDACTION.SAVE;
			checker.check(iAction, vo, obj);
		}

	}

	public void setBillVO(AggregatedValueObject billVO) {
		this.billVO = billVO;
	}

	public void setChecker(IBDBusiCheck checker) {
		this.checker = checker;
	}

	public void setInitWhere(String initWhere) {
		this.initWhere = initWhere;
	}

	public void setQueryServer(IQueryAfterSave queryServer) {
		this.queryServer = queryServer;
	}

	public void setSingleBody(boolean isSingleBody) {
		this.isSingleBody = isSingleBody;
	}

	public void setStrUniqueFieldCodes(String strUniqueFieldCodes) {
		this.strUniqueFieldCodes = strUniqueFieldCodes;
	}

	public void setStrUniqueFieldNames(String strUniqueFieldNames) {
		this.strUniqueFieldNames = strUniqueFieldNames;
	}

	@SuppressWarnings({ "rawtypes" })
	public void setVoClzs(Class[] voClzs) {
		this.voClzs = voClzs;
	}
}
