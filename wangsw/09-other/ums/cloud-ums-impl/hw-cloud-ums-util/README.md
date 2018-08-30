# hw-cloud-ums-util

------------------------------
一些简单的工具类，BaseController，配合oauth2调用的调用方法等

- 发布到仓库

	mvn deploy:deploy-file -DgroupId=com.vortex.cloud -DartifactId=hw-cloud-ums-util -Dversion=1.0.0 -Dsources=target\hw-cloud-ums-util-1.0.0-sources.jar -Dpackaging=jar -Dfile=target\hw-cloud-ums-util-1.0.0.jar -Durl=http://222.92.212.126:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
	

- 使用方法是:

<dependency>
        <groupId>com.vortex.cloud</groupId>
        <artifactId>hw-cloud-ums-util</artifactId>
        <version>1.0.0</version>
</dependency>

	
- changelog


# 1.0.0
以前hw-core中现在vfs没有的方法，以及BaseController。