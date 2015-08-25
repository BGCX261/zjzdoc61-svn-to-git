package nc.vo.mdm.frame;

import java.util.HashMap;
import java.util.Vector;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.trade.pub.IExAggVO;

/**
 * 主数据聚合VO<br>
 * 改动了getAllChildrenVO()方法
 * @author 周海茂
 * @since 2012-03-29
 */
public class DocAggVO extends AggregatedValueObject implements IExAggVO {
	
	private DocVO parentVO = null;
	
	private DocVO[] childVOs = null;
	
	private HashMap<String, DocVO[]> tabVOmap = null;

	private static final long serialVersionUID = -7060156489228113788L;

	@Override
	public CircularlyAccessibleValueObject[] getChildrenVO() {
		return childVOs;
	}

	@Override
	public CircularlyAccessibleValueObject getParentVO() {
		return parentVO;
	}

	@Override
	public void setChildrenVO(CircularlyAccessibleValueObject[] children) {
		this.childVOs = (DocVO[])children;
	}

	@Override
	public void setParentVO(CircularlyAccessibleValueObject parent) {
		this.parentVO = (DocVO)parent;
	}

	@Override
	public CircularlyAccessibleValueObject[] getAllChildrenVO() {
		DocVO[] vos = (DocVO[])getChildrenVO();
		if( tabVOmap!=null && tabVOmap.size()>0 ){
			Vector<DocVO> vec = new Vector<DocVO>();
			for (DocVO[] vo : tabVOmap.values()) {
				for (int i = 0; i < vo.length; i++) {
					vec.add(vo[i]);
				}
			}
			vos = new DocVO[vec.size()];
			vec.toArray(vos);
		}
		return vos;
	}

	public SuperVO[] getChildVOsByParentId(String tableCode, String parentid) {
		return null;
	}

	public String getDefaultTableCode() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	public HashMap getHmEditingVOs() throws Exception {
		return null;
	}

	public String getParentId(SuperVO item) {
		return null;
	}

	public String[] getTableCodes() {
		return null;
	}

	public String[] getTableNames() {
		return null;
	}

	public CircularlyAccessibleValueObject[] getTableVO(String tableCode) {
		return null;
	}

	public void setParentId(SuperVO item, String id) {
		
	}

	public void setTableVO(String tableCode, CircularlyAccessibleValueObject[] values) {
		
	}

}
