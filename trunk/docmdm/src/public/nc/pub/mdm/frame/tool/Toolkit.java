package nc.pub.mdm.frame.tool;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

/**
 * 方法集合工具
 * @author 周海茂
 * @since 2011-03-22
 */
public class Toolkit {

	public static String NEW_LINE = System.getProperty("line.separator");

	public static String makeInSQL(String[] sFieldValues) {
		StringBuffer sReturnsBuffer = new StringBuffer();
		if (sFieldValues != null) {
			for (int i = 0; i < sFieldValues.length; i++) {
				sReturnsBuffer.append("'" + sFieldValues[i] + "',");
			}
		}
		sReturnsBuffer.setLength(sReturnsBuffer.length() - 1);
		return sReturnsBuffer.toString();
	}

	public static boolean isInDebug() {
		boolean b = "develop".equals(System.getProperty("nc.runMode"));
		return b;
	}

	/**
	 * @return boolean null : true<br>
	 *         String(length()==0) : true <br>
	 *         Integer(<=true) : true<br>
	 *         UFDouble(==0.0) : true<br>
	 *         Collection(value.size()<=0) : true <br>
	 *         Dictionary(value.size()<=0) : true <br>
	 *         else false
	 * @param value
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNull(Object value) {
		if (value == null)
			return true;
		if ((value instanceof StringBuffer) && (((StringBuffer) value).length() <= 0))
			return true;
		if ((value instanceof String) && ("null".equalsIgnoreCase((String) value) || ((String) value).trim().length() == 0))
			return true;
		if ((value instanceof Integer) && (((Integer) value).intValue() == 0))
			return true;
		if ((value instanceof UFDouble) && (((UFDouble) value).compareTo(new UFDouble("0.0")) == 0))
			return true;
		if ((value instanceof Object[]) && (((Object[]) value).length <= 0))
			return true;
		if ((value instanceof Collection) && ((Collection) value).size() <= 0)
			return true;
		if ((value instanceof Dictionary) && ((Dictionary) value).size() <= 0)
			return true;
		if ((value instanceof HashMap) && (((HashMap) value).size() <= 0))
			return true;
		return false;
	}

	public static boolean isWindows() {
		String os = System.getProperty("os.name");
		if (os.startsWith("Windows")) {
			return true;
		} else {
			return false;
		}
	}

	public static String makeDownloadFileName(String strFileName, int iLength, String strCharset) {
		if (iLength == -1) {
			iLength = 140;
		}
		StringBuffer sbRet = new StringBuffer();
		String strTail = "";
		try {
			String strPre = strFileName;
			int iTemp = strFileName.lastIndexOf(".");
			if (iTemp > 0) {
				strTail = strFileName.substring(iTemp);
				strTail = URLEncoder.encode(strTail, strCharset);
				iLength = iLength - strTail.getBytes().length;
				strPre = strFileName.substring(0, iTemp);
			}
			int i = 0;
			while (sbRet.length() < iLength && i < strPre.length()) {
				String strEnc;
				strEnc = URLEncoder.encode(strPre.substring(i, i + 1), strCharset);
				if (strEnc.length() + sbRet.length() > iLength) {
					sbRet.append("..");
					break;
				}
				i++;
				sbRet.append(strEnc);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		sbRet.append(strTail);
		return sbRet.toString();

	}

	public static DocVO makeHashVO(SuperVO vo, String[] attrNames) {
		DocVO retVO = new DocVO();
		if (attrNames == null || attrNames.length < 1) {
			attrNames = vo.getAttributeNames();
		}
		for (int j = 0; j < attrNames.length; j++) {
			Object objValue = null;
			try {
				objValue = vo.getAttributeValue(attrNames[j]);
			} catch (Exception e) {
				LogTool.error(e);
				continue;
			}
			retVO.setAttributeValue(attrNames[j], objValue);
		}

		retVO.setPrimaryKey( vo.getPrimaryKey() );

		return retVO;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static DocVO[] makeHashVO(List listVOs, String[] attrNames) {
		DocVO[] retVOs = null;
		Object objVO = listVOs.get(0);
		Class voClz = objVO.getClass();
		if (listVOs != null && listVOs.size() > 0) {
			if( voClz==DocVO.class){
				retVOs = new DocVO[listVOs.size()];
				listVOs.toArray(retVOs);
				
			}else{
				SuperVO[] vos = (SuperVO[]) Array.newInstance(voClz, listVOs.size());
				listVOs.toArray(vos);
				retVOs = makeHashVO(vos, attrNames);
			}
		}
		return retVOs;
	}

	public static DocVO[] makeHashVO(SuperVO[] vos, String[] attrNames) {
		DocVO[] retVOs = null;
		if (vos != null && vos.length > 0) {
			retVOs = new DocVO[vos.length];
			for (int i = 0; i < retVOs.length; i++) {
				if (attrNames == null || attrNames.length < 1) {
					attrNames = vos[0].getAttributeNames();
				}
				retVOs[i] = makeHashVO(vos[i], attrNames);
			}
		}
		return retVOs;
	}

	/**
	 * @return java.lang.String
	 */
	public static String newLine() {
		if (NEW_LINE == null) {
			NEW_LINE = System.getProperty("line.separator");
		}
		return NEW_LINE;
	}

	private Toolkit() {
		super();
	}
	

	public static int getStringIndexOfArray(final String[] ss, final String s) {
		if (ss != null) {
			for (int i = 0; i < ss.length; i++) {
				if (ss[i].equals(s))
					return i;
			}
		}
		return -1;
	}

	public static boolean isEqual(Object obj1, Object obj2){
		boolean isEqual = false;
		if( obj1 == null && obj2 == null ){
			isEqual = true;
			
		}else if( obj1 != null && obj2 != null){
			isEqual = obj1.equals(obj2);
			
		}
		return isEqual;
	}

}