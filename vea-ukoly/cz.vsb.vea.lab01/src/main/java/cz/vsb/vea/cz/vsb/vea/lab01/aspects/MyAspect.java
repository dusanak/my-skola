package cz.vsb.vea.cz.vsb.vea.lab01.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class MyAspect {

//    @Before("execution(* cz.vsb..*.*(..))")
//    public void log(JoinPoint joinPoint) {
//        System.out.println("Method: " + joinPoint.getSignature());
//        System.out.println("Args: " + Arrays.toString(joinPoint.getArgs()));
//    }

    @Around("execution(* cz.vsb.vea.cz.vsb.vea.lab01.services.PersonService.*(..))")
    public Object exec(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Method: " + joinPoint.getSignature());
        return joinPoint.proceed(joinPoint.getArgs());
    }
}
