package nc.pub.mdm.proxy;

import nc.bs.trade.comsave.IQueryAfterSave;
import nc.vo.pub.AggregatedValueObject;

/**
 * 桥接器后台专用接口，从NCV5系列升级而来
 * @author 周海茂
 * @since 2012-8-28
 */
public interface IBaseBusiQuery extends IQueryAfterSave {
	public abstract void setAggVO(AggregatedValueObject aggVO);
	public abstract void setWhere(String strWhere);
}
