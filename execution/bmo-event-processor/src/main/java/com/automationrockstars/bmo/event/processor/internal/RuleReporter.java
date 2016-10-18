package com.automationrockstars.bmo.event.processor.internal;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.bmo.event.processor.HttpEventUtils.RequestProducer;
import com.automationrockstars.bmo.event.processor.Message;
import com.automationrockstars.bmo.event.processor.annotations.ExchangeReceiver;
import com.automationrockstars.bmo.event.processor.annotations.ExchangeReceivers;
import com.automationrockstars.bmo.event.processor.annotations.ExchangeSender;
import com.automationrockstars.bmo.event.processor.annotations.ExchangeSenders;
import com.automationrockstars.bmo.event.processor.annotations.HttpReceiver;
import com.automationrockstars.bmo.event.processor.annotations.HttpReceivers;
import com.automationrockstars.bmo.event.processor.annotations.HttpSender;
import com.automationrockstars.bmo.event.processor.annotations.HttpSenders;
import com.automationrockstars.bmo.event.processor.annotations.Rule;
import com.automationrockstars.gunter.rabbit.Publisher;
import com.automationrockstars.gunter.rabbit.RabbitEventBroker;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import eu.infomas.annotation.AnnotationDetector;
import eu.infomas.annotation.AnnotationDetector.MethodReporter;

public class RuleReporter implements MethodReporter{

	private static final List<Method> rules = Lists.newCopyOnWriteArrayList();
	private static final FluentIterable<Method> validRules = FluentIterable.from(rules);

	private static final Logger LOG = LoggerFactory.getLogger(RuleReporter.class);  
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Annotation>[] annotations() {
		return new Class[] {Rule.class};
	}

	@Override
	public void reportMethodAnnotation(Class<? extends Annotation> annotation, final String className, final String methodName) {

		try {
			FluentIterable<Method> rule = FluentIterable.from(Lists.newArrayList(Class.forName(className).getMethods())).filter(new Predicate<Method>(){
				@Override
				public boolean apply(Method input) {
					if (input.getName().equals(methodName) && input.getAnnotation(Rule.class) != null){
						try {
							Preconditions.checkArgument(
									isExchangeReceiver(input) || isHttpReceiver(input),
									"Rule needs to have at least one receiver");

							Preconditions.checkArgument(
									input.getAnnotation(ExchangeSender.class) != null || 
									input.getAnnotation(HttpSender.class) != null || 
									(input.getAnnotation(HttpSenders.class) != null && input.getAnnotation(HttpSenders.class).value().length > 0) ||
									(input.getAnnotation(ExchangeSenders.class) != null && input.getAnnotation(ExchangeSenders.class).value().length > 0),
									"Rule needs to have at least one sender");

							Preconditions.checkArgument(Message.class.isAssignableFrom(input.getReturnType()),"Rule return type needs to be a Message");
							Preconditions.checkArgument(input.getParameterTypes().length == 1 && Message.class.isAssignableFrom(input.getParameterTypes()[0]),"Rule needs to accept exactly one argument of type Message");

							return true;
						} catch (Throwable t){
							LOG.error("Rule {} on method {} from {} cannot be create due to ",input.getAnnotation(Rule.class).value(),methodName,className,t);
							return false;
						}
					} else return false;
				}});
			rule.copyInto(rules);
		} catch (Throwable e) {
			LOG.error("Rule on method {} from {} cannot be create due to ",methodName,className,e);
		}

	}

	private static void init() {
		try {
			if (! detectionCompleted){
				new AnnotationDetector(new RuleReporter()).detect();
				detectionCompleted = true;
			}
			LOG.info("Found rules:\n{}",Joiner.on("\n").join(validRules.transform(new Function<Method,String>(){

				@Override
				public String apply(Method input) {
					return input.getAnnotation(Rule.class).value();
				}})));
		} catch (IOException e) {
			LOG.error("Exception detecting rules",e);
		}
	}
	private static boolean detectionCompleted = false;
	public static synchronized FluentIterable<Method> validRules(){
		init();			
		return validRules;
	}

	public static boolean isExchangeReceiver(Method input){
		return input.getAnnotation(ExchangeReceiver.class) != null || 
				(input.getAnnotation(ExchangeReceivers.class) != null && input.getAnnotation(ExchangeReceivers.class).value().length > 0);
	}

	public static boolean isHttpReceiver(Method input){
		return input.getAnnotation(HttpReceiver.class) != null || 
				(input.getAnnotation(HttpReceivers.class) != null && input.getAnnotation(HttpReceivers.class).value().length > 0) ;
	}

	private static Publisher publisherForAnnotation(ExchangeSender senderInfo){
		return RabbitEventBroker.publisher(senderInfo.value(), senderInfo.key());
	}


	private static Map<String,List<Publisher>> publishers = Maps.newHashMap();
	private static synchronized void initializePublishers(String ruleName, Method input){
		List<Publisher> rulePublishers = Lists.newArrayList();
		ExchangeSender senderInfo;
		if ((senderInfo = input.getAnnotation(ExchangeSender.class)) != null){
			rulePublishers.add(publisherForAnnotation(senderInfo));
		}
		ExchangeSenders senders;
		if ((senders = input.getAnnotation(ExchangeSenders.class)) != null){
			for (ExchangeSender senderInf : Lists.newArrayList(senders.value())){
				rulePublishers.add(publisherForAnnotation(senderInf));
			}
		}
		publishers.put(ruleName, rulePublishers);
	}
	public static synchronized FluentIterable<Publisher> getExchangeSenders(Method input){
		String ruleName = input.getAnnotation(Rule.class).value();
		if (publishers.get(ruleName) == null){
			initializePublishers(ruleName, input);
		}
		return FluentIterable.from(publishers.get(ruleName));
	}

	private static Map<String,List<RequestProducer>> producers = Maps.newHashMap();
	private static synchronized void initializeProducers(String ruleName, Method input){
		List<RequestProducer> ruleProducers = Lists.newArrayList();
		HttpSender senderInfo;
		if((senderInfo = input.getAnnotation(HttpSender.class))!=null){
			ruleProducers.add(RequestProducer.create(senderInfo));
		}
		HttpSenders senders;
		if ((senders = input.getAnnotation(HttpSenders.class))!=null){
			for (HttpSender senderInf : Lists.newArrayList(senders.value())){
				ruleProducers.add(RequestProducer.create(senderInf));
			}
		}
		producers.put(ruleName, ruleProducers);
	}
	public static FluentIterable<RequestProducer> getHttpSenders(Method input){
		String ruleName = input.getAnnotation(Rule.class).value();
		if (producers.get(ruleName) == null){
			initializeProducers(ruleName,input);
		}
		return FluentIterable.from(producers.get(ruleName));
	}

	public static FluentIterable<HttpReceiver> getHttpReceivers(Method input){
		return getReceivers(input, HttpReceiver.class, HttpReceivers.class);
	}

	public static FluentIterable<ExchangeReceiver> getExchangeReceivers(Method input){
		return getReceivers(input, ExchangeReceiver.class, ExchangeReceivers.class);
	}

	@SuppressWarnings("unchecked")
	private static <T> FluentIterable<T> getReceivers(Method method,final Class<? extends  Annotation> singleReceiverAnnotation,final Class<? extends Annotation> multipleReceiverAnnoation){
		List<T> receivers = Lists.newArrayList();
		T receiver = null;
		if ((receiver = (T) method.getAnnotation(singleReceiverAnnotation))!= null){
			receivers.add(receiver);
		}
		Object ruleReceivers = null;
		if ((ruleReceivers = method.getAnnotation(multipleReceiverAnnoation))!= null){
				Object manyReceivers = null;
				try {
					if (multipleReceiverAnnoation.isAssignableFrom(HttpReceivers.class)){
						manyReceivers = ((HttpReceivers)ruleReceivers).value();
					} else if (multipleReceiverAnnoation.isAssignableFrom(ExchangeReceivers.class)){
						manyReceivers = ((ExchangeReceivers)ruleReceivers).value();
					}
					for (T ruleReceiver : (T[])manyReceivers){
						receivers.add(ruleReceiver);
					}
				} catch (Throwable t) {
					LOG.error("Cannot get multiple receivers for {} due to ",method,t);
				}			
			}
		
		return FluentIterable.from(receivers);

	}


}
