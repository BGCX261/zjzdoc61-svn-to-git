package nc.ui.mdm.ref.busi;

import nc.ui.bd.ref.busi.MDClassDefaultTreeModel;

public class MDEntityRefModel extends MDClassDefaultTreeModel {

	public MDEntityRefModel() {
		super();
		setFieldCode(new String[] { "md_component.name", "md_class.displayname" });
		String oldwhere=getWherePart();
		setWherePart(oldwhere+" and md_component.ownmodule='docmdm' and "
				+"(md_component.displayname like 'DOC%' or md_component.displayname like 'BD%' or md_component.displayname like 'UN%')");
	}


}
