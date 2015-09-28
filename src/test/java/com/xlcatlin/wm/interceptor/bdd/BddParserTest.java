package com.xlcatlin.wm.interceptor.bdd;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.xlcatlin.wm.aop.Advice;
import com.xlcatlin.wm.aop.chainprocessor.AOPChainProcessor;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AOPChainProcessor.class)
public class BddParserTest {

	@Test
	public void shouldParse() throws Exception {

		PowerMockito.mockStatic(AOPChainProcessor.class);
		AOPChainProcessor mockProcessor = mock(AOPChainProcessor.class);
		PowerMockito.when(AOPChainProcessor.getInstance()).thenReturn(mockProcessor);

		InputStream bddstream = this.getClass().getResourceAsStream("/bdd/multipleReturnBdd.xml");
		Advice advice = new BddParser().parse(bddstream);
		
		assertEquals("aspect id",advice.getId());
	}
}