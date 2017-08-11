package kr.ac.kgu.se;

import org.apache.log4j.Logger;
import org.junit.Test;

public class LogTest {
	//static Logger logger = LogMgr.getInstance(LogTest.class, "logtest");
	static Logger slogger = LogMgr.getInstance("Serverlog");
	static Logger clogger = LogMgr.getInstance("Clientlog");
	
	static Logger slogger2 = Logger.getLogger("s2");
	static Logger clogger2 = Logger.getLogger("c2");
	
	
	@Test
	public void test() {
		
		slogger.info("SV Message!");
		
		clogger.info("CL Message!");
		
		slogger2.info("SV Message!");
		
		clogger2.info("CL Message!");
	}

}
