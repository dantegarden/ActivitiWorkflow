package com.dvt.ActivitiWorkflow.commons.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class JGCGCJRolesHandler  implements TaskListener{
	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		String taskKey = delegateTask.getTaskDefinitionKey();
		if("kyyys".equals(taskKey)){
			delegateTask.addCandidateGroup("JGCGCJ_KYYYS");
		}else if("kyysp".equals(taskKey)){
			delegateTask.addCandidateGroup("JGCGCJ_KYYSP");
		}else if("rscba".equals(taskKey)){
			delegateTask.addCandidateGroup("JGCGCJ_RSCBA");
		}else if("zzbba".equals(taskKey)){
			delegateTask.addCandidateGroup("JGCGCJ_ZZBBA");
		}else if("gjcys".equals(taskKey)){
			delegateTask.addCandidateGroup("JGCGCJ_GJCYS");
		}else if("gjckzsp".equals(taskKey)){
			delegateTask.addCandidateGroup("JGCGCJ_GJCKZSP");
		}else if("gjcfczsp".equals(taskKey)){
			delegateTask.addCandidateGroup("JGCGCJ_GJCFCZSP");
		}else if("gjcczsp".equals(taskKey)){
			delegateTask.addCandidateGroup("JGCGCJ_GJCCZSP");
		}else if("fgwsfxzsp".equals(taskKey)){
			delegateTask.addCandidateGroup("JGCGCJ_FGWSFXZSP");
		}else if("xzqp".equals(taskKey)){
			delegateTask.addCandidateGroup("JGCGCJ_XZQP");
		}else if("sjqp".equals(taskKey)){
			delegateTask.addCandidateGroup("JGCGCJ_SJQP");
		}else if("gjccpj".equals(taskKey)){
			delegateTask.addCandidateGroup("JGCGCJ_GJCCPJ");
		}
	}
}
