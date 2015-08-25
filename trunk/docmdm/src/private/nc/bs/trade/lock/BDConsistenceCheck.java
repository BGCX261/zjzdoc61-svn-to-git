package nc.bs.trade.lock;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * 此处插入类型说明。 创建日期：(2004-5-13 20:49:45)
 * @author 宋杰
 * 
 * @since 2012-10-16
 * @author 周海茂
 * @because nc.bs.pub.pflock.VOConsistenceCheck.checkConsistenceAry()方法居然从m_billtype中取主键<br>
 * 修改所谓的“基本档案时间戳校验”，“基本档案”本身不应该有m_billtype，所以修改本方法，规避VOConsistenceCheck.checkConsistenceAry()的错误
 */
public class BDConsistenceCheck extends nc.bs.pub.pflock.VOConsistenceCheck
{
	/**
	 * BDConsistenceCheck 构造子注解。
	 * 
	 * @param vo
	 *            nc.vo.pub.AggregatedValueObject
	 */
	public BDConsistenceCheck(nc.vo.pub.AggregatedValueObject vo)
	{
		super(vo);
	}

	/** 
	 * 检查要操作的AggregatedVO是否在数据库中存在，或者是否和当前使用的版本一致(ts) 如果不存在，throw
	 * BusinessException "该单据已经被他人删除，请刷新界面" 如果ts不一致，throw BusinessException
	 * "该单据已经被他人修改，请刷新界面，重做业务" 返回：VOID 创建日期：(2003-5-23 7:49:37) 樊冠军
	 */
	public void checkConsistence() throws BusinessException
	{

		if(this.m_vo instanceof AggregatedValueObject)
		{

			AggregatedValueObject agvo = (AggregatedValueObject) this.m_vo;
			CircularlyAccessibleValueObject headVo = agvo.getParentVO();
			CircularlyAccessibleValueObject[] itemVos = agvo.getChildrenVO();
			if (headVo != null)
				super.checkConsistence();
			else{
				// 周海茂  2012-10-16 begin
				// super.checkConsistenceAry(itemVos);
				if( itemVos!=null && itemVos.length>0 ){
					for (int i = 0; i < itemVos.length; i++) {
						if( itemVos[i]!=null ){
							m_vo = itemVos[i];
							super.checkConsistence();
						}
					}
				}
				// 周海茂  2012-10-16 end
			}
		}

	}
}