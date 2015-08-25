package nc.pub.mdm.frame;

import java.io.PrintWriter;

/**
 * 异常处理输出工具类
 * @author 周海茂
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