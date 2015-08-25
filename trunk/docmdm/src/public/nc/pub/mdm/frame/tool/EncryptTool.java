package nc.pub.mdm.frame.tool;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.internet.MimeUtility;

import nc.bs.logging.Logger;
import nc.vo.framework.rsa.Encode;
import nc.vo.pub.BusinessException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.sun.mail.util.ASCIIUtility;

/**
 * 各种加密算法工具
 * @author 周海茂
 * @since 2011-03-22
 */
public final class EncryptTool {
	
	public static String PARSER_CHARSET = "utf-8";
	public static String PARSER_CHARSET_KEY = "charset=";

	
	private static Log log = LogFactory.getLog(EncryptTool.class.getName());
	private static Encode smUserEncode = new Encode();
	private static Cipher cipher;
	private static KeyGenerator generator = null;

	private static BASE64Encoder base64Enc = new BASE64Encoder();
	private static BASE64Decoder base64Dec = new BASE64Decoder();

	private static final String DES_DEFAULT_PWD = "webwend0";
	private final static String Cipher_Type_DES = "DES";
	private final static String Cipher_Type_RSA = "RSA";

	public static byte[] base64Decode(String s) {
		byte[] ret = null;
		if (s != null) {
			try {
				ret = base64Dec.decodeBuffer(s);
			} catch (Exception e) {
				log.fatal(e.getMessage());
			}
		}
		return ret;
	}

	public static String smUserPwdEncode(String strSrcPwd){
		String strRet = null;
		if( strSrcPwd!=null ){
			strRet = smUserEncode.encode(strSrcPwd);
		}
		return strRet;
	}
	
	public static String smUserPwdDecode(String strEncPwd){
		String strRet = null;
		if( strEncPwd!=null ){
			strRet = smUserEncode.decode(strEncPwd);
		}
		return strRet;
	}
	
	public static String base64DecodePwd(String strMi, String strPwd) {

		BASE64Decoder base64De = new BASE64Decoder();
		byte[] byteMing = null;
		byte[] byteMi = null;
		String strMing = "";
		try {
			byteMi = base64De.decodeBuffer(strMi);
			byteMing = getDesCode(byteMi, getKey(strPwd));
			strMing = new String(byteMing, "gb2312");
		} catch (Exception e) {
			log.fatal(e.getMessage());
		} finally {
			base64De = null;
			byteMing = null;
			byteMi = null;
		}
		return strMing;
	}

	public static String base64DecodeCharset(String s, String strCharset) {
		if (s == null)
			return null;
		// BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = base64Dec.decodeBuffer(s);
			if (strCharset == null) {
				return new String(b);
			}
			return new String(b, strCharset);
		} catch (Exception e) {
			log.fatal(e.getMessage());
			return null;
		}
	}

	public static String base64Encode(byte[] bs) {
		if (bs == null)
			return null;
		return base64Enc.encode(bs);
	}

	public static void base64Encode(InputStream is, OutputStream os) throws IOException {
		base64Enc.encode(is, os);
		os.flush();
	}

	public static String base64Encode(String s) {
		if (s == null)
			return null;
		return base64Enc.encode(s.getBytes());
	}

	public static String base64Encode(String strMing, String strPwd) {

		byte[] byteMi = null;
		byte[] byteMing = null;
		String strMi = "";
		BASE64Encoder base64en = new BASE64Encoder();
		try {
			byteMing = strMing.getBytes("gb2312");
			byteMi = getEncCode(byteMing, getKey(strPwd));
			strMi = base64en.encode(byteMi);
			// strMi = new String(byteMi,"UTF8");
		} catch (Exception e) {
			log.fatal(e.getMessage());
		} finally {
			base64en = null;
			byteMing = null;
			byteMi = null;
		}
		return strMi;
	}

	public static String base64EncodeCharset(String strDeWord, String strCharSet) {
		try {
			return base64Enc.encode(strDeWord.getBytes(strCharSet));
		} catch (UnsupportedEncodingException e) {
			Logger.error(e.getMessage(), e.getCause());
			return null;
		}
	}

	public static String base64EncodeCharsetInnerType(String strDeWord) {
		return base64EncodeCharsetInnerType(strDeWord, PARSER_CHARSET);
	}

	public static String base64EncodeCharsetInnerType(String strDeWord, String strCharSet) {
		String strEnc = base64EncodeCharset(strDeWord, strCharSet);
		strCharSet = "=?" + PARSER_CHARSET + "?B?";
		strEnc = strCharSet + strEnc.replace("\r\n", "?=\r\n\t" + strCharSet);
		strEnc += "?=";
		return strEnc;
	}

	public static byte[] byteFromHexString(String strHexString) {
		byte[] byteHex = null;
		try {
			byte[] byteHexString = strHexString.getBytes();
			byteHex = new byte[byteHexString.length / 2];
			for (int n = 0; n < byteHexString.length; n += 2) {
				String item = new String(byteHexString, n, 2);
				byteHex[n / 2] = (byte) Integer.parseInt(item, 16);
			}
		} catch (Exception e) {
			log.debug("Not Hex String:" + strHexString);
		}
		return byteHex;
	}

	public static String byteToHexString(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	public static String charsetDecodeInnerType(String strEnWord) {
		String strRet = strEnWord;
		if (strEnWord.startsWith("=?") && strEnWord.endsWith("?=")) {
			try {
				strRet = MimeUtility.decodeWord(strEnWord);
			} catch (Exception e) {
				log.warn("Charset decode failse." + strEnWord);
			}
		}

		return strRet;
	}

	public static String decodeQMixed(String strEnc, String strCharset, StringBuffer sbTail) {
		StringBuffer sbRet = new StringBuffer();
		for (int i = 0; i < strEnc.length();) {
			char c = strEnc.charAt(i);
			if (c != '=') {
				sbRet.append(c);
				i++;
			} else {
				// <DIV>=C3=BB=D3=D0=C4=DA=C8=DD=C5=AA=B8=F6</DIV>
				if (i + 3 < strEnc.length()) {
					String strTemp = strEnc.substring(i, i + 3);
					if (strTemp.equals("=3D")) {
						sbRet.append("=");
						i += 3;
						continue;
					}

				}
				if (i + 6 < strEnc.length()) {
					String strByteCN = strEnc.substring(i, i + 6).replaceAll("=", "");
					byte[] byteCN = byteFromHexString(strByteCN);
					try {
						sbRet.append(new String(byteCN, strCharset));
					} catch (Exception e) {
						log.warn("Charset error:" + strCharset);
						sbRet.append("=").append(strByteCN);
					}

					i += 6;
				} else {
					String strTail = strEnc.substring(i);
					if (strTail.endsWith("=")) {
						sbTail.append(strTail.subSequence(0, strTail.length() - 1));
					}
					break;
				}
			}
		}
		return sbRet.toString();
	}

	public static String charsetDecodeQ(String strEnc, String charset) throws BusinessException {
		if (charset == null) {
			charset = PARSER_CHARSET;
		}
		byte[] bts = strEnc.getBytes();
		byte[] rets = new byte[bts.length];
		int index = 0;
		for (int x = 0; x < bts.length; x++) {
			byte i = bts[x];
			if (i == 95) {
				rets[index++] = 32;
			} else if (i == 61) {
				byte[] ab = new byte[2];
				ab[0] = bts[++x];
				ab[1] = bts[++x];
				try {
					rets[index++] = (byte) ASCIIUtility.parseInt(ab, 0, 2, 16);
				} catch (NumberFormatException numberformatexception) {
					throw new BusinessException("Error in QP stream " + numberformatexception.getMessage());
				}
			} else {
				rets[index++] = i;
			}
		}

		String strRet = null;
		try {
			strRet = new String(rets, 0, index, charset);
		} catch (UnsupportedEncodingException e) {
			log.fatal(e, e.getCause());
			throw new BusinessException("Decode failed!");
		}
		return strRet;
	}

	public static byte[] decodeAES(byte[] cipherText, byte[] pwd) {
		byte[] ret = null;
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(pwd));
			SecretKey skey = kgen.generateKey();
			byte[] raw = skey.getEncoded();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			ret = cipher.doFinal(cipherText);
		} catch (Exception e) {
			log.fatal("Password Error:" + e.getMessage());
		}
		return ret;
	}

	public static byte[] decodeDES(byte[] src, byte[] pwd) throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(pwd);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Cipher_Type_DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance(Cipher_Type_DES);
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		return cipher.doFinal(src);
	}

	public final static String decodeDES(String strData) throws Exception {
		return new String(decodeDES(byteFromHexString(strData), DES_DEFAULT_PWD.getBytes()));
	}

	public static byte[] decodeRSA(byte[] byteContent, RSAPrivateKey privKey) {
		byte[] retByte = null;
		if (privKey != null && byteContent != null) {
			try {
				Cipher cipher = Cipher.getInstance(Cipher_Type_RSA);
				cipher.init(Cipher.DECRYPT_MODE, privKey);
				retByte = cipher.doFinal(byteContent);
			} catch (Exception e) {
				log.debug(e.getMessage(), e.getCause());
			}
		}
		return retByte;
	}

	public static byte[] encodeAES(byte[] plainText, byte[] pwd) {
		byte[] ret = null;
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(pwd));
			SecretKey skey = kgen.generateKey();
			byte[] raw = skey.getEncoded();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			ret = cipher.doFinal(plainText);

		} catch (Exception e) {
			log.fatal(e.getMessage());
		}
		return ret;
	}

	public static byte[] encodeDES(byte[] src, byte[] pwd) throws Exception {
		if (pwd == null) {
			pwd = DES_DEFAULT_PWD.getBytes();
		} else if (pwd.length < 8) {

		}
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(pwd);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Cipher_Type_DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance(Cipher_Type_DES);
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		return cipher.doFinal(src);
	}

	public final static String encodeDES(String strData) throws Exception {
		return byteToHexString(encodeDES(strData.getBytes(), DES_DEFAULT_PWD.getBytes()));
	}

	public static String encodeMD5(byte[] content) {
		if (content == null)
			return null;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsae) {
			Logger.error(nsae.getMessage(), nsae.getCause());
		}
		md.update(content);
		byte bytes[] = md.digest();
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hexString = Integer.toHexString(bytes[i] & 0xff);
			buff.append(hexString.length() == 2 ? hexString : "0" + hexString);
		}
		return buff.toString();
	}

	public static String encodeMD5(String content) {
		if (content == null)
			return null;
		StringBuffer buff = new StringBuffer();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsae) {
			Logger.error(nsae.getMessage(), nsae.getCause());
		}
		md.update(content.getBytes());
		byte bytes[] = md.digest();
		for (int i = 0; i < bytes.length; i++) {
			String hexString = Integer.toHexString(bytes[i] & 0xff);
			buff.append(hexString.length() == 2 ? hexString : "0" + hexString);
		}
		return buff.toString();
	}

	public static byte[] encodeRSA(byte[] byteContent, RSAPublicKey pubKey) {
		byte[] retByte = null;
		if (pubKey != null && byteContent != null) {
			try {
				Cipher cipher = Cipher.getInstance(Cipher_Type_RSA);
				cipher.init(Cipher.ENCRYPT_MODE, pubKey);
				retByte = cipher.doFinal(byteContent);
			} catch (Exception e) {
				log.fatal(e, e.getCause());
			}
		}
		return retByte;
	}

	public static String encodeSHA(String content) {

		try {
			MessageDigest sha = MessageDigest.getInstance("SHA");
			sha.update(content.getBytes());
			return new String(sha.digest());

		} catch (NoSuchAlgorithmException nsae) {
			log.fatal(nsae, nsae.getCause());
			return null;
		}
	}

	public static Cipher getCipher() {

		if (cipher == null) {
			try {
				cipher = Cipher.getInstance("DES");
			} catch (Exception e) {
				log.fatal(e, e.getCause());
			}
		}
		return cipher;
	}

	private static byte[] getDesCode(byte[] byteD, Key key) {

		byte[] byteFina = null;
		try {
			getCipher().init(Cipher.DECRYPT_MODE, key);
			byteFina = getCipher().doFinal(byteD);
		} catch (Exception e) {
			log.fatal(e, e.getCause());
		} finally {
			// cipher = null;
		}
		return byteFina;

	}

	private static byte[] getEncCode(byte[] byteS, Key key) {

		byte[] byteFina = null;
		try {
			getCipher().init(Cipher.ENCRYPT_MODE, key);
			byteFina = getCipher().doFinal(byteS);
		} catch (Exception e) {
			log.fatal(e, e.getCause());
		} finally {
			// cipher = null;
		}
		return byteFina;
	}

	public static KeyGenerator getGenerator() {

		if (generator == null) {
			try {
				generator = KeyGenerator.getInstance("DES");
			} catch (NoSuchAlgorithmException e) {
				log.fatal(e, e.getCause());
			}
		}
		return generator;
	}

	private static Key getKey(String strPwd) {

		try {
			getGenerator().init(new SecureRandom(strPwd.getBytes()));
			return getGenerator().generateKey();
		} catch (Exception e) {
			log.fatal(e, e.getCause());
		}
		return null;
	}

	public static Object readRSAKey(String strPath) {
		Object objKey = null;
		try {
			FileInputStream fis = new FileInputStream(strPath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			objKey = ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objKey;
	}
}
