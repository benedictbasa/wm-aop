package org.wmaop.interceptor.bdd;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.wmaop.aop.Advice;
import org.wmaop.aop.InterceptPoint;
import org.wmaop.aop.chainprocessor.Interceptor;
import org.wmaop.aop.matcher.AlwaysTrueMatcher;
import org.wmaop.aop.matcher.FlowPositionMatcher;
import org.wmaop.aop.matcher.Matcher;
import org.wmaop.aop.matcher.jexl.JexlWrappingMatcher;
import org.wmaop.aop.pointcut.ServicePipelinePointCut;

import com.wm.data.IData;
import org.wmaop.interceptor.bdd.xsd.Scenario;
import org.wmaop.interceptor.bdd.xsd.Service;
import org.wmaop.interceptor.bdd.xsd.When;

public class BddParser {

	private static final Logger logger = Logger.getLogger(BddParser.class);

	class AssertionHolder {
		String whenCondition;
		String whenid;
		String assertionid;
	}

	public Advice parse(InputStream bddstream) throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Scenario.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Scenario scenario = (Scenario) jaxbUnmarshaller.unmarshal(bddstream);
		return processAdvice(scenario);
	}

	private Advice processAdvice(Scenario scenario) {
		Interceptor interceptor = new BddInterceptor(scenario, true);
		return new Advice(scenario.getId(), getJoinPoint(scenario), interceptor);
	}

	private InterceptPoint getInterceptPoint(Scenario scenario) {
		InterceptPoint interceptPoint = InterceptPoint.valueOf(scenario.getGiven().getService().getIntercepted().toUpperCase());
		logger.info("Creating intercept point: " + interceptPoint);
		return interceptPoint;
	}

	private ServicePipelinePointCut getJoinPoint(Scenario scenario) {
		Service service = scenario.getGiven().getService();
		When when = scenario.getGiven().getWhen();

		FlowPositionMatcher flowPositionMatcher = new FlowPositionMatcher(service.getValue() + '_' + service.getIntercepted(), service.getValue());
		logger.info("Created flow position matcher: " + flowPositionMatcher);

		ServicePipelinePointCut joinPoint = new ServicePipelinePointCut(flowPositionMatcher, getMatcher(when), getInterceptPoint(scenario));
		return joinPoint;
	}

	private Matcher<? super IData> getMatcher(When when) {
		String condition = null;
		String id = null;
		if (when != null) {
			condition = when.getCondition();
			id = when.getId();
		}
		Matcher<? super IData> pipelineMatcher;
		if (condition != null && condition.length() > 0) {
			pipelineMatcher = new JexlWrappingMatcher(id, condition);
		} else {
			pipelineMatcher = new AlwaysTrueMatcher<IData>(id);
		}
		logger.info("Created pipeline matcher: " + pipelineMatcher);
		return pipelineMatcher;
	}
}
