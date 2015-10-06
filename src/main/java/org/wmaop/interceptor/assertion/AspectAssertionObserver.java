package org.wmaop.interceptor.assertion;

import java.util.Observable;
import java.util.Observer;

import org.wmaop.aop.Advice;
import org.wmaop.aop.chainprocessor.Interceptor;
import org.wmaop.interceptor.bdd.BddInterceptor;

public class AspectAssertionObserver implements Observer {

	private AssertionManager assertionManager;

	public AspectAssertionObserver(AssertionManager assertionManager) {
		this.assertionManager = assertionManager;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		Advice advice = (Advice)arg;
		Interceptor interceptor = advice.getInterceptor();
		if (interceptor instanceof Assertion) {
			handleState(advice, (Assertion) interceptor);
		}
		if (interceptor instanceof BddInterceptor) {
			for (Interceptor icpt : ((BddInterceptor)interceptor).getInterceptorsOfType(Assertion.class)) {
				handleState(advice, (Assertion) icpt);
			}
		}
	}

	private void handleState(Advice advice, Assertion interceptor) {
		switch (advice.getAdviceState()) {
		case NEW:
			assertionManager.addAssertion(interceptor.getName(), interceptor);
			break;
		case DISPOSED:
			assertionManager.removeAssertion(interceptor.getName());
			break;
		default:
			break;
		}
	}

}
