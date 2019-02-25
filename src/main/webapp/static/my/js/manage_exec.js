$(function(){
	$("#btn-assign").on("click",function(e){
		assign();
	});
	$("#btn-exec").on("click",function(e){
		execute();
	});
	$("#btn-jump").on("click",function(e){
		jump();
	});
	$("#btn-back").on("click",function(e){
		back();
	});
});

function assign(){
	$.ajax({
        cache: true,
        type: "POST",
        url: CTX + "/process/execute/assign",
        data: $("#form-execute").serialize(),
        async: true,
        error: function(request) {
        	alert(request + "报错");
        },
        success: function(data) {
            bootbox.alert(data.msg);
        }
    });
}

function execute(){
	$.ajax({
        cache: true,
        type: "POST",
        url: CTX + "/process/execute/complete",
        data: $("#form-execute").serialize(),
        async: true,
        error: function(request) {
        	alert(request + "报错");
        },
        success: function(data) {
        	if (data.success) {
            	if(data.data){
            		var html = "执行任务成功  </br>"
            				 + "流程实例ID：" + data.data.processInstanceId +"</br>"
            				 + "流程定义：" + data.data.processDefinitionId + "</br>"
            				 + "已完成当前任务 ： 任务ID("+data.data.taskId+") 任务代码("+data.data.taskKey+") 所处环节("+data.data.taskName+") </br>";
            		if(data.data.nextTask){
            			var nexttasks = data.data.nextTask;
            			html += "下一步任务信息：</br>";
            			for(var i=0;i<nexttasks.length;i++){
            				html += "任务ID("+nexttasks[i].nextTaskId+") 任务代码("+nexttasks[i].nextTaskKey+") 所处环节("+nexttasks[i].nextTaskName+") ";
            				nexttasks[i].nextaskAssigner?html+="委办人("+nexttasks[i].nextaskAssigner+") ":"";
            				nexttasks[i].nextaskRole?html+="委办角色("+nexttasks[i].nextaskRole+") ":"";
            				html +="</br>"
            			}
            		}
            		bootbox.alert(html);		 
            	}else{
            		bootbox.alert(data.msg);
            	}
            	
            }else{
            	bootbox.alert(data.msg);
            }
        }
    });
}
function jump(){
	$.ajax({
        cache: true,
        type: "POST",
        url: CTX + "/process/execute/jump",
        data: $("#form-execute").serialize(),
        async: true,
        error: function(request) {
        	alert(request + "报错");
        },
        success: function(data) {
        	if (data.success) {
            	if(data.data){
            		var html = "跳转任务节点成功  </br>"
            				 + "流程实例ID：" + data.data.processInstanceId +"</br>"
            				 + "流程定义：" + data.data.processDefinitionId + "</br>"
            				 + "已完成当前任务 ： 任务ID("+data.data.taskId+") 任务代码("+data.data.taskKey+") 所处环节("+data.data.taskName+") </br>";
            		if(data.data.nextTask){
            			var nexttasks = data.data.nextTask;
            			html += "下一步任务信息：</br>";
            			for(var i=0;i<nexttasks.length;i++){
            				html += "任务ID("+nexttasks[i].nextTaskId+") 任务代码("+nexttasks[i].nextTaskKey+") 所处环节("+nexttasks[i].nextTaskName+") ";
            				nexttasks[i].nextaskAssigner?html+="委办人("+nexttasks[i].nextaskAssigner+") ":"";
            				nexttasks[i].nextaskRole?html+="委办角色("+nexttasks[i].nextaskRole+") ":"";
            				html +="</br>"
            			}
            		}
            		bootbox.alert(html);		 
            	}else{
            		bootbox.alert(data.msg);
            	}
            	
            }else{
            	bootbox.alert(data.msg);
            }
        }
    });
}


function back(){
	$.ajax({
        cache: true,
        type: "POST",
        url: CTX + "/process/execute/back",
        data: $("#form-execute").serialize(),
        async: true,
        error: function(request) {
        	alert(request + "报错");
        },
        success: function(data) {
        	if (data.success) {
            	if(data.data){
            		var html = "退回上个历史任务节点成功  </br>"
            				 + "流程实例ID：" + data.data.processInstanceId +"</br>"
            				 + "流程定义：" + data.data.processDefinitionId + "</br>"
            				 + "已完成当前任务 ： 任务ID("+data.data.taskId+") 任务代码("+data.data.taskKey+") 所处环节("+data.data.taskName+") </br>";
            		if(data.data.nextTask){
            			var nexttasks = data.data.nextTask;
            			html += "下一步任务信息：</br>";
            			for(var i=0;i<nexttasks.length;i++){
            				html += "任务ID("+nexttasks[i].nextTaskId+") 任务代码("+nexttasks[i].nextTaskKey+") 所处环节("+nexttasks[i].nextTaskName+") ";
            				nexttasks[i].nextaskAssigner?html+="委办人("+nexttasks[i].nextaskAssigner+") ":"";
            				nexttasks[i].nextaskRole?html+="委办角色("+nexttasks[i].nextaskRole+") ":"";
            				html +="</br>"
            			}
            		}
            		bootbox.alert(html);		 
            	}else{
            		bootbox.alert(data.msg);
            	}
            	
            }else{
            	bootbox.alert(data.msg);
            }
        }
    });
}