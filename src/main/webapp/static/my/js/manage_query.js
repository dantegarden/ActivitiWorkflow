$(function(){
	$("#btn-query").on("click",function(){
		$("#activiti-query-hist-table").closest("div.widget-box").hide();
		$("#activiti-query-table").closest("div.widget-box").show();
		query();
	});
	$("#btn-hist-query").on("click",function(){
		$("#activiti-query-table").closest("div.widget-box").hide();
		$("#activiti-query-hist-table").closest("div.widget-box").show();
		queryHist();
	});
	$("#btn-hist-query-unique").on("click",function(){
		$("#activiti-query-table").closest("div.widget-box").hide();
		$("#activiti-query-hist-table").closest("div.widget-box").show();
		queryHistUnique();
	});
});


function query(){
	$.ajax({
        cache: true,
        type: "POST",
        url: CTX + "/process/query/currentTaskQuery",
        data: {
            "processInstanceId" : $("#processInstanceId").val(),
            "userId" : $("#userId").val(),
            "roleId" : $("#roleId").val()
        },
        async: false,
        error: function(request) {
            alert("出错" + request)
        },
        success: function(data) {
        	$("#activiti-query-table tbody").html("");
        	if(data.success){
        		var list = data.data;
        		for(var i=0;i<list.length;i++){
        			var html = "<tr><td>"+ list[i].nextTaskId + "</td>"
        					 + "<td title='"+list[i].processInstaceId+"'>"+ list[i].processDefinedId + "</td>"
        					 + "<td>"+ list[i].nextTaskKey + "</td>"
        					 + "<td>"+ list[i].nextTaskName + "</td>"
        					 + "<td>"+ list[i].nextaskAssigner + "</td>"
        					 + "<td>"+ list[i].nextaskRole + "</td>"
        					 + "<td>"+ list[i].startTIme + "</td></tr>";
        			$("#activiti-query-table tbody").append(html);
        		}
        	}else{
        		bootbox.alert(data.msg);
        	}
        }
    });
	
}


function queryHist(){
	$.ajax({
        cache: true,
        type: "POST",
        url: CTX + "/process/query/histTaskQuery",
        data: {
            "processInstanceId" : $("#processInstanceId").val(),
            "userId" : $("#userId").val(),
            "roleId" : $("#roleId").val(),
            "queryType" : 1
        },
        async: false,
        error: function(request) {
            alert("出错" + request)
        },
        success: function(data) {
        	$("#activiti-query-hist-table tbody").html("");
        	if(data.success){
        		var list = data.data;
        		for(var i=0;i<list.length;i++){
        			var html = "<tr><td>"+ list[i].taskId + "</td>"
        					 + "<td title='"+list[i].processInstanceId+"'>"+ list[i].processDefinitionId + "</td>"
        					 + "<td>"+ list[i].taskKey + "</td>"
        					 + "<td>"+ list[i].taskName + "</td>"
        					 + "<td>"+ list[i].taskAssigner + "</td>"
        					 + "<td>"+ list[i].taskRoler + "</td>"
        					 + "<td>"+ list[i].createTime + "</td>"
        					 + "<td>"+ list[i].endTime + "</td></tr>";
        			$("#activiti-query-hist-table tbody").append(html);
        		}
        	}else{
        		bootbox.alert(data.msg);
        	}
        }
    });
	
}


function queryHistUnique(){
	$.ajax({
        cache: true,
        type: "POST",
        url: CTX + "/process/query/histTaskQuery",
        data: {
            "processInstanceId" : $("#processInstanceId").val(),
            "userId" : $("#userId").val(),
            "roleId" : $("#roleId").val(),
            "queryType" : 2
        },
        async: false,
        error: function(request) {
            alert("出错" + request)
        },
        success: function(data) {
        	$("#activiti-query-hist-table tbody").html("");
        	if(data.success){
        		var list = data.data;
        		for(var i=0;i<list.length;i++){
        			var html = "<tr><td>"+ list[i].taskId + "</td>"
        					 + "<td title='"+list[i].processInstanceId+"'>"+ list[i].processDefinitionId + "</td>"
        					 + "<td>"+ list[i].taskKey + "</td>"
        					 + "<td>"+ list[i].taskName + "</td>"
        					 + "<td>"+ list[i].taskAssigner + "</td>"
        					 + "<td>"+ list[i].createTime + "</td>"
        					 + "<td>"+ list[i].endTime + "</td></tr>";
        			$("#activiti-query-hist-table tbody").append(html);
        		}
        	}else{
        		bootbox.alert(data.msg);
        	}
        }
    });
}