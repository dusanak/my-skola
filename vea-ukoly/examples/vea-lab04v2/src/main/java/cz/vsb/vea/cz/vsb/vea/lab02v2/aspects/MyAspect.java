package cz.vsb.vea.cz.vsb.vea.lab02v2.aspects;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyAspect {

//	@Before("execution(* cz.vsb.vea.cz.vsb.vea.lab02v2.controllers.*.*(..)) || execution(* cz.vsb.vea.cz.vsb.vea.lab02v2.services.*.*(..))")
//	public void log(JoinPoint joinPoint) {
//		System.out.println(joinPoint.getSignature());
//		System.out.println(Arrays.toString(joinPoint.getArgs()));
//	}
	
	@Around("execution(* cz.vsb.vea.cz.vsb.vea.lab02v2.services.PersonService.*(..))")
	public Object hack(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.println(joinPoint.getSignature());
		System.out.println(Arrays.toString(joinPoint.getArgs()));
		if(joinPoint.getArgs().length>0 && joinPoint.getArgs()[0] instanceof Integer) {
			joinPoint.getArgs()[0] = (Integer)joinPoint.getArgs()[0]*2;
		}
		Object result = joinPoint.proceed(joinPoint.getArgs());
		if(result instanceof String) {
			result = "-- " + result + " --";
		}
		return result;
	}
	
	
}
