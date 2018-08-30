# cas-client

------------------------------
cas-client cas的客户端，配置在gateway上进行权限过滤

- 发布到仓库

	mvn deploy:deploy-file -DgroupId=com.vortex.cloud -DartifactId=cas-client -Dversion=1.0.0 -Dsources=target\cas-client-1.0.0-sources.jar -Dpackaging=jar -Dfile=target\cas-client-1.0.0.jar -Durl=http://222.92.212.126:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
	

- 使用方法是:

<dependency>
        <groupId>com.vortex.cloud</groupId>
        <artifactId>cas-client</artifactId>
        <version>1.0.0</version>
</dependency>

	
- changelog


# 1.0.0
cas-client cas的客户端，配置在gateway上进行权限过滤