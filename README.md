# activiti工作流通用引擎
**项目负责人**：@lij

**镜像**：hub.docker.dvt.io:5000/devops/activiti_workflow:latest

## 以镜像方式启动
```bash
docker run -d --name awf -p 8099:8080 -v /home/devops/awf/logs:/home/tomcat/apache-tomcat-8.0.44/logs -v /home/devops/awf/deployments:/home/tomcat/ActivitiWorkflow/src/main/resources/deployments -v /home/devops/awf/application.properties:/home/tomcat/ActivitiWorkflow/src/main/resources/application.properties  hub.docker.dvt.io:5000/devops/activiti_workflow:latest
```
### 需要暴露的端口
程序访问端口 8080
### 挂载卷
日志目录 /home/tomcat/apache-tomcat-8.0.44/logs

工作流存放目录  /home/tomcat/ActivitiWorkflow/src/main/resources/deployments

数据库配置文件  /home/tomcat/ActivitiWorkflow/src/main/resources/application.properties"# ActivitiWorkflow" 
