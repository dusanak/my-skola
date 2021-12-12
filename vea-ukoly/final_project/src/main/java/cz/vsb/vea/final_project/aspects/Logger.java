package cz.vsb.vea.final_project.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class Logger {
    @Before("execution(* cz.vsb.vea.final_project.controllers.*.*(..)) || execution(* cz.vsb.vea.final_project.services.*.*(..))")
	public void log(JoinPoint joinPoint) {
		System.out.println(joinPoint.getSignature());
		System.out.println(Arrays.toString(joinPoint.getArgs()));
	}
}

