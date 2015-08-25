package nc.ui.mdm.base.mvc;

import nc.vo.bd.meta.BDObjectAdpaterFactory;
import nc.vo.bd.meta.IBDObject;
import nc.vo.mdm.frame.DocVO;

public class BaseBusinessObjectFatory extends BDObjectAdpaterFactory {
	@Override
	public IBDObject createBDObject(Object obj) {
		if( obj instanceof DocVO){
			return (DocVO)obj;
		}
		return super.createBDObject(obj);
	}
}
