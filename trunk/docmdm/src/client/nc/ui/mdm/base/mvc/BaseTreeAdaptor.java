package nc.ui.mdm.base.mvc;

import nc.vo.bd.meta.CAVO2BDObject;
import nc.vo.bd.meta.IBDObject;
import nc.vo.bd.meta.IBDObjectAdapterFactory;
import nc.vo.mdm.frame.DocVO;

public class BaseTreeAdaptor implements IBDObjectAdapterFactory{

	@Override
	public IBDObject createBDObject(Object obj) {
		IBDObject retObj = null;
		if( obj instanceof DocVO){
			DocVO dv = (DocVO)obj;
			retObj = new CAVO2BDObject( dv, dv.getPrimaryKeyField(),"vcode","vname", "pk_parent", null, null);
		}
		return retObj;
	}
}
