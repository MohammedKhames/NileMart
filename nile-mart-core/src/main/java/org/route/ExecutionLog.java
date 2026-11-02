package org.route;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ExecutionLog {

    @Around("execution(* org.route.*Service.*(..))")
    public Object executeLog(ProceedingJoinPoint jp) throws Throwable {
        String op = jp.getSignature().toShortString();
        long start = System.nanoTime();
        System.out.println(op + " Started");

        try {
            Object result = jp.proceed();
            System.out.println(op + " Success (" + elapsedMs(start) + " ms)");
            return result;
        } catch (Throwable e) {
            System.out.println(op + " Failed (" + elapsedMs(start) + " ms)");
            throw e;
        }
    }

    private long elapsedMs(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }

//    @Before("execution(* org.route.ProductService.*(..))")
//    public void executeBefore() {
//        System.out.println("before");
//    }
}
