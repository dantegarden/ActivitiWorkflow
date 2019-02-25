FROM hub.docker.dvt.io:5000/library/maven_tomcat:1.1
MAINTAINER lij lijing@bjdvt.com

# install tools
USER root

# Install Odoo
ENV GIT_USERNAME=lijing  \
        GIT_EMAIL=lijing@bjdvt.com \
        GIT_PASSWORD=asdfjkl; \
        GIT_URL=47.94.215.3:7010/yfzx/ActivitiWorkflow.git

ENV WEBAPP_NAME ActivitiWorkflow

RUN echo "#! /bin/bash" > entrypoint.sh \
        && echo "set -e" >> entrypoint.sh \
        && echo "chown -R tomcat:tomcat /home/tomcat" >> entrypoint.sh \
        && echo "su tomcat -c 'cd /home/tomcat && /bin/sh /home/tomcat/update.sh' --preserve-environment" >> entrypoint.sh \
        && echo "file=/home/tomcat/apache-tomcat-8.0.44/logs/catalina.out" >> entrypoint.sh \
        && echo 'while [ ! -f "$file" ]' >> entrypoint.sh \
        && echo 'do' >> entrypoint.sh \
        && echo '       echo "webserver is starting now, please wait........ "' >> entrypoint.sh \
        && echo 'done' >> entrypoint.sh \
        && echo 'echo "webserver has started successfully"' >> entrypoint.sh \
        && echo 'su tomcat' >> entrypoint.sh \
        && echo "tail -f /home/tomcat/apache-tomcat-8.0.44/logs/catalina.out" >> entrypoint.sh \
        && chmod +x entrypoint.sh

RUN git config --global user.name ${GIT_USERNAME} \
        && git config --global user.email  ${GIT_EMAIL}
RUN cd /home/tomcat && git clone -b master http://${GIT_USERNAME}:${GIT_PASSWORD}@${GIT_URL} \
        && echo "#!/bin/bash" > update.sh \
        && echo "project_name=${WEBAPP_NAME}" >> update.sh \
        && echo "project_path=/home/tomcat/${WEBAPP_NAME}" >> update.sh \
        && echo "tomcat_path=/home/tomcat/apache-tomcat-8.0.44" >> update.sh \
        && echo 'cd ${project_path}' >> update.sh \
        && echo "git pull" >> update.sh \
        && echo "/usr/maven/apache-maven-3.5.2/bin/mvn clean package" >> update.sh \
        && echo "ff=\`ps -ef |grep java |awk '{print \$2}'\`" >> update.sh \
        && echo 'for l in $ff' >> update.sh \
        && echo " do" >> update.sh \
        && echo '   kill -9 $l' >> update.sh \
        && echo " done" >> update.sh \
        && echo 'rm -rf ${tomcat_path}/webapps/${project_name}/*' >> update.sh \
        && echo 'mv ${project_path}/target/${project_name}-0.0.1-SNAPSHOT.war ${tomcat_path}/webapps/${project_name}/' >> update.sh \
        && echo 'cd ${tomcat_path}/webapps/${project_name}/' >> update.sh \
        && echo 'unzip ${project_name}-0.0.1-SNAPSHOT.war' >> update.sh \
        && echo 'rm -f ${project_name}-0.0.1-SNAPSHOT.war' >> update.sh \
        && echo 'sh ${tomcat_path}/bin/catalina.sh start' >> update.sh \
        && chmod +x update.sh \
        && mkdir -p /home/tomcat/apache-tomcat-8.0.44/webapps/${WEBAPP_NAME}

VOLUME ["/home/tomcat/apache-tomcat-8.0.44/logs", "/home/tomcat/ActivitiWorkflow/src/main/resources/deployments"]

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]