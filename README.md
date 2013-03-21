akka-spring
===========

Prototyping of an Akka app that uses Spring DI along with JSR-299 and JSR-330

How to Run
----------

Currently there is a problem running this from sbt.

    sbt
    > run-main org.typesafe.Akkaspring
    
    [error] Caused by: org.springframework.beans.factory.BeanDefinitionStoreException: Factory method [public org.springframework.instrument.classloading.LoadTimeWeaver org.springframework.context.annotation.LoadTimeWeavingConfiguration.loadTimeWeaver()] threw exception; nested exception is java.lang.IllegalStateException: ClassLoader [scala.tools.nsc.util.ScalaClassLoader$URLClassLoader] does NOT provide an 'addTransformer(ClassFileTransformer)' method. Specify a custom LoadTimeWeaver or start your Java virtual machine with Spring's agent: -javaagent:org.springframework.instrument.jar

After compiling with sbt, define the following variables to point to your directories of ivy and sbt:

    export IVY_CACHE=/Users/patrik/.ivy2/cache
    export SBT_BOOT=/Users/patrik/.sbt/0.11.3/boot

 Run from the command line:

    java -javaagent:$IVY_CACHE/org.springframework/spring-instrument/jars/spring-instrument-3.2.2.RELEASE.jar -classpath $IVY_CACHE/com.typesafe.akka/akka-actor/jars/akka-actor-2.0.1.jar:$IVY_CACHE/org.springframework/spring-context/jars/spring-context-3.2.2.RELEASE.jar:$IVY_CACHE/org.springframework/spring-aop/jars/spring-aop-3.2.2.RELEASE.jar:$IVY_CACHE/aopalliance/aopalliance/jars/aopalliance-1.0.jar:$IVY_CACHE/org.springframework/spring-beans/jars/spring-beans-3.2.2.RELEASE.jar:$IVY_CACHE/org.springframework/spring-core/jars/spring-core-3.2.2.RELEASE.jar:$IVY_CACHE/commons-logging/commons-logging/jars/commons-logging-1.1.1.jar:$IVY_CACHE/org.springframework/spring-expression/jars/spring-expression-3.2.2.RELEASE.jar:$IVY_CACHE/org.springframework/spring-aspects/jars/spring-aspects-3.2.2.RELEASE.jar:$IVY_CACHE/org.aspectj/aspectjweaver/jars/aspectjweaver-1.7.2.jar:$IVY_CACHE/org.springframework/spring-context-support/jars/spring-context-support-3.2.2.RELEASE.jar:$IVY_CACHE/javax.inject/javax.inject/jars/javax.inject-1.jar:$SBT_BOOT/scala-2.9.2/lib/scala-library.jar:./target/scala-2.9.2/classes/:./src/main/resources -Dorg.aspectj.tracing.factory=default org.typesafe.Akkaspring

    [error] Caused by: org.springframework.beans.factory.BeanDefinitionStoreException: Factory method [public org.springframework.instrument.classloading.LoadTimeWeaver org.springframework.context.annotation.LoadTimeWeavingConfiguration.loadTimeWeaver()] threw exception; nested exception is java.lang.IllegalStateException: ClassLoader [scala.tools.nsc.util.ScalaClassLoader$URLClassLoader] does NOT provide an 'addTransformer(ClassFileTransformer)' method. Specify a custom LoadTimeWeaver or start your Java virtual machine with Spring's agent: -javaagent:org.springframework.instrument.jar