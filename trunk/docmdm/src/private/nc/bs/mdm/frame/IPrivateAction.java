package nc.bs.mdm.frame;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.trade.pub.IBDACTION;

/**
 * @author 周海茂
 * @since 2012-8-28
 * nc.vo.crccprj.main.ProjectApproveVO<br>
 * nc.bs.crccprj.main.ProjectApproveImpl<br>
 * @see nc.bs.mdm.frame.BaseBusiChecker#dealAfter(int, AggregatedValueObject, Object)
 * @since 2012-02-08
 *
 */
public interface IPrivateAction {
	
	/**
	 * @param intBdAction
	 * @param billVo
	 * @param userObj
	 * @throws java.lang.Exception
	 * @see nc.bs.mdm.frame.BaseBusiChecker#dealAfter(int, AggregatedValueObject, Object)
	 * @see IBDACTION
	 * {@link nc.bs.trade.business.IBDBusiCheck#dealAfter(int, AggregatedValueObject, Object)}
	 * 
	 * Sample:<br>
	 * if( intBdAction == IBDACTION.SAVE){...}
	 */
	void dealAfter(int intBdAction, AggregatedValueObject billVo, Object userObj)throws java.lang.Exception;

	/**
	 * 相当于 dealBefore，见BillSave.saveBD_new()方法
	 * @param intBdAction
	 * @param billVo
	 * @param userObj
	 * @throws java.lang.Exception
	 * @see BaseBusiChecker {@link nc.bs.mdm.frame.BaseBusiChecker#check(int, AggregatedValueObject, Object)}
	 * @see IBDACTION {@link nc.bs.trade.business.IBDBusiCheck#check(int, AggregatedValueObject, Object)
	 * 
	 * Sample:<br>
	 * if( intBdAction == IBDACTION.SAVE){...}
	 */
	void check(int intBdAction, AggregatedValueObject vo, Object userObj)throws Exception;
}
