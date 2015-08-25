package nc.pub.mdm.frame;

import java.io.PrintWriter;

/**
 * �쳣�������������
 * @author �ܺ�ï
 * @since 2012-8-28
 */
public class ExceptionStringWriter extends PrintWriter {

	public ExceptionStringWriter() {
		super(new ExceptionStringWriter());
	}

	public ExceptionStringWriter(int initialSize) {
		super(new ExceptionStringWriter(initialSize));
	}

	public String getString() {
		flush();
		return ((ExceptionStringWriter) super.out).toString();
	}
}